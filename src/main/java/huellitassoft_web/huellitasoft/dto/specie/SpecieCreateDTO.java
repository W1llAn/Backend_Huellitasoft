package huellitassoft_web.huellitasoft.dto.specie;

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
@Schema(description = "DTO para crear una nueva especie de animal")
public class SpecieCreateDTO {

    @NotNull(message = "El nombre de la especie es obligatorio")
    @NotBlank(message = "El nombre de la especie no puede estar vacío")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    @Schema(
            description = "Nombre común de la especie",
            example = "Perro",
            minLength = 2,
            maxLength = 150
    )
    private String nombre;
}
