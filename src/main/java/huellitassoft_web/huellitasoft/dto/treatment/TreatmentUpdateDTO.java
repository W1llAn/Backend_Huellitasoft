package huellitassoft_web.huellitasoft.dto.treatment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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

    @Positive(message = "La duración debe ser mayor a 0")
    @Schema(description = "Duración del tratamiento en días", example = "14")
    private Integer duracionDias;

    @Schema(description = "Observaciones adicionales", example = "Aplicar cada 12 horas")
    private String observaciones;

    @Schema(description = "Estado del tratamiento", example = "true")
    private Boolean estado;
}
