package huellitassoft_web.huellitasoft.dto.medicalhistory;

import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para crear un nuevo historial clínico")
public class MedicalHistoryCreateDTO {
    @NotNull(message = "El ID de la mascota no puede ser nulo")
    @Schema(description = "ID único de la mascota", example = "1")
    private Long idMascota;

    @NotBlank(message = "El número del historial no puede estar vacío")
    @Schema(description = "Número único del historial clínico", example = "HC-2025-001")
    private String numero;

    @NotNull(message = "El estado del historial no puede ser nulo")
    @Schema(description = "Estado inicial del historial", example = "ACTIVO")
    private MedicalHistoryState estado;
}
