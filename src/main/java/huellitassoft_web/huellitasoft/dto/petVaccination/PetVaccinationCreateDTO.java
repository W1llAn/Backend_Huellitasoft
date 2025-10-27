package huellitassoft_web.huellitasoft.dto.petVaccination;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para registrar una vacunación en una mascota")
public class PetVaccinationCreateDTO {

    @NotNull(message = "El ID de la mascota es obligatorio")
    @Schema(description = "ID de la mascota", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idMascota;

    @NotNull(message = "El ID de la vacuna es obligatorio")
    @Schema(description = "ID de la vacuna aplicada", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idVacuna;
}
