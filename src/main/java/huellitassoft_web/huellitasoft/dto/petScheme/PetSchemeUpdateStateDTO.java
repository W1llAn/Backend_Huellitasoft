package huellitassoft_web.huellitasoft.dto.petScheme;

import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para actualizar únicamente el estado de la relación mascota↔esquema")
public class PetSchemeUpdateStateDTO {

    @Schema(description = "Nuevo estado del seguimiento",
            example = "COMPLETADO",
            allowableValues = {"ACTIVO", "COMPLETADO", "CANCELADO"})
    private PetSchemeState estado;
}