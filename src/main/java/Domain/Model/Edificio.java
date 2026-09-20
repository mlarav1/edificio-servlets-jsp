package Domain.Model;

import java.math.BigDecimal;

/** Entidad Edificio (ejercicio 13). Los tipos coinciden con las columnas de la tabla. */
public class Edificio {
    private int id;
    private String nombre;
    private BigDecimal metrosCuadrados;
    private BigDecimal altura;
    private int numPisos;
    private int numApartamentos;
    private int numOficinas;
    private String nombreParqueadero;
    private int numPiscinas;
    private String pais;
    private String departamento;
    private String ciudad;
    private boolean tieneAscensor;
    private BigDecimal valorAdministracion;
    private boolean tieneZonaSocial;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(BigDecimal v) { this.metrosCuadrados = v; }
    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }
    public int getNumPisos() { return numPisos; }
    public void setNumPisos(int numPisos) { this.numPisos = numPisos; }
    public int getNumApartamentos() { return numApartamentos; }
    public void setNumApartamentos(int v) { this.numApartamentos = v; }
    public int getNumOficinas() { return numOficinas; }
    public void setNumOficinas(int v) { this.numOficinas = v; }
    public String getNombreParqueadero() { return nombreParqueadero; }
    public void setNombreParqueadero(String v) { this.nombreParqueadero = v; }
    public int getNumPiscinas() { return numPiscinas; }
    public void setNumPiscinas(int v) { this.numPiscinas = v; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String v) { this.departamento = v; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public boolean isTieneAscensor() { return tieneAscensor; }
    public void setTieneAscensor(boolean v) { this.tieneAscensor = v; }
    public BigDecimal getValorAdministracion() { return valorAdministracion; }
    public void setValorAdministracion(BigDecimal v) { this.valorAdministracion = v; }
    public boolean isTieneZonaSocial() { return tieneZonaSocial; }
    public void setTieneZonaSocial(boolean v) { this.tieneZonaSocial = v; }
}
