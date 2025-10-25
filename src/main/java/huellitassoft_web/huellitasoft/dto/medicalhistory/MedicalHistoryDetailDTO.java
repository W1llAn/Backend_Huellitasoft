package huellitassoft_web.huellitasoft.dto.medicalhistory;

import huellitassoft_web.huellitasoft.dto.consultation.ConsultationResponseDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentResponseDTO;
import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO detallado de historial clínico con todas sus consultas y tratamientos")
public class MedicalHistoryDetailDTO {
    @Schema(description = "ID único del historial clínico", example = "1")
    private Long idHistoria;

    @Schema(description = "ID de la mascota", example = "5")
    private Long idMascota;

    @Schema(description = "Nombre de la mascota", example = "Max")
    private String nombreMascota;

    @Schema(description = "Raza de la mascota", example = "Labrador Retriever")
    private String razaMascota;

    @Schema(description = "Número del historial clínico", example = "HC-2025-001")
    private String numero;

    @Schema(description = "Estado actual del historial", example = "ACTIVO")
    private MedicalHistoryState estado;

    @Schema(description = "Lista de consultas del historial")
    private List<ConsultationResponseDTO> consultas;

    @Schema(description = "Lista de tratamientos del historial")
    private List<TreatmentResponseDTO> tratamientos;

    @Schema(description = "Cantidad total de consultas", example = "3")
    private Integer totalConsultas;

    @Schema(description = "Cantidad total de tratamientos", example = "2")
    private Integer totalTratamientos;
}
