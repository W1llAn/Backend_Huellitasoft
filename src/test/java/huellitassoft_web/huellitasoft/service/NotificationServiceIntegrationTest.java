package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationRequestDTO;
import huellitassoft_web.huellitasoft.dto.Notification.NotificationResponseDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.*;
import huellitassoft_web.huellitasoft.repository.*;
import huellitassoft_web.huellitasoft.service.impl.EmailService;
import huellitassoft_web.huellitasoft.service.impl.NotificationService;
import huellitassoft_web.huellitasoft.service.impl.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Integration Tests")
class NotificationServiceIntegrationTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private NotificationService notificationService;

    private Client testClient;
    private User testVeterinarian;
    private Notification testNotification;
    private NotificationRequestDTO notificationRequestDTO;

    @BeforeEach
    void setUp() {
        // Usuario veterinario
        testVeterinarian = User.builder()
                .idUsuario(1L)
                .email("vet@example.com")
                .username("veterinario")
                .contrasena("password")
                .rol(UserRol.ROLE_VETERINARIO)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        // Usuario cliente
        User clientUser = User.builder()
                .idUsuario(2L)
                .email("client@example.com")
                .username("cliente")
                .contrasena("password")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        // Cliente
        testClient = Client.builder()
                .idCliente(1L)
                .usuario(clientUser)
                .documentoIdentidad("123456789")
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@test.com")
                .telefono("555-1234")
                .direccion("Calle Principal 123")
                .build();

        // Notificación
        testNotification = Notification.builder()
                .idNotificacion(1L)
                .titulo(NotificationTitle.CITA_CREADA)
                .asunto("Nueva Cita")
                .mensaje("Se ha creado una nueva cita para su mascota")
                .tipo("CITA")
                .cliente(testClient)
                .veterinario(testVeterinarian)
                .fechaCreacion(LocalDateTime.now())
                .build();
        testNotification.setLeida(false);
        testNotification.setEnviadaEmail(false);

        // DTO de creación
        notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setTitulo(NotificationTitle.CITA_CREADA);
        notificationRequestDTO.setAsunto("Nueva Cita");
        notificationRequestDTO.setMensaje("Se ha creado una nueva cita para su mascota");
        notificationRequestDTO.setTipo("CITA");
        notificationRequestDTO.setIdCliente(1L);
        notificationRequestDTO.setIdVeterinario(1L);
    }

    @Test
    @DisplayName("Debe crear una notificación exitosamente")
    void testCreateNotification_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testVeterinarian));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        doNothing().when(emailService).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
        doNothing().when(webSocketService).sendNotificationToClient(anyLong(), any(NotificationResponseDTO.class));
        doNothing().when(webSocketService).sendNotificationToVeterinarian(anyLong(), any(NotificationResponseDTO.class));

        // When
        NotificationResponseDTO result = notificationService.createNotification(notificationRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAsunto()).isEqualTo("Nueva Cita");
        verify(clientRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear notificación con cliente inexistente")
    void testCreateNotification_ClientNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(notificationRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado");

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear notificación con veterinario inexistente")
    void testCreateNotification_VeterinarianNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(notificationRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Veterinario no encontrado");

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe crear notificación con cita asociada")
    void testCreateNotification_WithAppointment() {
        // Given
        Appointment appointment = Appointment.builder()
                .idCita(1L)
                .fechaHora(LocalDateTime.now())
                .motivo("Control general")
                .estado(EstadoCita.PENDIENTE)
                .build();

        notificationRequestDTO.setIdCita(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testVeterinarian));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        doNothing().when(emailService).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
        doNothing().when(webSocketService).sendNotificationToClient(anyLong(), any(NotificationResponseDTO.class));
        doNothing().when(webSocketService).sendNotificationToVeterinarian(anyLong(), any(NotificationResponseDTO.class));

        // When
        NotificationResponseDTO result = notificationService.createNotification(notificationRequestDTO);

        // Then
        assertThat(result).isNotNull();
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe crear notificación con vacuna asociada")
    void testCreateNotification_WithVaccine() {
        // Given
        Specie specie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        Vaccine vaccine = Vaccine.builder()
                .idVacuna(1L)
                .nombre("Parvovirus")
                .descripcion("Vacuna contra parvovirus")
                .specie(specie)
                .build();

        notificationRequestDTO.setIdVacuna(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testVeterinarian));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(vaccine));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        doNothing().when(emailService).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
        doNothing().when(webSocketService).sendNotificationToClient(anyLong(), any(NotificationResponseDTO.class));
        doNothing().when(webSocketService).sendNotificationToVeterinarian(anyLong(), any(NotificationResponseDTO.class));

        // When
        NotificationResponseDTO result = notificationService.createNotification(notificationRequestDTO);

        // Then
        assertThat(result).isNotNull();
        verify(vaccineRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe obtener todas las notificaciones")
    void testGetAllNotifications_Success() {
        // Given
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(testNotification));

        // When
        List<NotificationResponseDTO> result = notificationService.getAllNotifications();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAsunto()).isEqualTo("Nueva Cita");
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener notificación por ID")
    void testGetNotificationById_Success() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // When
        NotificationResponseDTO result = notificationService.getNotificationById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdNotificacion()).isEqualTo(1L);
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar notificación inexistente")
    void testGetNotificationById_NotFound() {
        // Given
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.getNotificationById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notificación no encontrada");
    }

    @Test
    @DisplayName("Debe obtener notificaciones por cliente")
    void testGetNotificationsByClient_Success() {
        // Given
        when(notificationRepository.findByClienteIdClienteOrderByFechaCreacionDesc(1L))
                .thenReturn(Arrays.asList(testNotification));

        // When
        List<NotificationResponseDTO> result = notificationService.getNotificationsByClient(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findByClienteIdClienteOrderByFechaCreacionDesc(1L);
    }

    @Test
    @DisplayName("Debe obtener notificaciones por veterinario")
    void testGetNotificationsByVeterinarian_Success() {
        // Given
        when(notificationRepository.findByVeterinarioIdUsuarioOrderByFechaCreacionDesc(1L))
                .thenReturn(Arrays.asList(testNotification));

        // When
        List<NotificationResponseDTO> result = notificationService.getNotificationsByVeterinarian(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findByVeterinarioIdUsuarioOrderByFechaCreacionDesc(1L);
    }

    @Test
    @DisplayName("Debe obtener notificaciones no leídas por cliente")
    void testGetUnreadNotificationsByClient_Success() {
        // Given
        when(notificationRepository.findByLeidaFalseAndClienteIdCliente(1L))
                .thenReturn(Arrays.asList(testNotification));

        // When
        List<NotificationResponseDTO> result = notificationService.getUnreadNotificationsByClient(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeida()).isFalse();
        verify(notificationRepository, times(1)).findByLeidaFalseAndClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe marcar notificación como leída")
    void testMarkAsRead_Success() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        NotificationResponseDTO result = notificationService.markAsRead(1L);

        // Then
        assertThat(result).isNotNull();
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Debe eliminar notificación exitosamente")
    void testDeleteNotification_Success() {
        // Given
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        // When
        notificationService.deleteNotification(1L);

        // Then
        verify(notificationRepository, times(1)).existsById(1L);
        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar notificación inexistente")
    void testDeleteNotification_NotFound() {
        // Given
        when(notificationRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> notificationService.deleteNotification(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notificación no encontrada");

        verify(notificationRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe mapear correctamente notificación con cliente null")
    void testGetNotification_WithNullClient() {
        // Given
        Notification notificationWithoutClient = Notification.builder()
                .idNotificacion(10L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .asunto("Asunto sin cliente")
                .mensaje("Mensaje sin cliente")
                .tipo("INFO")
                .fechaCreacion(LocalDateTime.now())
                .build();
        notificationWithoutClient.setLeida(false);
        notificationWithoutClient.setEnviadaEmail(false);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notificationWithoutClient));

        // When
        NotificationResponseDTO result = notificationService.getNotificationById(10L);

        // Then
        assertThat(result.getNombreCliente()).isNull();
        assertThat(result.getEmailCliente()).isNull();
        verify(notificationRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Debe mapear correctamente notificación con veterinario null")
    void testGetNotification_WithNullVeterinarian() {
        // Given
        Notification notificationWithoutVet = Notification.builder()
                .idNotificacion(11L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .asunto("Asunto sin veterinario")
                .mensaje("Mensaje sin veterinario")
                .tipo("INFO")
                .cliente(testClient)
                .fechaCreacion(LocalDateTime.now())
                .build();
        notificationWithoutVet.setLeida(false);
        notificationWithoutVet.setEnviadaEmail(false);

        when(notificationRepository.findById(11L)).thenReturn(Optional.of(notificationWithoutVet));

        // When
        NotificationResponseDTO result = notificationService.getNotificationById(11L);

        // Then
        assertThat(result.getNombreCliente()).isNotNull();
        assertThat(result.getNombreVeterinario()).isNull();
        assertThat(result.getEmailVeterinario()).isNull();
        verify(notificationRepository, times(1)).findById(11L);
    }

    @Test
    @DisplayName("Debe mapear correctamente notificación con cliente y veterinario presentes")
    void testGetNotification_WithClientAndVeterinarian() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // When
        NotificationResponseDTO result = notificationService.getNotificationById(1L);

        // Then
        assertThat(result.getNombreCliente()).isEqualTo("Juan Pérez");
        assertThat(result.getEmailCliente()).isEqualTo("juan@test.com");
        assertThat(result.getNombreVeterinario()).isEqualTo("veterinario vet@example.com");
        assertThat(result.getEmailVeterinario()).isEqualTo("vet@example.com");
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe crear notificación con cliente null correctamente")
    void testCreateNotification_WithNullClient() {
        // Given
        NotificationRequestDTO requestDTO = new NotificationRequestDTO();
        requestDTO.setTitulo(NotificationTitle.CITA_CONFIRMADA);
        requestDTO.setAsunto("Notificación del sistema");
        requestDTO.setMensaje("Mensaje del sistema");
        requestDTO.setTipo("SISTEMA");
        requestDTO.setIdVeterinario(1L);
        // idCliente es null

        Notification savedNotification = Notification.builder()
                .idNotificacion(20L)
                .titulo(NotificationTitle.CITA_CONFIRMADA)
                .asunto("Notificación del sistema")
                .mensaje("Mensaje del sistema")
                .tipo("SISTEMA")
                .veterinario(testVeterinarian)
                .fechaCreacion(LocalDateTime.now())
                .build();
        savedNotification.setLeida(false);
        savedNotification.setEnviadaEmail(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testVeterinarian));
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // When
        NotificationResponseDTO result = notificationService.createNotification(requestDTO);

        // Then
        assertThat(result.getIdNotificacion()).isEqualTo(20L);
        assertThat(result.getNombreCliente()).isNull();
        assertThat(result.getEmailCliente()).isNull();
        verify(clientRepository, never()).findById(anyLong());
        verify(emailService, times(1)).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe crear notificación con veterinario null correctamente")
    void testCreateNotification_WithNullVeterinarian() {
        // Given
        NotificationRequestDTO requestDTO = new NotificationRequestDTO();
        requestDTO.setTitulo(NotificationTitle.CITA_RECORDATORIO);
        requestDTO.setAsunto("Recordatorio para cliente");
        requestDTO.setMensaje("Mensaje para el cliente");
        requestDTO.setTipo("RECORDATORIO");
        requestDTO.setIdCliente(1L);
        // idVeterinario es null

        Notification savedNotification = Notification.builder()
                .idNotificacion(21L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .asunto("Recordatorio para cliente")
                .mensaje("Mensaje para el cliente")
                .tipo("RECORDATORIO")
                .cliente(testClient)
                .fechaCreacion(LocalDateTime.now())
                .build();
        savedNotification.setLeida(false);
        savedNotification.setEnviadaEmail(false);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // When
        NotificationResponseDTO result = notificationService.createNotification(requestDTO);

        // Then
        assertThat(result.getIdNotificacion()).isEqualTo(21L);
        assertThat(result.getNombreCliente()).isNotNull();
        assertThat(result.getNombreVeterinario()).isNull();
        assertThat(result.getEmailVeterinario()).isNull();
        verify(userRepository, never()).findById(anyLong());
        verify(emailService, times(1)).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe listar notificaciones con diferentes combinaciones de cliente/veterinario null")
    void testGetAllNotifications_WithMixedNullValues() {
        // Given
        Notification notif1 = Notification.builder()
                .idNotificacion(1L)
                .titulo(NotificationTitle.CITA_CREADA)
                .cliente(testClient)
                .veterinario(testVeterinarian)
                .fechaCreacion(LocalDateTime.now())
                .build();
        notif1.setLeida(false);
        notif1.setEnviadaEmail(false);

        Notification notif2 = Notification.builder()
                .idNotificacion(2L)
                .titulo(NotificationTitle.CITA_CONFIRMADA)
                .veterinario(testVeterinarian)
                .fechaCreacion(LocalDateTime.now())
                .build();
        notif2.setLeida(false);
        notif2.setEnviadaEmail(false);

        Notification notif3 = Notification.builder()
                .idNotificacion(3L)
                .titulo(NotificationTitle.CITA_RECORDATORIO)
                .cliente(testClient)
                .fechaCreacion(LocalDateTime.now())
                .build();
        notif3.setLeida(false);
        notif3.setEnviadaEmail(false);

        when(notificationRepository.findAll()).thenReturn(Arrays.asList(notif1, notif2, notif3));

        // When
        List<NotificationResponseDTO> result = notificationService.getAllNotifications();

        // Then
        assertThat(result).hasSize(3);
        
        // Notificación con ambos
        assertThat(result.get(0).getNombreCliente()).isNotNull();
        assertThat(result.get(0).getNombreVeterinario()).isNotNull();
        
        // Notificación solo con veterinario
        assertThat(result.get(1).getNombreCliente()).isNull();
        assertThat(result.get(1).getNombreVeterinario()).isNotNull();
        
        // Notificación solo con cliente
        assertThat(result.get(2).getNombreCliente()).isNotNull();
        assertThat(result.get(2).getNombreVeterinario()).isNull();
        
        verify(notificationRepository, times(1)).findAll();
    }
}

