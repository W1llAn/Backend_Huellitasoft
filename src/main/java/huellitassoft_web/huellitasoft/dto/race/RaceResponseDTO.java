package huellitassoft_web.huellitasoft.dto.race;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta para una raza de animal")
public class RaceResponseDTO {

    @Schema(
            description = "ID único de la raza",
            example = "1"
    )
    private Long idRaza;

    @Schema(
            description = "Nombre de la raza",
            example = "Labrador Retriever"
    )
    private String nombre;


    @Schema(
            description = "ID de la especie a la que pertenece",
            example = "1"
    )
    private Long idEspecie;

    @Schema(
            description = "Nombre de la especie",
            example = "Canis familiaris"
    )
    private String nombreEspecie;
}
