package huellitassoft_web.huellitasoft.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Maneja excepciones de recurso no encontrado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.error("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de recurso ya existente.
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, WebRequest request) {

        log.error("Recurso ya existe: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Maneja errores de validación de datos de entrada.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.error("Error de validación: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Error de validación");
        response.put(TIMESTAMP, LocalDateTime.now().format(formatter));
        response.put("errors", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones genéricas no capturadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Error no esperado: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Error interno del servidor")
                .timestamp(LocalDateTime.now().format(formatter))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja violaciones de integridad de datos (Foreign Keys, Unique Constraints, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {

        log.error("Violación de integridad de datos: {}", ex.getMessage());

        String message = "No se puede completar la operación debido a restricciones de integridad";
        String exceptionMessage = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        // Detectar el tipo específico de violación
        if (exceptionMessage.contains("fk_mascota_raza")) {
            message = "No se puede eliminar la raza porque está asignada a una o más mascotas";
        } else if (exceptionMessage.contains("fk_raza_especie")) {
            message = "No se puede eliminar la especie porque tiene razas asociadas";
        } else if (exceptionMessage.contains("fk_mascota_cliente")) {
            message = "No se puede eliminar el cliente porque tiene mascotas registradas";
        } else if (exceptionMessage.contains("foreign key")) {
            message = "No se puede eliminar este registro porque está siendo utilizado por otros datos";
        } else if (exceptionMessage.contains("unique constraint") || exceptionMessage.contains("duplicate key")) {
            message = "Ya existe un registro con estos datos";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(message)
                .timestamp(LocalDateTime.now().format(formatter))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
