package huellitassoft_web.huellitasoft.dto;

import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para devolver información del usuario en las respuestas.
 * Se utiliza en los endpoints GET.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer idUsuario;
    private String email;
    private String username;
    private UserRol rol;
    private UserState estado;
    private LocalDateTime fechaCreacion;
}
