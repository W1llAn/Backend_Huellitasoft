package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationRequestDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentUpdateDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.*;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.*;
import huellitassoft_web.huellitasoft.service.impl.AppointmentServiceImpl;
import huellitassoft_web.huellitasoft.service.impl.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para AppointmentService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Integration Tests")
class AppointmentServiceIntegrationTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubsidiaryRepository subsidiaryRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User testUser;
    private Client testClient;
    private Pet testPet;
    private Subsidiary testSubsidiary;
    private Appointment testAppointment;
    private AppointmentCreateDTO appointmentCreateDTO;

    @BeforeEach
    void setUp() {
        // Usuario veterinario
        testUser = User.builder()
                .idUsuario(1L)
                .email("vet@example.com")
                .username("veterinario")
                .contrasena("password")
                .rol(UserRol.ROLE_VETERINARIO)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        // Cliente
        User clientUser = User.builder()
                .idUsuario(2L)
                .email("client@example.com")
                .username("cliente")
                .contrasena("password")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        testClient = Client.builder()
                .idCliente(1L)
                .nombres("Juan")
                .apellidos("García")
                .documentoIdentidad("1234567890")
                .email("juan@example.com")
                .telefono("3001234567")
                .direccion("Calle 123")
                .estado(ClientState.ACTIVO)
                .usuario(clientUser)
                .build();

        // Especie y raza
        Specie specie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        Race race = Race.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .specie(specie)
                .build();

        // Mascota
        testPet = Pet.builder()
                .idMascota(1L)
                .nombre("Max")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .sexo(Sex.M)
                .estado(true)
                .eliminado(false)
                .cliente(testClient)
                .raza(race)
                .build();

        // Sucursal
        testSubsidiary = new Subsidiary();
        testSubsidiary.setIdSubsidiary(1L);
        testSubsidiary.setName("Sucursal Centro");
        testSubsidiary.setAddress("Avenida Principal");
        testSubsidiary.setState(SubsidiaryState.ACTIVO);

        // Cita
        testAppointment = Appointment.builder()
                .idCita(1L)
                .cliente(testClient)
                .mascota(testPet)
                .usuario(testUser)
                .sucursal(testSubsidiary)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .estado(EstadoCita.CONFIRMADA)
                .motivo("Control de rutina")
                .build();

        // DTO de creación
        appointmentCreateDTO = AppointmentCreateDTO.builder()
                .idCliente(1L)
                .idMascota(1L)
                .idUsuario(1L)
                .idSucursal(1L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .estado(EstadoCita.CONFIRMADA)
                .motivo("Control de rutina")
                .build();
    }

    @Test
    @DisplayName("Debe crear una cita exitosamente")
    void testCreateAppointment_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);
        when(appointmentRepository.existsByMascotaIdMascotaAndFechaHora(anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        AppointmentResponseDTO result = appointmentService.create(appointmentCreateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(clientRepository, times(1)).findById(1L);
        verify(petRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(subsidiaryRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void testCreateAppointment_ClientNotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");

        verify(clientRepository, times(1)).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la mascota no existe")
    void testCreateAppointment_PetNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(petRepository, times(1)).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void testCreateAppointment_UserNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(userRepository, times(1)).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no es veterinario")
    void testCreateAppointment_InvalidUserRole() {
        // Given
        User clientUser = User.builder()
                .idUsuario(1L)
                .rol(UserRol.ROLE_CLIENTE)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(clientUser));

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Solo los usuarios con rol VETERINARIO");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ya existe cita en el mismo horario para el veterinario")
    void testCreateAppointment_VetAlreadyHasAppointment() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El veterinario ya tiene una cita programada");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la mascota ya tiene cita en el mismo horario")
    void testCreateAppointment_PetAlreadyHasAppointment() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);
        when(appointmentRepository.existsByMascotaIdMascotaAndFechaHora(anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La mascota ya tiene una cita");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe actualizar una cita exitosamente")
    void testUpdateAppointment_Success() {
        // Given
        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .fechaHora(LocalDateTime.now().plusDays(2))
                .motivo("Control actualizado")
                .estado(EstadoCita.CONFIRMADA)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        AppointmentResponseDTO result = appointmentService.update(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe eliminar una cita exitosamente")
    void testDeleteAppointment_Success() {
        // Given
        when(appointmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(1L);

        // When
        appointmentService.delete(1L);

        // Then
        verify(appointmentRepository, times(1)).existsById(1L);
        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe buscar cita por ID exitosamente")
    void testFindById_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        // When
        Optional<AppointmentResponseDTO> result = appointmentService.findById(1L);

        // Then
        assertThat(result).isPresent();
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe buscar todas las citas")
    void testFindAll_Success() {
        // Given
        when(appointmentRepository.findAll()).thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar citas por cliente")
    void testFindByCliente_Success() {
        // Given
        when(appointmentRepository.findByClienteIdCliente(1L)).thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findByCliente(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findByClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe buscar citas por mascota")
    void testFindByMascota_Success() {
        // Given
        when(appointmentRepository.findByMascotaIdMascota(1L)).thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findByMascota(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findByMascotaIdMascota(1L);
    }

    @Test
    @DisplayName("Debe buscar citas por usuario")
    void testFindByUsuario_Success() {
        // Given
        when(appointmentRepository.findByUsuarioIdUsuario(1L)).thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findByUsuario(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findByUsuarioIdUsuario(1L);
    }

    @Test
    @DisplayName("Debe buscar citas por sucursal")
    void testFindBySucursal_Success() {
        // Given
        when(appointmentRepository.findBySucursalIdSubsidiary(1L)).thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findBySucursal(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findBySucursalIdSubsidiary(1L);
    }

    @Test
    @DisplayName("Debe buscar citas por estado")
    void testFindByEstado_Success() {
        // Given
        when(appointmentRepository.findByEstado(EstadoCita.PENDIENTE)).thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findByEstado(EstadoCita.PENDIENTE);

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findByEstado(EstadoCita.PENDIENTE);
    }

    @Test
    @DisplayName("Debe buscar citas entre fechas")
    void testFindByFechaBetween_Success() {
        // Given
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(7);
        when(appointmentRepository.findByFechaHoraBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(java.util.List.of(testAppointment));

        // When
        java.util.List<AppointmentResponseDTO> result = appointmentService.findByFechaBetween(fechaInicio, fechaFin);

        // Then
        assertThat(result).hasSize(1);
        verify(appointmentRepository, times(1)).findByFechaHoraBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe verificar si existe cita por mascota y fecha/hora")
    void testExistsByMascotaAndFechaHora_Success() {
        // Given
        LocalDate fecha = LocalDate.now();
        LocalDateTime fechaHora = LocalDateTime.of(fecha, java.time.LocalTime.of(10, 30));
        when(appointmentRepository.existsByMascotaIdMascotaAndFechaHora(1L, fechaHora)).thenReturn(true);

        // When
        boolean result = appointmentService.existsByMascotaAndFechaHora(1L, fecha, 10, 30);

        // Then
        assertThat(result).isTrue();
        verify(appointmentRepository, times(1)).existsByMascotaIdMascotaAndFechaHora(1L, fechaHora);
    }

    @Test
    @DisplayName("Debe contar citas por cliente")
    void testCountByCliente_Success() {
        // Given
        when(appointmentRepository.countByClienteIdCliente(1L)).thenReturn(5L);

        // When
        long result = appointmentService.countByCliente(1L);

        // Then
        assertThat(result).isEqualTo(5L);
        verify(appointmentRepository, times(1)).countByClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe contar citas por estado")
    void testCountByEstado_Success() {
        // Given
        when(appointmentRepository.countByEstado(EstadoCita.PENDIENTE)).thenReturn(3L);

        // When
        long result = appointmentService.countByEstado(EstadoCita.PENDIENTE);

        // Then
        assertThat(result).isEqualTo(3L);
        verify(appointmentRepository, times(1)).countByEstado(EstadoCita.PENDIENTE);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la mascota no pertenece al cliente")
    void testCreateAppointment_PetDoesNotBelongToClient() {
        // Given
        Client otroCliente = Client.builder()
                .idCliente(99L)
                .nombres("Otro")
                .apellidos("Cliente")
                .usuario(User.builder().idUsuario(999L).build()) // para evitar NPE en mapper si llega
                .build();

        Pet mascotaAjena = Pet.builder()
                .idMascota(1L)
                .nombre("Rex")
                .cliente(otroCliente)
                .build();

        // Mockea TODAS las dependencias que se buscan ANTES de la validación
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(mascotaAjena));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La mascota con ID 1 no pertenece al cliente con ID 1");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la sucursal no existe")
    void testCreateAppointment_SubsidiaryNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> appointmentService.create(appointmentCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si la cita ya está ATENDIDA")
    void testUpdateAppointment_AlreadyAttended() {
        // Given
        testAppointment.setEstado(EstadoCita.ATENDIDA);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .motivo("Nuevo motivo")
                .build();

        // When & Then
        assertThatThrownBy(() -> appointmentService.update(1L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede modificar una cita que ya está ATENDIDA");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar veterinario si el nuevo ya tiene cita en ese horario")
    void testUpdateAppointment_ChangeVet_Conflict() {
        // Given
        User nuevoVet = User.builder().idUsuario(99L).rol(UserRol.ROLE_VETERINARIO).build();
        LocalDateTime nuevaFechaHora = LocalDateTime.now().plusDays(3);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userRepository.findById(99L)).thenReturn(Optional.of(nuevoVet));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(99L, nuevaFechaHora))
                .thenReturn(true);

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .idUsuario(99L)
                .fechaHora(nuevaFechaHora)
                .build();

        // When & Then
        assertThatThrownBy(() -> appointmentService.update(1L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El veterinario ya tiene una cita en la nueva fecha y hora");
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar fecha/hora si es pasada")
    void testUpdateAppointment_PastDateTime() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        LocalDateTime fechaPasada = LocalDateTime.now().minusHours(1);

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .fechaHora(fechaPasada)
                .build();

        // When & Then
        assertThatThrownBy(() -> appointmentService.update(1L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha y hora deben ser futuras");
    }

    @Test
    @DisplayName("Debe actualizar fecha/hora y veterinario exitosamente cuando no hay conflicto")
    void testUpdateAppointment_ChangeVetAndDateTime_Success() {
        // Given
        User nuevoVet = User.builder().idUsuario(99L).username("drjones").rol(UserRol.ROLE_VETERINARIO).build();
        LocalDateTime nuevaFechaHora = LocalDateTime.now().plusDays(5);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userRepository.findById(99L)).thenReturn(Optional.of(nuevoVet));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(99L, nuevaFechaHora))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .idUsuario(99L)
                .fechaHora(nuevaFechaHora)
                .motivo("Urgencia")
                .estado(EstadoCita.PENDIENTE)
                .build();

        // When
        AppointmentResponseDTO result = appointmentService.update(1L, updateDTO);

        // Then
        assertThat(result.getIdUsuario()).isEqualTo(99L);
        assertThat(result.getNombreUsuario()).isEqualTo("drjones");
        assertThat(result.getFechaHora()).isEqualTo(nuevaFechaHora);
        assertThat(result.getMotivo()).isEqualTo("Urgencia");
        assertThat(result.getEstado()).isEqualTo(EstadoCita.PENDIENTE);
        verify(notificationService, times(1)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el estado y motivo sin cambiar fecha ni veterinario")
    void testUpdateAppointment_OnlyStateAndReason_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .estado(EstadoCita.CANCELADA)
                .motivo("Cliente no puede asistir")
                .build();

        // When
        AppointmentResponseDTO result = appointmentService.update(1L, updateDTO);

        // Then
        assertThat(result.getEstado()).isEqualTo(EstadoCita.CANCELADA);
        assertThat(result.getMotivo()).isEqualTo("Cliente no puede asistir");
        verify(notificationService, times(1)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar si la cita no existe")
    void testDeleteAppointment_NotFound() {
        // Given
        when(appointmentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> appointmentService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cita no encontrada con ID: 999");

        verify(appointmentRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe enviar notificación al crear cita")
    void testCreateAppointment_SendsNotification() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(1L)).thenReturn(Optional.of(testSubsidiary));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(anyLong(), any())).thenReturn(false);
        when(appointmentRepository.existsByMascotaIdMascotaAndFechaHora(anyLong(), any())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> {
            Appointment a = i.getArgument(0);
            a.setIdCita(1L);
            return a;
        });

        // When
        appointmentService.create(appointmentCreateDTO);

        // Then
        verify(notificationService, times(1)).createNotification(argThat(notif ->
                notif.getTitulo() == NotificationTitle.CITA_CREADA &&
                        notif.getIdCliente().equals(1L) &&
                        notif.getIdVeterinario().equals(1L)
        ));
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no hay citas")
    void testFindAll_EmptyList() {
        // Given
        when(appointmentRepository.findAll()).thenReturn(List.of());

        // When
        List<AppointmentResponseDTO> result = appointmentService.findAll();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Al cambiar estado a CANCELADA se envía notificación como 'modificada' (comportamiento actual del código)")
    void testUpdateAppointment_ToCancelled_SendsUpdatedNotification_NotCancelled() {
        // Given
        testAppointment.setEstado(EstadoCita.CONFIRMADA);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .estado(EstadoCita.CANCELADA)
                .motivo("Cliente canceló la cita")
                .build();

        // When
        appointmentService.update(1L, updateDTO);

        // Then - Verifica lo que REALMENTE se envía (CITA_CONFIRMADA)
        verify(notificationService, times(1)).createNotification(argThat(notif ->
                notif.getTitulo() == NotificationTitle.CITA_CONFIRMADA &&
                        notif.getAsunto().equals("Cita modificada") &&
                        notif.getMensaje().contains("ha sido modificada") &&
                        notif.getIdCita().equals(1L)
        ));
    }

    @Test
    @DisplayName("Debe enviar notificación de cita modificada al cambiar fecha/hora")
    void testUpdateAppointment_ChangeDateTime_SendsUpdatedNotification() {
        // Given
        LocalDateTime nuevaFechaHora = LocalDateTime.now().plusDays(10);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(1L, nuevaFechaHora))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .fechaHora(nuevaFechaHora)
                .build();

        // When
        appointmentService.update(1L, updateDTO);

        // Then
        verify(notificationService, times(1)).createNotification(argThat(notif ->
                notif.getTitulo() == NotificationTitle.CITA_CONFIRMADA &&
                        notif.getMensaje().contains("ha sido modificada")
        ));
    }

    @Test
    @DisplayName("Debe cubrir lambda de cambio de veterinario con conflicto de horario")
    void testUpdateAppointment_ChangeVet_WithConflict_CoversLambda() {
        // Given
        User nuevoVet = User.builder().idUsuario(99L).username("drjones").rol(UserRol.ROLE_VETERINARIO).build();
        LocalDateTime mismaFechaHora = testAppointment.getFechaHora();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userRepository.findById(99L)).thenReturn(Optional.of(nuevoVet));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(99L, mismaFechaHora))
                .thenReturn(true); // ← Conflicto

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .idUsuario(99L)
                .fechaHora(mismaFechaHora)
                .build();

        // When & Then
        assertThatThrownBy(() -> appointmentService.update(1L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El veterinario ya tiene una cita en la nueva fecha y hora");

        // Este test cubre el lambda interno del cambio de veterinario
        verify(appointmentRepository, times(1)).existsByUsuarioIdUsuarioAndFechaHora(99L, mismaFechaHora);
    }

    @Test
    @DisplayName("Debe cubrir lambda de cambio de fecha/hora con conflicto")
    void testUpdateAppointment_ChangeDateTime_WithConflict_CoversLambda() {
        // Given
        LocalDateTime nuevaFechaHora = testAppointment.getFechaHora().plusDays(1); // Fecha DIFERENTE

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(1L, nuevaFechaHora))
                .thenReturn(true); // Conflicto en la nueva fecha

        AppointmentUpdateDTO updateDTO = AppointmentUpdateDTO.builder()
                .fechaHora(nuevaFechaHora)
                .build();

        // When & Then
        assertThatThrownBy(() -> appointmentService.update(1L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El veterinario ya tiene una cita en la fecha y hora indicada");
    }

    @Test
    @DisplayName("Al cancelar cita se envía notificación con título CITA_CONFIRMADA (comportamiento actual)")
    void testUpdateAppointment_ToCancelled_SendsUpdatedNotification() {
        testAppointment.setEstado(EstadoCita.CONFIRMADA);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        appointmentService.update(1L, AppointmentUpdateDTO.builder()
                .estado(EstadoCita.CANCELADA)
                .build());

        verify(notificationService, times(1)).createNotification(argThat(notif ->
                notif.getTitulo() == NotificationTitle.CITA_CONFIRMADA &&
                        notif.getMensaje().contains("ha sido modificada")
        ));
    }

    @Test
    @DisplayName("Debe enviar notificación CITA_CANCELADA correctamente")
    void testSendAppointmentCancelledNotification() throws Exception {
        // Given - Usar reflexión para acceder al método privado
        var method = AppointmentServiceImpl.class.getDeclaredMethod("sendAppointmentCancelledNotification", Appointment.class);
        method.setAccessible(true);

        // When - Invocar el método privado
        method.invoke(appointmentService, testAppointment);

        // Then - Verificar que se creó la notificación correctamente
        verify(notificationService, times(1)).createNotification(argThat(notif ->
                notif.getTitulo() == NotificationTitle.CITA_CANCELADA &&
                        notif.getAsunto().equals("Cita cancelada") &&
                        notif.getMensaje().contains("La cita para " + testPet.getNombre()) &&
                        notif.getMensaje().contains("ha sido cancelada") &&
                        notif.getTipo().equals("CITA") &&
                        notif.getIdCliente().equals(testClient.getIdCliente()) &&
                        notif.getIdVeterinario().equals(testUser.getIdUsuario()) &&
                        notif.getIdCita().equals(testAppointment.getIdCita())
        ));
    }
}
