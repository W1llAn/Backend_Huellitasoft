package huellitassoft_web.huellitasoft.dto.race;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para crear una nueva raza de animal")
public class RaceCreateDTO {

    @NotNull(message = "El nombre de la raza es obligatorio")
    @NotBlank(message = "El nombre de la raza no puede estar vacío")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    @Schema(
            description = "Nombre de la raza",
            example = "Labrador Retriever",
            minLength = 2,
            maxLength = 150
    )
    private String nombre;


    @NotNull(message = "El ID de la especie es obligatorio")
    @Schema(
            description = "ID de la especie a la que pertenece esta raza",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idEspecie;
}
