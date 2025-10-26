package huellitassoft_web.huellitasoft.dto.vaccine;

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
@Schema(description = "DTO para crear una nueva vacuna")
public class VaccineCreateDTO {

    @NotNull(message = "El nombre de la vacuna es obligatoria")
    @NotBlank(message = "El nombre de la vacuna no puede estar vacia")
    @Size(min = 3, max = 100, message = "El nombre de la vacuna debe tener entre 3 y 150 caracteres")
    @Schema(
            description = "nombre de común de la vacuna",
            example = "Parvovirus",
            minLength = 3,
            maxLength = 100
    )
    private String nombre;

    @NotNull(message = "La descripción de la vacuna es obligatoria")
    @NotBlank(message = "La descripción de la vacuna no puede estar vacía")
    @Size(min = 3, max = 300, message = "La descripción de la vacuna debe tener entre 3 y 300 caracteres")
    @Schema(
            description = "Descripción detallada de la vacuna",
            example = "Vacuna que protege contra el virus del parvovirus canino.",
            minLength = 3,
            maxLength = 300
    )
    private String descripcion;
    @NotNull(message = "El ID de la especie es obligatorio")
    @Schema(
            description = "Id_especie a la que se asiganara la vacuna",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idEspecie;
}
