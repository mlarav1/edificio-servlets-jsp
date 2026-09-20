package Infrastructure.Persistence;

import Domain.Model.Usuario;
import Infrastructure.Database.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de Usuario. Todas las consultas usan PreparedStatement
 * (parametros "?") y try-with-resources para cerrar la conexion.
 */
public class UsuarioDAO {

    private static final String COLUMNAS = "id, clave, nombre, rol";

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getString("id"), rs.getString("clave"),
                rs.getString("nombre"), rs.getString("rol"));
    }

    public List<Usuario> listar() throws SQLException {
        return consultar("SELECT " + COLUMNAS + " FROM usuario ORDER BY nombre");
    }

    public Usuario buscarPorId(String id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuario WHERE LOWER(id) = LOWER(?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (id, clave, nombre, rol) VALUES (?, ?, ?, ?)";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getId());
            ps.setString(2, u.getClave());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getRol());
            ps.executeUpdate();
        }
    }

    /** Actualiza nombre y rol (la clave se cambia con actualizarClave). */
    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, rol = ? WHERE id = ?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getRol());
            ps.setString(3, u.getId());
            ps.executeUpdate();
        }
    }

    public void actualizarClave(String id, String claveHash) throws SQLException {
        String sql = "UPDATE usuario SET clave = ? WHERE id = ?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, claveHash);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminar(String id) throws SQLException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM usuario WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    public int contarAdmins() throws SQLException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM usuario WHERE rol = 'ADMIN'");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Reporte 1 de Usuario: usuarios de un rol determinado. */
    public List<Usuario> reportePorRol(String rol) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuario WHERE rol = ? ORDER BY nombre";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Reporte 2 de Usuario: texto contenido en el nombre o en el correo/dominio. */
    public List<Usuario> reportePorTexto(String texto) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuario "
                + "WHERE LOWER(nombre) LIKE ? OR LOWER(id) LIKE ? ORDER BY nombre";
        String patron = "%" + texto.toLowerCase() + "%";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private List<Usuario> consultar(String sql) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
}
