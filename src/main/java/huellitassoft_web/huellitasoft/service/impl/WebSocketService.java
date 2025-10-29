package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToClient(Long clientId, NotificationResponseDTO notification) {
        String destination = "/topic/notifications/client/" + clientId;
        messagingTemplate.convertAndSend(destination, notification);
        log.info("Notificación enviada por WebSocket al cliente: {}", clientId);
    }

    public void sendNotificationToVeterinarian(Long veterinarianId, NotificationResponseDTO notification) {
        String destination = "/topic/notifications/veterinarian/" + veterinarianId;
        messagingTemplate.convertAndSend(destination, notification);
        log.info("Notificación enviada por WebSocket al veterinario: {}", veterinarianId);
    }

    public void broadcastNotification(NotificationResponseDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications/all", notification);
        log.info("Notificación enviada por broadcast");
    }
}
