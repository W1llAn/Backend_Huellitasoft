package huellitassoft_web.huellitasoft.dto.specie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta para una especie de animal")
public class SpecieResponseDTO {

    @Schema(
            description = "ID único de la especie",
            example = "1"
    )
    private Long idEspecie;

    @Schema(
            description = "Nombre común de la especie",
            example = "Perro"
    )
    private String nombre;
}
