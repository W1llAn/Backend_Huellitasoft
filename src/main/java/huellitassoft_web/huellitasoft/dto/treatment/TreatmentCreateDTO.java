package huellitassoft_web.huellitasoft.dto.treatment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para crear un nuevo tratamiento")
public class TreatmentCreateDTO {
    @NotNull(message = "El ID de la consulta no puede ser nulo")
    @Schema(description = "ID de la consulta asociada", example = "1")
    private Long idConsulta;

    @NotNull(message = "El ID de la mascota no puede ser nulo")
    @Schema(description = "ID de la mascota", example = "5")
    private Long idMascota;


    @NotNull(message = "La duración en días no puede ser nula")
    @Positive(message = "La duración debe ser mayor a 0")
    @Schema(description = "Duración del tratamiento en días", example = "14")
    private Integer duracionDias;

    @Schema(description = "Observaciones adicionales", example = "Aplicar cada 12 horas")
    private String observaciones;

    @NotNull(message = "El estado no puede ser nulo")
    @Schema(description = "Estado del tratamiento", example = "true")
    private Boolean estado;
}
