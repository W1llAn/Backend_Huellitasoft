package huellitassoft_web.huellitasoft.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;

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

    private Long creadoPorId;

    private Long idSucursal;
}
