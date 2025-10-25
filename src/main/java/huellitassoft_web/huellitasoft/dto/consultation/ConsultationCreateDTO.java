package huellitassoft_web.huellitasoft.dto.consultation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para crear una nueva consulta veterinaria")
public class ConsultationCreateDTO {
    @NotNull(message = "El ID del historial clínico no puede ser nulo")
    @Schema(description = "ID del historial clínico", example = "1")
    private Long idHistoria;

    @NotNull(message = "La fecha y hora de la consulta no pueden ser nulas")
    @Schema(description = "Fecha y hora de la consulta", example = "2025-10-24T14:30:00")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo de la consulta no puede estar vacío")
    @Schema(description = "Motivo de la consulta", example = "Vacunación anual")
    private String motivo;

    @NotNull(message = "El ID del veterinario no puede ser nulo")
    @Schema(description = "ID del veterinario que realiza la consulta", example = "3")
    private Integer idVeterinario;

    @Schema(description = "Diagnóstico de la consulta", example = "Mascota en buen estado de salud")
    private String diagnostico;

    @Schema(description = "Indicaciones y recomendaciones", example = "Seguimiento en 6 meses")
    private String indicaciones;
}
