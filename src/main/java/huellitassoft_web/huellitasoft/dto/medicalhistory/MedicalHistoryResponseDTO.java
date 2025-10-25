package huellitassoft_web.huellitasoft.dto.medicalhistory;

import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta para historial clínico (sin detalles de consultas y tratamientos)")
public class MedicalHistoryResponseDTO {
    @Schema(description = "ID único del historial clínico", example = "1")
    private Long idHistoria;

    @Schema(description = "ID de la mascota", example = "5")
    private Long idMascota;

    @Schema(description = "Nombre de la mascota", example = "Max")
    private String nombreMascota;

    @Schema(description = "Número del historial clínico", example = "HC-2025-001")
    private String numero;

    @Schema(description = "Estado actual del historial", example = "ACTIVO")
    private MedicalHistoryState estado;
}
