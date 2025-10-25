package huellitassoft_web.huellitasoft.dto.consultation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta para consulta veterinaria")
public class ConsultationResponseDTO {
    @Schema(description = "ID único de la consulta", example = "1")
    private Long idConsulta;

    @Schema(description = "ID del historial clínico", example = "1")
    private Long idHistoria;

    @Schema(description = "Fecha y hora de la consulta", example = "2025-10-24T14:30:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Motivo de la consulta", example = "Vacunación anual")
    private String motivo;

    @Schema(description = "ID del veterinario", example = "3")
    private Integer idVeterinario;

    @Schema(description = "Nombre del veterinario", example = "Dr. Juan Pérez")
    private String nombreVeterinario;

    @Schema(description = "Diagnóstico de la consulta", example = "Mascota en buen estado de salud")
    private String diagnostico;

    @Schema(description = "Indicaciones y recomendaciones", example = "Seguimiento en 6 meses")
    private String indicaciones;

    @Schema(description = "Cantidad de tratamientos asociados", example = "2")
    private Integer totalTratamientos;
}
