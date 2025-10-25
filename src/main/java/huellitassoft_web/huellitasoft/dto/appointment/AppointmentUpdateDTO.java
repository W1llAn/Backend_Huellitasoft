package huellitassoft_web.huellitasoft.dto.appointment;

import huellitassoft_web.huellitasoft.enums.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
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
@Schema(description = "DTO para actualizar una cita existente (Appointment)")
public class AppointmentUpdateDTO {
    @Future(message = "La fecha y hora deben ser futuras")
    @Schema(description = "Nueva fecha y hora de la cita", example = "2025-11-11T10:00:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Nuevo estado de la cita", example = "CONFIRMADA")
    private EstadoCita estado;

    @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
    @Schema(description = "Motivo actualizado de la cita", example = "Cambio de hora de consulta")
    private String motivo;

    @Schema(description = "Nuevo ID del veterinario (opcional)", example = "2")
    private Long idUsuario;
}
