package huellitassoft_web.huellitasoft.dto.vaccine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta para una vacuna")
public class VaccineResposeDTO {
    @Schema(
            description = "ID único de la vacuna",
            example = "1"
    )
    private Long idVacuna;

    @Schema(
            description = "Nombre de la vacuna",
            example = "Parvovirus"
    )
    private String nombre;

    @Schema(
            description = "Descripcion de la vacuna",
            example = "El parvovirus es otra enfermedad viral que es de cuidado, pues sus efectos pueden afectar seriamente a tu mascota, especialmente cachorros."
    )
    private String descripcion;
}
