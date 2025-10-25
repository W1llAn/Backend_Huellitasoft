package huellitassoft_web.huellitasoft.dto.appointment;

import huellitassoft_web.huellitasoft.enums.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para crear una nueva cita (Appointment)")
public class AppointmentCreateDTO {
    @NotNull(message = "El ID del cliente es obligatorio")
    @Schema(description = "ID del cliente asociado", example = "1")
    private Long idCliente;

    @NotNull(message = "El ID de la mascota es obligatorio")
    @Schema(description = "ID de la mascota asociada", example = "3")
    private Long idMascota;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario (veterinario o asistente)", example = "2")
    private Long idUsuario;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    @Schema(description = "ID de la sucursal donde se realizará la cita", example = "4")
    private Long idSucursal;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha y hora deben ser mayores a la fecha y hora actual")
    @Schema(description = "Fecha y hora programada para la cita", example = "2025-11-10T15:30:00")
    private LocalDateTime fechaHora;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado actual de la cita", example = "PENDIENTE")
    private EstadoCita estado;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
    @Schema(description = "Motivo de la cita", example = "Vacunación anual")
    private String motivo;
}
