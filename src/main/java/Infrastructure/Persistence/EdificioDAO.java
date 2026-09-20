package Infrastructure.Persistence;

import Domain.Model.Edificio;
import Infrastructure.Database.Conexion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de Edificio con JDBC y PreparedStatement. */
public class EdificioDAO {

    private static final String SELECT = "SELECT id, nombre, metros_cuadrados, altura, num_pisos, "
            + "num_apartamentos, num_oficinas, nombre_parqueadero, num_piscinas, pais, departamento, "
            + "ciudad, tiene_ascensor, valor_administracion, tiene_zona_social FROM edificio ";

    private Edificio mapear(ResultSet rs) throws SQLException {
        Edificio e = new Edificio();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setMetrosCuadrados(rs.getBigDecimal("metros_cuadrados"));
        e.setAltura(rs.getBigDecimal("altura"));
        e.setNumPisos(rs.getInt("num_pisos"));
        e.setNumApartamentos(rs.getInt("num_apartamentos"));
        e.setNumOficinas(rs.getInt("num_oficinas"));
        e.setNombreParqueadero(rs.getString("nombre_parqueadero"));
        e.setNumPiscinas(rs.getInt("num_piscinas"));
        e.setPais(rs.getString("pais"));
        e.setDepartamento(rs.getString("departamento"));
        e.setCiudad(rs.getString("ciudad"));
        e.setTieneAscensor(rs.getBoolean("tiene_ascensor"));
        e.setValorAdministracion(rs.getBigDecimal("valor_administracion"));
        e.setTieneZonaSocial(rs.getBoolean("tiene_zona_social"));
        return e;
    }

    /** Asigna los 14 campos editables (sin id) a un PreparedStatement, empezando en el parametro 1. */
    private void asignar(PreparedStatement ps, Edificio e) throws SQLException {
        ps.setString(1, e.getNombre());
        ps.setBigDecimal(2, e.getMetrosCuadrados());
        ps.setBigDecimal(3, e.getAltura());
        ps.setInt(4, e.getNumPisos());
        ps.setInt(5, e.getNumApartamentos());
        ps.setInt(6, e.getNumOficinas());
        ps.setString(7, e.getNombreParqueadero());
        ps.setInt(8, e.getNumPiscinas());
        ps.setString(9, e.getPais());
        ps.setString(10, e.getDepartamento());
        ps.setString(11, e.getCiudad());
        ps.setBoolean(12, e.isTieneAscensor());
        ps.setBigDecimal(13, e.getValorAdministracion());
        ps.setBoolean(14, e.isTieneZonaSocial());
    }

    public List<Edificio> listar() throws SQLException {
        List<Edificio> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY ciudad, nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Edificio buscarPorId(int id) throws SQLException {
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public int insertar(Edificio e) throws SQLException {
        String sql = "INSERT INTO edificio (nombre, metros_cuadrados, altura, num_pisos, num_apartamentos, "
                + "num_oficinas, nombre_parqueadero, num_piscinas, pais, departamento, ciudad, "
                + "tiene_ascensor, valor_administracion, tiene_zona_social) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, e);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                return k.next() ? k.getInt(1) : 0;
            }
        }
    }

    public void actualizar(Edificio e) throws SQLException {
        String sql = "UPDATE edificio SET nombre=?, metros_cuadrados=?, altura=?, num_pisos=?, "
                + "num_apartamentos=?, num_oficinas=?, nombre_parqueadero=?, num_piscinas=?, pais=?, "
                + "departamento=?, ciudad=?, tiene_ascensor=?, valor_administracion=?, tiene_zona_social=? "
                + "WHERE id=?";
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, e);
            ps.setInt(15, e.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection c = Conexion.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM edificio WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Reporte 1 de Edificio: por ciudad (parcial, sin distinguir mayusculas) y rango de pisos. */
    public List<Edificio> reportePorCiudadYPisos(String ciudad, int pisosMin, int pisosMax) throws SQLException {
        String sql = SELECT + "WHERE LOWER(ciudad) LIKE ? AND num_pisos BETWEEN ? AND ? "
                + "ORDER BY num_pisos DESC, nombre";
        List<Edificio> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + ciudad.toLowerCase() + "%");
            ps.setInt(2, pisosMin);
            ps.setInt(3, pisosMax);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Reporte 2 de Edificio: rango de valor de administracion con filtros de ascensor y zona social.
     * Los filtros son "SI", "NO" o "" (cualquiera); se resuelven con condiciones parametrizadas.
     */
    public List<Edificio> reportePorAdministracion(BigDecimal min, BigDecimal max,
                                                   String ascensor, String zonaSocial) throws SQLException {
        String sql = SELECT + "WHERE valor_administracion BETWEEN ? AND ? "
                + "AND (? = '' OR tiene_ascensor = (? = 'SI')) "
                + "AND (? = '' OR tiene_zona_social = (? = 'SI')) "
                + "ORDER BY valor_administracion DESC";
        List<Edificio> lista = new ArrayList<>();
        try (Connection c = Conexion.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            ps.setString(3, ascensor);
            ps.setString(4, ascensor);
            ps.setString(5, zonaSocial);
            ps.setString(6, zonaSocial);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
}
