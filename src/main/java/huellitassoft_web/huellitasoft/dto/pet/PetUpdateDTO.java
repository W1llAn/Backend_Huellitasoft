package huellitassoft_web.huellitasoft.dto.pet;

import huellitassoft_web.huellitasoft.enums.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para actualizar información de una mascota")
public class PetUpdateDTO {
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @PastOrPresent(message = "La fecha de nacimiento debe ser en el pasado o el presente")
    private LocalDate fechaNacimiento;

    private Sex sexo;

    private Boolean estado;

    private String imagen;

    private String observaciones;

    private Boolean eliminado;

    private Long idCliente;

    private Long idRaza;
}
