package huellitassoft_web.huellitasoft.dto.treatment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "DTO para crear un nuevo tratamiento")
public class TreatmentCreateDTO {

    @NotNull(message = "El ID de la consulta no puede ser nulo")
    @Schema(description = "ID de la consulta asociada", example = "1")
    private Long idConsulta;

    @NotNull(message = "El ID de la mascota no puede ser nulo")
    @Schema(description = "ID de la mascota", example = "5")
    private Long idMascota;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Schema(description = "Descripción del tratamiento", example = "Tratamiento antibiótico para infección respiratoria")
    private String description;

    @NotBlank(message = "El medicamento no puede estar vacío")
    @Size(max = 200, message = "El medicamento no puede exceder los 200 caracteres")
    @Schema(description = "Nombre del medicamento", example = "Amoxicilina")
    private String medication;

    @NotBlank(message = "La dosis no puede estar vacía")
    @Size(max = 100, message = "La dosis no puede exceder los 100 caracteres")
    @Schema(description = "Dosis del medicamento", example = "500mg")
    private String dosage;

    @NotBlank(message = "La frecuencia no puede estar vacía")
    @Size(max = 100, message = "La frecuencia no puede exceder los 100 caracteres")
    @Schema(description = "Frecuencia de administración", example = "Cada 12 horas")
    private String frequency;

    @NotNull(message = "La duración en días no puede ser nula")
    @Positive(message = "La duración debe ser mayor a 0")
    @Schema(description = "Duración del tratamiento en días", example = "14")
    private Integer durationDays;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    @Schema(description = "Observaciones adicionales", example = "Aplicar después de las comidas. Mantener refrigerado.")
    private String observations;

    @Schema(description = "Estado del tratamiento (activo/inactivo)", example = "true", defaultValue = "true")
    private Boolean status;
}
