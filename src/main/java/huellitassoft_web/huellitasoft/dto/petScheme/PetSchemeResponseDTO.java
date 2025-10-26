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
@Schema(description = "DTO de respuesta para la relación mascota↔esquema de vacunación")
public class PetSchemeResponseDTO {

    @Schema(description = "ID de la relación mascota↔esquema", example = "1001")
    private Long idMascotaEsquema;

    @Schema(description = "ID de la mascota", example = "10")
    private Long idMascota;

    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombreMascota;

    @Schema(description = "ID del esquema de vacunación", example = "5")
    private Long idEsquema;

    @Schema(description = "ID de la vacuna asociada al esquema", example = "1")
    private Long idVacuna;

    @Schema(description = "Nombre de la vacuna asociada", example = "Parvovirus")
    private String nombreVacuna;

    @Schema(description = "Número de dosis del esquema", example = "2")
    private Integer dosisNumero;

    @Schema(description = "Edad recomendada (en semanas) para esta dosis", example = "12")
    private Integer edadSemanas;

    @Schema(description = "Estado actual del seguimiento",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "COMPLETADO", "CANCELADO"})
    private PetSchemeState estado;
}