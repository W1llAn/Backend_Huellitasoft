package huellitassoft_web.huellitasoft.dto.client;

import huellitassoft_web.huellitasoft.enums.ClientState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para responder con información de un cliente.
 * 
 * Se utiliza en GET, POST, PUT para retornar datos del cliente.
 * Incluye el estado del cliente (ACTIVO/INACTIVO).
 * 
 * @author Backend Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    @Schema(
            description = "Identificador único del cliente",
            example = "1"
    )
    private Long idCliente;

    @Schema(
            description = "Nombres completos del cliente",
            example = "Juan Pedro"
    )
    private String nombres;

    @Schema(
            description = "Apellidos completos del cliente",
            example = "García López"
    )
    private String apellidos;

    @Schema(
            description = "Documento de identidad del cliente",
            example = "1234567890"
    )
    private String documentoIdentidad;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "juan.garcia@example.com"
    )
    private String email;

    @Schema(
            description = "Número telefónico del cliente",
            example = "+57 312 456 7890"
    )
    private String telefono;

    @Schema(
            description = "Dirección física del cliente",
            example = "Calle 10 #20-30, Apartamento 301"
    )
    private String direccion;

    @Schema(
            description = "Estado actual del cliente",
            example = "ACTIVO",
            implementation = ClientState.class
    )
    private ClientState estado;
}
