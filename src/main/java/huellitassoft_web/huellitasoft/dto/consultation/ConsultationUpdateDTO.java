package huellitassoft_web.huellitasoft.dto.consultation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para actualizar una consulta veterinaria")
public class ConsultationUpdateDTO {
    @NotBlank(message = "El motivo de la consulta no puede estar vacío")
    @Schema(description = "Motivo de la consulta", example = "Revisión post-operatoria")
    private String motivo;

    @Schema(description = "Diagnóstico de la consulta", example = "Recuperación satisfactoria")
    private String diagnostico;

    @Schema(description = "Indicaciones y recomendaciones", example = "Mantener reposo 2 semanas más")
    private String indicaciones;
}
