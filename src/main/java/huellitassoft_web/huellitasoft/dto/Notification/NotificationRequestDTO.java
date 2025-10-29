package huellitassoft_web.huellitasoft.dto.Notification;

import huellitassoft_web.huellitasoft.enums.NotificationTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    @NotNull(message = "El título es obligatorio")
    private NotificationTitle titulo;

    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // CITA, VACUNA

    private Long idCliente;
    private Long idVeterinario;
    private Long idCita;
    private Long idVacuna;
}
