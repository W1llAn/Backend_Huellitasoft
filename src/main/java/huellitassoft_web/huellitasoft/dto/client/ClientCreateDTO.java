package huellitassoft_web.huellitasoft.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo cliente.
 * 
 * Contiene validaciones de datos requeridos para la creación de un cliente.
 * Incluye el ID del usuario que será la cuenta del cliente.
 * 
 * @author Backend Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDTO {

    @Schema(
            description = "Nombres completos del cliente",
            example = "Juan Pedro",
            minLength = 2,
            maxLength = 100
    )
    @NotNull(message = "El nombre no puede ser nulo")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombres;

    @Schema(
            description = "Apellidos completos del cliente",
            example = "García López",
            minLength = 2,
            maxLength = 100
    )
    @NotNull(message = "Los apellidos no pueden ser nulos")
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    @Schema(
            description = "Documento de identidad del cliente (cédula, pasaporte, etc.)",
            example = "1234567890",
            minLength = 5,
            maxLength = 20
    )
    @NotNull(message = "El documento de identidad no puede ser nulo")
    @NotBlank(message = "El documento de identidad no puede estar vacío")
    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    @Pattern(regexp = "^[0-9a-zA-Z-]+$", message = "El documento debe contener solo números, letras y guiones")
    private String documentoIdentidad;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "juan.garcia@example.com"
    )
    @NotNull(message = "El email no puede ser nulo")
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    @Schema(
            description = "Número telefónico del cliente",
            example = "+57 312 456 7890",
            minLength = 7,
            maxLength = 20
    )
    @NotNull(message = "El teléfono no puede ser nulo")
    @NotBlank(message = "El teléfono no puede estar vacío")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s()-]{7,20}$", message = "El teléfono debe ser válido")
    private String telefono;

    @Schema(
            description = "Dirección física del cliente",
            example = "Calle 10 #20-30, Apartamento 301",
            maxLength = 255
    )
    @NotNull(message = "La dirección no puede ser nula")
    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(min = 5, max = 255, message = "La dirección debe tener entre 5 y 255 caracteres")
    private String direccion;

    @Schema(
            description = "ID del usuario asociado al cliente (debe tener rol CLIENTE)",
            example = "5"
    )
    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Integer idUsuario;
}
