package Domain.Model;

/**
 * Entidad Usuario. Tiene exactamente cuatro atributos.
 * El "id" es el correo electronico: se usa para iniciar sesion y como
 * destino de la recuperacion de clave.
 */
public class Usuario {
    public static final String ADMIN = "ADMIN";
    public static final String OPERADOR = "OPERADOR";
    public static final String CONSULTA = "CONSULTA";

    private String id;      // correo electronico
    private String clave;   // hash BCrypt (nunca texto plano)
    private String nombre;
    private String rol;

    public Usuario() { }

    public Usuario(String id, String clave, String nombre, String rol) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean esAdmin() { return ADMIN.equals(rol); }
    /** ADMIN y OPERADOR pueden modificar edificios; CONSULTA solo lee. */
    public boolean puedeEscribir() { return ADMIN.equals(rol) || OPERADOR.equals(rol); }
}
