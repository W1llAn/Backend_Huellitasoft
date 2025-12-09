package huellitassoft_web.huellitasoft.dto.user;

import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private Long idUsuario;
    private String email;
    private String username;
    private UserRol rol;
    private UserState estado;
    private LocalDateTime fechaCreacion;
    private Long creadoPorId;
    private String creadoPorUsername;
    private Long idSucursal;
    private String sucursalNombre;
    private String imagen;

    // Información Personal
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;

    // Información Profesional
    private String especialidad;
    private String numeroLicencia;
    private Integer aniosExperiencia;
    private String biografia;
}
