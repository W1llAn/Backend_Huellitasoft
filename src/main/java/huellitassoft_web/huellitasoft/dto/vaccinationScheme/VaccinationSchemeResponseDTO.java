package huellitassoft_web.huellitasoft.dto.vaccinationScheme;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta de esquema de vacunación")
public class VaccinationSchemeResponseDTO {
    @Schema(description = "ID del esquema de vacunación", example = "12")
    private Long idEsquema;

    @Schema(description = "ID de la vacuna asociada", example = "1")
    private Long idVacuna;

    @Schema(description = "Nombre de la vacuna asociada", example = "Parvovirus")
    private String nombreVacuna;

    @Schema(description = "Número de dosis dentro del esquema (secuencial por vacuna)", example = "2")
    private Integer dosisNumero;

    @Schema(description = "Edad recomendada para aplicar la dosis (en semanas)", example = "12")
    private Integer edadSemanas;

    @Schema(description = "Observaciones clínicas o instrucciones", example = "No aplicar si presenta fiebre.")
    private String observaciones;
}
