package Business.Services;

import Business.Exceptions.NegocioException;
import Domain.Model.Usuario;
import Infrastructure.Persistence.TokenDAO;
import Infrastructure.Persistence.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

/** Autenticacion y recuperacion de clave con token de un solo uso. */
public class AuthService {
    private static final int MINUTOS_VIGENCIA = 30;
    private final UsuarioDAO dao = new UsuarioDAO();
    private final TokenDAO tokens = new TokenDAO();
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
     * Genera un token aleatorio, guarda SOLO su hash SHA-256 con vencimiento y envia por correo
     * el enlace de restablecimiento. Si el correo no existe no se informa (evita enumerar usuarios).
     *
     * @param baseUrl direccion publica de la aplicacion, para armar el enlace
     */
    public void solicitarRecuperacion(String correoUsuario, String baseUrl) throws NegocioException {
        if (correoUsuario == null || correoUsuario.isBlank()) throw new NegocioException("Ingresa tu correo.");
        try {
            Usuario u = dao.buscarPorId(correoUsuario.trim());
            if (u == null) return;
            String token = generarToken();
            tokens.guardar(hash(token), u.getId(), LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA));
            String enlace = baseUrl.replaceAll("/+$", "") + "/AuthController.jsp?action=restablecer&token=" + token;
            correo.enviarRecuperacion(u.getId(), u.getNombre(), enlace, MINUTOS_VIGENCIA);
        } catch (SQLException e) {
            throw new NegocioException("Error de base de datos: " + e.getMessage(), e);
        }
    }

    /** Valida que el token exista y no haya vencido; devuelve el usuario dueño del token. */
    public Usuario validarToken(String token) throws NegocioException {
        if (token == null || token.isBlank()) throw new NegocioException("El enlace de recuperación no es válido.");
        try {
            String h = hash(token.trim());
            Object[] fila = tokens.buscar(h);
            if (fila == null) throw new NegocioException("El enlace es inválido o ya fue utilizado.");
            if (((Timestamp) fila[1]).toLocalDateTime().isBefore(LocalDateTime.now())) {
                tokens.borrar(h);
                throw new NegocioException("El enlace venció. Solicita uno nuevo.");
            }
            Usuario u = dao.buscarPorId((String) fila[0]);
            if (u == null) throw new NegocioException("El usuario no existe.");
            return u;
        } catch (SQLException e) {
            throw new NegocioException("Error de base de datos: " + e.getMessage(), e);
        }
    }

    /** Cambia la clave (guardada con BCrypt) y consume el token: solo sirve una vez. */
    public void restablecer(String token, String nueva, String confirmacion) throws NegocioException {
        if (nueva == null || nueva.length() < 6) throw new NegocioException("La clave debe tener al menos 6 caracteres.");
        if (!nueva.equals(confirmacion)) throw new NegocioException("Las claves no coinciden.");
        Usuario u = validarToken(token);
        try {
            dao.actualizarClave(u.getId(), BCrypt.hashpw(nueva, BCrypt.gensalt(10)));
            tokens.borrarDeUsuario(u.getId());
        } catch (SQLException e) {
            throw new NegocioException("Error de base de datos: " + e.getMessage(), e);
        }
    }

    /** Token de 256 bits en Base64 URL (sirve directo en el enlace). */
    static String generarToken() {
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    /** Hash SHA-256 en hexadecimal (64 caracteres). */
    static String hash(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
