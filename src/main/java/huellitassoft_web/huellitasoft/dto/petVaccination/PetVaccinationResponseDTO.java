package huellitassoft_web.huellitasoft.dto.petVaccination;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta para una vacunación de mascota")
public class PetVaccinationResponseDTO {

    @Schema(description = "ID único del registro de vacunación", example = "42")
    private Long idVacunacion;

    @Schema(description = "ID de la mascota vacunada", example = "10")
    private Long idMascota;

    @Schema(description = "Nombre de la mascota vacunada", example = "Rocky")
    private String nombreMascota;

    @Schema(description = "ID de la vacuna aplicada", example = "3")
    private Long idVacuna;

    @Schema(description = "Nombre de la vacuna aplicada", example = "Parvovirus")
    private String nombreVacuna;

    @Schema(
            description = "Fecha y hora en que se aplicó la vacuna (generado por el backend)",
            example = "2025-10-25T14:37:12"
    )
    private LocalDateTime fechaAplicada;

    @Schema(description = "ID del usuario (veterinario) que aplicó la vacuna", example = "7")
    private Long idUsuario;

    @Schema(description = "Usuario/username del veterinario que aplicó la vacuna", example = "dr.gomez")
    private String nombreUsuario;
}
