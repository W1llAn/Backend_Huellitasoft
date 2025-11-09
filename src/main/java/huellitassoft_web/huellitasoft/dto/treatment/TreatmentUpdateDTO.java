package huellitassoft_web.huellitasoft.dto.treatment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para actualizar un tratamiento")
public class TreatmentUpdateDTO {

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Schema(description = "Descripción del tratamiento", example = "Tratamiento antibiótico para infección respiratoria")
    private String description;

    @Size(max = 200, message = "El medicamento no puede exceder los 200 caracteres")
    @Schema(description = "Nombre del medicamento", example = "Amoxicilina")
    private String medication;

    @Size(max = 100, message = "La dosis no puede exceder los 100 caracteres")
    @Schema(description = "Dosis del medicamento", example = "500mg")
    private String dosage;

    @Size(max = 100, message = "La frecuencia no puede exceder los 100 caracteres")
    @Schema(description = "Frecuencia de administración", example = "Cada 12 horas")
    private String frequency;

    @Positive(message = "La duración debe ser mayor a 0")
    @Schema(description = "Duración del tratamiento en días", example = "14")
    private Integer durationDays;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    @Schema(description = "Observaciones adicionales", example = "Aplicar después de las comidas. Mantener refrigerado.")
    private String observations;

    @Schema(description = "Estado del tratamiento (activo/inactivo)", example = "true")
    private Boolean status;
}
