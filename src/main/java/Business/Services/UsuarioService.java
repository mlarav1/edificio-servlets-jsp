package Business.Services;

import Business.Exceptions.NegocioException;
import Domain.Model.Usuario;
import Infrastructure.Persistence.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/** Logica de negocio de Usuario: validaciones, hash de clave y reglas de rol. */
public class UsuarioService {
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private final UsuarioDAO dao = new UsuarioDAO();

    public List<Usuario> listar() throws NegocioException {
        try { return dao.listar(); } catch (SQLException e) { throw error(e); }
    }

    public Usuario buscar(String id) throws NegocioException {
        try { return dao.buscarPorId(id); } catch (SQLException e) { throw error(e); }
    }

    private void validarBasico(Usuario u) throws NegocioException {
        if (u.getNombre() == null || u.getNombre().isBlank())
            throw new NegocioException("El nombre es obligatorio.");
        if (u.getNombre().length() > 100)
            throw new NegocioException("El nombre no puede superar 100 caracteres.");
        String rol = u.getRol();
        if (!Usuario.ADMIN.equals(rol) && !Usuario.OPERADOR.equals(rol) && !Usuario.CONSULTA.equals(rol))
            throw new NegocioException("El rol debe ser ADMIN, OPERADOR o CONSULTA.");
    }

    public void crear(Usuario u, String claveTextoPlano) throws NegocioException {
        if (u.getId() == null || !CORREO.matcher(u.getId().trim()).matches())
            throw new NegocioException("El id debe ser un correo electrónico válido.");
        u.setId(u.getId().trim().toLowerCase());
        validarBasico(u);
        validarClave(claveTextoPlano);
        try {
            if (dao.buscarPorId(u.getId()) != null)
                throw new NegocioException("Ya existe un usuario con el correo " + u.getId() + ".");
            u.setClave(BCrypt.hashpw(claveTextoPlano, BCrypt.gensalt(10)));
            dao.insertar(u);
        } catch (SQLException e) { throw error(e); }
    }

    /** Actualiza nombre y rol; si viene una clave nueva la guarda con hash. */
    public void actualizar(Usuario u, String claveNueva) throws NegocioException {
        validarBasico(u);
        try {
            Usuario actual = dao.buscarPorId(u.getId());
            if (actual == null) throw new NegocioException("El usuario no existe.");
            // Regla: siempre debe quedar al menos un administrador.
            if (Usuario.ADMIN.equals(actual.getRol()) && !Usuario.ADMIN.equals(u.getRol())
                    && dao.contarAdmins() <= 1)
                throw new NegocioException("Debe existir al menos un administrador.");
            dao.actualizar(u);
            if (claveNueva != null && !claveNueva.isBlank()) {
                validarClave(claveNueva);
                dao.actualizarClave(u.getId(), BCrypt.hashpw(claveNueva, BCrypt.gensalt(10)));
            }
        } catch (SQLException e) { throw error(e); }
    }

    public void eliminar(String id, String idSesion) throws NegocioException {
        if (id.equalsIgnoreCase(idSesion))
            throw new NegocioException("No puedes eliminar tu propio usuario mientras tienes la sesión abierta.");
        try {
            Usuario actual = dao.buscarPorId(id);
            if (actual == null) throw new NegocioException("El usuario no existe.");
            if (Usuario.ADMIN.equals(actual.getRol()) && dao.contarAdmins() <= 1)
                throw new NegocioException("Debe existir al menos un administrador.");
            dao.eliminar(id);
        } catch (SQLException e) { throw error(e); }
    }

    public List<Usuario> reportePorRol(String rol) throws NegocioException {
        if (rol == null || rol.isBlank()) throw new NegocioException("Selecciona un rol.");
        try { return dao.reportePorRol(rol); } catch (SQLException e) { throw error(e); }
    }

    public List<Usuario> reportePorTexto(String texto) throws NegocioException {
        if (texto == null || texto.trim().length() < 2)
            throw new NegocioException("Escribe al menos 2 caracteres para buscar.");
        try { return dao.reportePorTexto(texto.trim()); } catch (SQLException e) { throw error(e); }
    }

    private void validarClave(String clave) throws NegocioException {
        if (clave == null || clave.length() < 6)
            throw new NegocioException("La clave debe tener al menos 6 caracteres.");
    }

    private NegocioException error(SQLException e) {
        return new NegocioException("Error de base de datos: " + e.getMessage(), e);
    }
}
