package Business.Exceptions;

/** Error de regla de negocio o validacion; su mensaje se muestra al usuario. */
public class NegocioException extends Exception {
    public NegocioException(String mensaje) { super(mensaje); }
    public NegocioException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
