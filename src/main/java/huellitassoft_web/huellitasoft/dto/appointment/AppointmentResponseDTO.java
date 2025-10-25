package huellitassoft_web.huellitasoft.dto.appointment;

import huellitassoft_web.huellitasoft.enums.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para devolver la información completa de una cita (Appointment)")
public class AppointmentResponseDTO {
    @Schema(description = "ID de la cita", example = "10")
    private Long idCita;

    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
    private String nombreCliente;

    @Schema(description = "ID cliente", example = "1")
    private Long idCliente;

    @Schema(description = "ID mascota", example = "2")
    private Long idMascota;

    @Schema(description = "ID usuario", example = "2")
    private Long idUsuario;

    @Schema(description = "ID sucursal", example = "2")
    private Long idSucursal;

    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombreMascota;

    @Schema(description = "Nombre del usuario asignado (veterinario o asistente)", example = "Dr. López")
    private String nombreUsuario;

    @Schema(description = "Nombre de la sucursal", example = "Sucursal Central")
    private String nombreSucursal;

    @Schema(description = "Fecha y hora de la cita", example = "2025-11-10T15:30:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Estado actual de la cita", example = "PENDIENTE")
    private EstadoCita estado;

    @Schema(description = "Motivo de la cita", example = "Vacunación anual")
    private String motivo;
}
