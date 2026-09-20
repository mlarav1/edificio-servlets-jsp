package Business.Services;

import Business.Exceptions.NegocioException;
import Domain.Model.Edificio;
import Infrastructure.Persistence.EdificioDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/** Logica de negocio de Edificio: validaciones y conversion de parametros. */
public class EdificioService {
    private final EdificioDAO dao = new EdificioDAO();

    public List<Edificio> listar() throws NegocioException {
        try { return dao.listar(); } catch (SQLException e) { throw error(e); }
    }

    public Edificio buscar(int id) throws NegocioException {
        try { return dao.buscarPorId(id); } catch (SQLException e) { throw error(e); }
    }

    public void guardar(Edificio e) throws NegocioException {
        validar(e);
        try {
            if (e.getId() == 0) dao.insertar(e); else dao.actualizar(e);
        } catch (SQLException ex) { throw error(ex); }
    }

    public void eliminar(int id) throws NegocioException {
        try { dao.eliminar(id); } catch (SQLException e) { throw error(e); }
    }

    private void validar(Edificio e) throws NegocioException {
        if (vacio(e.getNombre())) throw new NegocioException("El nombre es obligatorio.");
        if (vacio(e.getPais()) || vacio(e.getDepartamento()) || vacio(e.getCiudad()))
            throw new NegocioException("País, departamento y ciudad son obligatorios.");
        if (e.getMetrosCuadrados() == null || e.getMetrosCuadrados().signum() <= 0)
            throw new NegocioException("Los metros cuadrados deben ser mayores que cero.");
        if (e.getAltura() == null || e.getAltura().signum() <= 0)
            throw new NegocioException("La altura debe ser mayor que cero.");
        if (e.getNumPisos() <= 0) throw new NegocioException("El número de pisos debe ser al menos 1.");
        if (e.getNumApartamentos() < 0 || e.getNumOficinas() < 0 || e.getNumPiscinas() < 0)
            throw new NegocioException("Apartamentos, oficinas y piscinas no pueden ser negativos.");
        if (e.getValorAdministracion() == null || e.getValorAdministracion().signum() < 0)
            throw new NegocioException("El valor de administración no puede ser negativo.");
        if (e.getNumPisos() > 1 && !e.isTieneAscensor() && e.getNumPisos() > 6)
            throw new NegocioException("Un edificio de más de 6 pisos debe tener ascensor.");
    }

    public List<Edificio> reportePorCiudadYPisos(String ciudad, String min, String max) throws NegocioException {
        if (vacio(ciudad)) throw new NegocioException("Escribe la ciudad.");
        int a = entero(min, "pisos mínimos");
        int b = entero(max, "pisos máximos");
        if (a > b) throw new NegocioException("Los pisos mínimos no pueden superar a los máximos.");
        try { return dao.reportePorCiudadYPisos(ciudad.trim(), a, b); } catch (SQLException e) { throw error(e); }
    }

    public List<Edificio> reportePorAdministracion(String min, String max, String ascensor, String zona)
            throws NegocioException {
        BigDecimal a = decimal(min, "valor mínimo");
        BigDecimal b = decimal(max, "valor máximo");
        if (a.compareTo(b) > 0) throw new NegocioException("El valor mínimo no puede superar al máximo.");
        try {
            return dao.reportePorAdministracion(a, b, normalizar(ascensor), normalizar(zona));
        } catch (SQLException e) { throw error(e); }
    }

    private String normalizar(String v) { return "SI".equals(v) || "NO".equals(v) ? v : ""; }

    private boolean vacio(String s) { return s == null || s.isBlank(); }

    public static int entero(String s, String campo) throws NegocioException {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { throw new NegocioException("El campo " + campo + " debe ser un número entero."); }
    }

    public static BigDecimal decimal(String s, String campo) throws NegocioException {
        try { return new BigDecimal(s.trim().replace(',', '.')); }
        catch (Exception e) { throw new NegocioException("El campo " + campo + " debe ser un número."); }
    }

    private NegocioException error(SQLException e) {
        return new NegocioException("Error de base de datos: " + e.getMessage(), e);
    }
}
