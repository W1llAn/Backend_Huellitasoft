package huellitassoft_web.huellitasoft.dto.petScheme;

import huellitassoft_web.huellitasoft.enums.PetSchemeState;
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
@Schema(description = "DTO para asignar un esquema de vacunación a una mascota")
public class PetSchemeCreateDTO {
    @NotNull(message = "El ID de la mascota es obligatorio")
    @Schema(description = "ID de la mascota a la que se asigna el esquema", example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idMascota;

    @NotNull(message = "El ID del esquema es obligatorio")
    @Schema(description = "ID del esquema de vacunación que se asigna", example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idEsquema;

    @Schema(description = "Estado inicial del seguimiento (por defecto ACTIVO)", example = "ACTIVO",
            allowableValues = {"ACTIVO", "COMPLETADO", "CANCELADO"})
    private PetSchemeState estado;
}
