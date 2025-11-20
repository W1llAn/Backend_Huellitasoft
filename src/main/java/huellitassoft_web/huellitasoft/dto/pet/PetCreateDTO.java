package huellitassoft_web.huellitasoft.dto.pet;

import huellitassoft_web.huellitasoft.enums.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "DTO para crear una nueva mascota")
public class PetCreateDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @PastOrPresent(message = "La fecha de nacimiento debe ser en el pasado o el presente")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El sexo es obligatorio")
    private Sex sexo;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    private String observaciones;

    private boolean eliminado;

    private String imagen;

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "La raza es obligatoria")
    private Long idRaza;
}
