package Infrastructure.Persistence;

import Infrastructure.Database.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Tokens de recuperacion de clave. Solo se guarda el hash SHA-256 del token, en una tabla
 * aparte para que Usuario conserve exactamente sus cuatro atributos.
 */
public class TokenDAO {

    private static volatile boolean tablaLista = false;

    /** Crea la tabla si no existe (idempotente), para no exigir un paso manual en produccion. */
    private void asegurarTabla(Connection c) throws SQLException {
        if (tablaLista) return;
        try (Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS token_recuperacion ("
                    + "token_hash VARCHAR(64) PRIMARY KEY, "
                    + "usuario_id VARCHAR(120) NOT NULL REFERENCES usuario(id) ON DELETE CASCADE, "
                    + "expira TIMESTAMP NOT NULL)");
        }
        tablaLista = true;
    }

    /** Un usuario solo tiene un enlace vigente: se borran los anteriores y se guarda el nuevo. */
    public void guardar(String tokenHash, String usuarioId, LocalDateTime expira) throws SQLException {
        try (Connection c = Conexion.obtener()) {
            asegurarTabla(c);
            borrarDeUsuario(c, usuarioId);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO token_recuperacion (token_hash, usuario_id, expira) VALUES (?, ?, ?)")) {
                ps.setString(1, tokenHash);
                ps.setString(2, usuarioId);
                ps.setTimestamp(3, Timestamp.valueOf(expira));
                ps.executeUpdate();
            }
        }
    }

    /** @return {usuario_id, expira (Timestamp)} o null si el token no existe. */
    public Object[] buscar(String tokenHash) throws SQLException {
        try (Connection c = Conexion.obtener()) {
            asegurarTabla(c);
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT usuario_id, expira FROM token_recuperacion WHERE token_hash = ?")) {
                ps.setString(1, tokenHash);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? new Object[]{rs.getString(1), rs.getTimestamp(2)} : null;
                }
            }
        }
    }

    public void borrarDeUsuario(String usuarioId) throws SQLException {
        try (Connection c = Conexion.obtener()) {
            asegurarTabla(c);
            borrarDeUsuario(c, usuarioId);
        }
    }

    private void borrarDeUsuario(Connection c, String usuarioId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM token_recuperacion WHERE usuario_id = ?")) {
            ps.setString(1, usuarioId);
            ps.executeUpdate();
        }
    }

    public void borrar(String tokenHash) throws SQLException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM token_recuperacion WHERE token_hash = ?")) {
            ps.setString(1, tokenHash);
            ps.executeUpdate();
        }
    }
}
