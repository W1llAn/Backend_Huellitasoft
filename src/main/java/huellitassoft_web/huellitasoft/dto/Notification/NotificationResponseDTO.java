package huellitassoft_web.huellitasoft.dto.Notification;

import huellitassoft_web.huellitasoft.enums.NotificationTitle;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private Long idNotificacion;
    private NotificationTitle titulo;
    private String asunto;
    private String mensaje;
    private String tipo;
    private String nombreCliente;
    private String emailCliente;
    private String nombreVeterinario;
    private String emailVeterinario;
    private Boolean leida;
    private Boolean enviadaEmail;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvioEmail;
}
