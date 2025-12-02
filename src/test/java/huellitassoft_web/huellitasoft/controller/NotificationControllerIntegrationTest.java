package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.Notification.NotificationRequestDTO;
import huellitassoft_web.huellitasoft.dto.Notification.NotificationResponseDTO;
import huellitassoft_web.huellitasoft.enums.NotificationTitle;
import huellitassoft_web.huellitasoft.service.impl.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para NotificationController
 */
@WebMvcTest(NotificationController.class)
@DisplayName("NotificationController Integration Tests")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private NotificationService notificationService;

    private NotificationResponseDTO notificationResponseDTO;
    private NotificationRequestDTO notificationRequestDTO;

    @BeforeEach
    void setUp() {
        notificationResponseDTO = NotificationResponseDTO.builder()
                .idNotificacion(1L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .asunto("Recordatorio de cita")
                .mensaje("Tiene una cita programada para mañana")
                .tipo("CITA")
                .fechaCreacion(LocalDateTime.now())
                .leida(false)
                .enviadaEmail(true)
                .nombreCliente("Juan Pérez")
                .emailCliente("juan@example.com")
                .nombreVeterinario("Dr. López")
                .emailVeterinario("drlopez@example.com")
                .build();

        notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setTitulo(NotificationTitle.CITA_RECORDATORIO);
        notificationRequestDTO.setAsunto("Recordatorio de cita");
        notificationRequestDTO.setMensaje("Tiene una cita programada para mañana");
        notificationRequestDTO.setTipo("CITA");
        notificationRequestDTO.setIdCliente(1L);
        notificationRequestDTO.setIdVeterinario(2L);
    }

    @Test
    @DisplayName("POST /api/notifications - Debe crear una notificación")
    @WithMockUser(roles = "VETERINARIO")
    void testCreateNotification_Success() throws Exception {
        // Given
        when(notificationService.createNotification(any(NotificationRequestDTO.class)))
                .thenReturn(notificationResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/notifications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idNotificacion", is(1)))
                .andExpect(jsonPath("$.asunto", is("Recordatorio de cita")))
                .andExpect(jsonPath("$.leida", is(false)));

        verify(notificationService, times(1)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/notifications - Debe listar todas las notificaciones")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetAllNotifications_Success() throws Exception {
        // Given
        List<NotificationResponseDTO> notifications = Arrays.asList(notificationResponseDTO);
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].asunto", is("Recordatorio de cita")));

        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    @DisplayName("GET /api/notifications/{id} - Debe obtener notificación por ID")
    @WithMockUser(roles = "CLIENTE")
    void testGetNotificationById_Success() throws Exception {
        // Given
        when(notificationService.getNotificationById(anyLong())).thenReturn(notificationResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/notifications/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNotificacion", is(1)))
                .andExpect(jsonPath("$.asunto", is("Recordatorio de cita")));

        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    @DisplayName("GET /api/notifications/client/{clientId} - Debe listar notificaciones por cliente")
    @WithMockUser(roles = "CLIENTE")
    void testGetNotificationsByClient_Success() throws Exception {
        // Given
        List<NotificationResponseDTO> notifications = Arrays.asList(notificationResponseDTO);
        when(notificationService.getNotificationsByClient(anyLong())).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/api/notifications/client/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreCliente", is("Juan Pérez")));

        verify(notificationService, times(1)).getNotificationsByClient(1L);
    }

    @Test
    @DisplayName("GET /api/notifications/veterinarian/{veterinarianId} - Debe listar notificaciones por veterinario")
    @WithMockUser(roles = "VETERINARIO")
    void testGetNotificationsByVeterinarian_Success() throws Exception {
        // Given
        List<NotificationResponseDTO> notifications = Arrays.asList(notificationResponseDTO);
        when(notificationService.getNotificationsByVeterinarian(anyLong())).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/api/notifications/veterinarian/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreVeterinario", is("Dr. López")));

        verify(notificationService, times(1)).getNotificationsByVeterinarian(2L);
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/mark-read - Debe marcar notificación como leída")
    @WithMockUser(roles = "CLIENTE")
    void testMarkNotificationAsRead_Success() throws Exception {
        // Given
        NotificationResponseDTO readNotification = NotificationResponseDTO.builder()
                .idNotificacion(1L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .asunto("Recordatorio de cita")
                .mensaje("Tiene una cita programada para mañana")
                .tipo("CITA")
                .fechaCreacion(LocalDateTime.now())
                .leida(true)
                .enviadaEmail(true)
                .nombreCliente("Juan Pérez")
                .emailCliente("juan@example.com")
                .nombreVeterinario("Dr. López")
                .emailVeterinario("drlopez@example.com")
                .build();
        when(notificationService.markAsRead(anyLong())).thenReturn(readNotification);

        // When & Then
        mockMvc.perform(patch("/api/notifications/1/read")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida", is(true)));

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    @DisplayName("DELETE /api/notifications/{id} - Debe eliminar notificación")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteNotification_Success() throws Exception {
        // Given
        doNothing().when(notificationService).deleteNotification(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/notifications/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).deleteNotification(1L);
    }
}
