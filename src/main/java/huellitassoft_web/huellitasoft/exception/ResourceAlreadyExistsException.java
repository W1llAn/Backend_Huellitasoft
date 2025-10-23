package huellitassoft_web.huellitasoft.exception;

/**
 * Excepción lanzada cuando un recurso ya existe.
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
