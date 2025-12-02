package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationResponseDTO;
import huellitassoft_web.huellitasoft.enums.NotificationTitle;
import huellitassoft_web.huellitasoft.service.impl.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocketService Integration Tests")
class WebSocketServiceIntegrationTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketService webSocketService;

    private NotificationResponseDTO testNotification;

    @BeforeEach
    void setUp() {
        testNotification = NotificationResponseDTO.builder()
                .idNotificacion(1L)
                .titulo(NotificationTitle.CITA_CREADA)
                .asunto("Nueva Cita")
                .mensaje("Se ha creado una nueva cita")
                .tipo("CITA")
                .nombreCliente("Juan Pérez")
                .emailCliente("juan@test.com")
                .nombreVeterinario("Dr. Veterinario")
                .emailVeterinario("vet@test.com")
                .leida(false)
                .enviadaEmail(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe enviar notificación a cliente específico por WebSocket")
    void testSendNotificationToClient_Success() {
        // Given
        Long clientId = 123L;
        String expectedDestination = "/topic/notifications/client/" + clientId;

        // When
        webSocketService.sendNotificationToClient(clientId, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar notificación a veterinario específico por WebSocket")
    void testSendNotificationToVeterinarian_Success() {
        // Given
        Long veterinarianId = 456L;
        String expectedDestination = "/topic/notifications/veterinarian/" + veterinarianId;

        // When
        webSocketService.sendNotificationToVeterinarian(veterinarianId, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar notificación broadcast a todos los usuarios")
    void testBroadcastNotification_Success() {
        // Given
        String expectedDestination = "/topic/notifications/all";

        // When
        webSocketService.broadcastNotification(testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar múltiples notificaciones a diferentes clientes")
    void testSendNotificationToClient_MultipleClients() {
        // Given
        Long clientId1 = 1L;
        Long clientId2 = 2L;
        Long clientId3 = 3L;

        // When
        webSocketService.sendNotificationToClient(clientId1, testNotification);
        webSocketService.sendNotificationToClient(clientId2, testNotification);
        webSocketService.sendNotificationToClient(clientId3, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/client/1"), eq(testNotification));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/client/2"), eq(testNotification));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/client/3"), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar múltiples notificaciones a diferentes veterinarios")
    void testSendNotificationToVeterinarian_MultipleVeterinarians() {
        // Given
        Long vetId1 = 10L;
        Long vetId2 = 20L;

        // When
        webSocketService.sendNotificationToVeterinarian(vetId1, testNotification);
        webSocketService.sendNotificationToVeterinarian(vetId2, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/veterinarian/10"), eq(testNotification));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/veterinarian/20"), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar notificación con diferentes tipos de contenido")
    void testSendDifferentNotificationTypes() {
        // Given
        Long clientId = 100L;
        
        NotificationResponseDTO citaCreada = NotificationResponseDTO.builder()
                .idNotificacion(1L)
                .titulo(NotificationTitle.CITA_CREADA)
                .asunto("Nueva Cita")
                .build();
        
        NotificationResponseDTO citaCancelada = NotificationResponseDTO.builder()
                .idNotificacion(2L)
                .titulo(NotificationTitle.CITA_CANCELADA)
                .asunto("Cita Cancelada")
                .build();
        
        NotificationResponseDTO recordatorio = NotificationResponseDTO.builder()
                .idNotificacion(3L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .asunto("Recordatorio")
                .build();

        // When
        webSocketService.sendNotificationToClient(clientId, citaCreada);
        webSocketService.sendNotificationToClient(clientId, citaCancelada);
        webSocketService.sendNotificationToClient(clientId, recordatorio);

        // Then
        verify(messagingTemplate, times(3)).convertAndSend(eq("/topic/notifications/client/100"), any(NotificationResponseDTO.class));
    }

    @Test
    @DisplayName("Debe manejar envío de notificaciones a cliente con ID 0")
    void testSendNotificationToClient_WithZeroId() {
        // Given
        Long clientId = 0L;
        String expectedDestination = "/topic/notifications/client/0";

        // When
        webSocketService.sendNotificationToClient(clientId, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }

    @Test
    @DisplayName("Debe manejar broadcast múltiple")
    void testBroadcastNotification_MultipleTimes() {
        // Given
        String expectedDestination = "/topic/notifications/all";

        // When
        webSocketService.broadcastNotification(testNotification);
        webSocketService.broadcastNotification(testNotification);
        webSocketService.broadcastNotification(testNotification);

        // Then
        verify(messagingTemplate, times(3)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }

    @Test
    @DisplayName("Debe combinar diferentes tipos de envío en una sesión")
    void testMixedNotificationSending() {
        // Given
        Long clientId = 1L;
        Long vetId = 2L;

        // When
        webSocketService.sendNotificationToClient(clientId, testNotification);
        webSocketService.sendNotificationToVeterinarian(vetId, testNotification);
        webSocketService.broadcastNotification(testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/client/1"), eq(testNotification));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/veterinarian/2"), eq(testNotification));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/notifications/all"), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar notificación con ID de cliente muy grande")
    void testSendNotificationToClient_WithLargeId() {
        // Given
        Long clientId = 999999999L;
        String expectedDestination = "/topic/notifications/client/" + clientId;

        // When
        webSocketService.sendNotificationToClient(clientId, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }

    @Test
    @DisplayName("Debe enviar notificación con ID de veterinario negativo")
    void testSendNotificationToVeterinarian_WithNegativeId() {
        // Given
        Long vetId = -1L;
        String expectedDestination = "/topic/notifications/veterinarian/" + vetId;

        // When
        webSocketService.sendNotificationToVeterinarian(vetId, testNotification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSend(eq(expectedDestination), eq(testNotification));
    }
}
