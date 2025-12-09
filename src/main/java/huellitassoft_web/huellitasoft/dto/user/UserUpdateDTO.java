package huellitassoft_web.huellitasoft.dto.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import org.w3c.dom.Text;

import java.time.LocalDate;

/**
 * DTO para actualizar un usuario.
 * Se utiliza en los endpoints PUT.
 * La contraseña es opcional - si no se proporciona, se mantiene la actual.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String contrasena;

    @NotNull(message = "El rol es requerido")
    private UserRol rol;

    @NotNull(message = "El estado es requerido")
    private UserState estado;

    private String imagen;

    // Información Personal
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Size(max = 10, message = "El tipo de documento no puede exceder 10 caracteres")
    private String tipoDocumento;

    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String numeroDocumento;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "El teléfono debe contener solo números y caracteres válidos")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    // Información Profesional
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String especialidad;

    @Size(max = 50, message = "El número de licencia no puede exceder 50 caracteres")
    private String numeroLicencia;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    @Max(value = 70, message = "Los años de experiencia no pueden exceder 70")
    private Integer aniosExperiencia;

    @Size(max = 5000, message = "La biografía no puede exceder 5000 caracteres")
    private String biografia;

    private Long creadoPorId;

    private Long idSucursal;
}
