package huellitassoft_web.huellitasoft.dto.treatment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta para tratamiento")
public class TreatmentResponseDTO {

    @Schema(description = "ID único del tratamiento", example = "1")
    private Long idTratamiento;

    @Schema(description = "ID de la consulta asociada", example = "1")
    private Long idConsulta;

    @Schema(description = "ID de la mascota", example = "5")
    private Long idMascota;

    @Schema(description = "Nombre de la mascota", example = "Max")
    private String nombreMascota;

    @Schema(description = "Descripción del tratamiento", example = "Tratamiento antibiótico para infección respiratoria")
    private String description;

    @Schema(description = "Nombre del medicamento", example = "Amoxicilina")
    private String medication;

    @Schema(description = "Dosis del medicamento", example = "500mg")
    private String dosage;

    @Schema(description = "Frecuencia de administración", example = "Cada 12 horas")
    private String frequency;

    @Schema(description = "Duración del tratamiento en días", example = "14")
    private Integer durationDays;

    @Schema(description = "Observaciones adicionales", example = "Aplicar después de las comidas. Mantener refrigerado.")
    private String observations;

    @Schema(description = "Estado del tratamiento (activo/inactivo)", example = "true")
    private Boolean status;
}
