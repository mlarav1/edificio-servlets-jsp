package Business.Services;

import Business.Exceptions.NegocioException;
import Domain.Model.Usuario;
import Infrastructure.Persistence.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;

/** Autenticacion y recuperacion de clave. */
public class AuthService {
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private final UsuarioDAO dao = new UsuarioDAO();
    private final CorreoService correo = new CorreoService();

    /** Devuelve el usuario si correo y clave son correctos; lanza NegocioException si no. */
    public Usuario autenticar(String correoUsuario, String clave) throws NegocioException {
        if (correoUsuario == null || correoUsuario.isBlank() || clave == null || clave.isEmpty())
            throw new NegocioException("Ingresa tu correo y tu clave.");
        try {
            Usuario u = dao.buscarPorId(correoUsuario.trim());
            // Mismo mensaje para "no existe" y "clave incorrecta": no revela que correos existen.
            if (u == null || !BCrypt.checkpw(clave, u.getClave()))
                throw new NegocioException("Correo o clave incorrectos.");
            return u;
        } catch (SQLException e) {
            throw new NegocioException("Error de base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Genera una clave temporal aleatoria, la guarda con hash BCrypt y la envia por correo.
     * Si el correo no existe no se informa (evita enumerar usuarios).
     */
    public void recuperarClave(String correoUsuario) throws NegocioException {
        if (correoUsuario == null || correoUsuario.isBlank())
            throw new NegocioException("Ingresa tu correo.");
        try {
            Usuario u = dao.buscarPorId(correoUsuario.trim());
            if (u == null) return;
            String temporal = generarClave(10);
            correo.enviar(u.getId(), "Recuperación de clave - Edificios",
                    "Hola " + u.getNombre() + ",\n\nTu clave temporal es: " + temporal
                            + "\n\nInicia sesión con ella y cámbiala cuanto antes.\n");
            // Se guarda solo despues de enviar, para no dejar al usuario bloqueado si falla el correo.
            dao.actualizarClave(u.getId(), BCrypt.hashpw(temporal, BCrypt.gensalt(10)));
        } catch (SQLException e) {
            throw new NegocioException("Error de base de datos: " + e.getMessage(), e);
        }
    }

    static String generarClave(int largo) {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < largo; i++) sb.append(ALFABETO.charAt(r.nextInt(ALFABETO.length())));
        return sb.toString();
    }
}
