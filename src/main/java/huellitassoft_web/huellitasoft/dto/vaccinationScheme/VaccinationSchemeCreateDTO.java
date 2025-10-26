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
@Schema(description = "DTO para crear/actualizar un esquema de vacunación")
public class VaccinationSchemeCreateDTO {
    private static final int MAX_DOSIS = 15;       // Ajustar segun el criterio de la mascota
    private static final int MAX_WEEKS = 520;     // ~10 años, borde superior prudente

    @NotNull(message = "El id de la vacuna es obligatorio")
    @Schema(description = "ID de la vacuna asociada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idVacuna;

    @NotNull
    @Min(value = 1, message = "La dosis debe ser al menos 1")
    @Max(value = MAX_DOSIS, message = "La dosis no puede exceder " + MAX_DOSIS)
    @Schema(description = "Número de dosis dentro del esquema", example = "1")
    private Integer dosisNumero;

    @NotNull
    @Min(value = 0, message = "La edad en semanas no puede ser negativa")
    @Max(value = MAX_WEEKS, message = "La edad en semanas no puede exceder " + MAX_WEEKS)
    @Schema(description = "Edad recomendada en semanas", example = "8")
    private Integer edadSemanas;

    @Size(max = 500, message = "Observaciones no debe exceder 500 caracteres")
    @Pattern(
            regexp = "^[\\p{L}0-9 .,:;()\\-_/\\n¿?¡!\"'°%@]*$",
            message = "Las observaciones contienen caracteres no permitidos (evita usar <, >, {, }, [, ], |, \\, `)"
    )
    @Schema(description = "Observaciones del esquema", example = "Aplicar solo si el cachorro está clínicamente sano.")
    private String observaciones;
}
