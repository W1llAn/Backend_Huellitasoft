package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.consultation.ConsultationCreateDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationResponseDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationUpdateDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.*;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.*;
import huellitassoft_web.huellitasoft.service.impl.ConsultationServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultationService Integration Tests")
class ConsultationServiceIntegrationTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private MedicalHistoryRepository medicalHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConsultationServiceImpl consultationService;

    private User testVeterinarian;
    private Client testClient;
    private Pet testPet;
    private MedicalHistory testMedicalHistory;
    private Consultation testConsultation;
    private ConsultationCreateDTO consultationCreateDTO;

    @BeforeEach
    void setUp() {
        // Veterinario
        testVeterinarian = User.builder()
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
                .usuario(clientUser)
                .documentoIdentidad("123456789")
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@test.com")
                .telefono("555-1234")
                .direccion("Calle Principal 123")
                .build();

        // Especie y Raza
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
                .raza(race)
                .cliente(testClient)
                .observaciones("Mascota sana")
                .build();

        // Historial médico
        testMedicalHistory = MedicalHistory.builder()
                .idHistoria(1L)
                .mascota(testPet)
                .numero("MH-001")
                .estado(MedicalHistoryState.ACTIVO)
                .build();

        // Consulta
        testConsultation = Consultation.builder()
                .idConsulta(1L)
                .medicalHistory(testMedicalHistory)
                .fechaHora(LocalDateTime.now())
                .motivo("Vacunación anual")
                .veterinarian(testVeterinarian)
                .diagnostico("Mascota en buen estado")
                .indicaciones("Seguimiento en 6 meses")
                .build();

        // Crear tratamientos para la consulta
        Treatment treatment1 = Treatment.builder()
                .id(1L)
                .consultation(testConsultation)
                .pet(testPet)
                .medication("Amoxicilina")
                .dosage("500mg")
                .frequency("Cada 8 horas")
                .durationDays(7)
                .observations("Tomar con comida")
                .build();

        Treatment treatment2 = Treatment.builder()
                .id(2L)
                .consultation(testConsultation)
                .pet(testPet)
                .medication("Ibuprofeno")
                .dosage("200mg")
                .frequency("Cada 12 horas")
                .durationDays(5)
                .observations("Después de las comidas")
                .build();

        testConsultation.setTratamientos(Arrays.asList(treatment1, treatment2));

        // DTO de creación
        consultationCreateDTO = ConsultationCreateDTO.builder()
                .idHistoria(1L)
                .fechaHora(LocalDateTime.now())
                .motivo("Vacunación anual")
                .idVeterinario(1)
                .diagnostico("Mascota en buen estado")
                .indicaciones("Seguimiento en 6 meses")
                .build();
    }

    @Test
    @DisplayName("Debe crear una consulta exitosamente")
    void testCreateConsultation_Success() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testVeterinarian));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(testConsultation);

        // When
        ConsultationResponseDTO result = consultationService.createConsultation(consultationCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdConsulta()).isEqualTo(1L);
        assertThat(result.getMotivo()).isEqualTo("Vacunación anual");
        verify(consultationRepository, times(1)).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear consulta con historial inexistente")
    void testCreateConsultation_HistoriaNotFound() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultationService.createConsultation(consultationCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Historial clínico no encontrado");

        verify(consultationRepository, never()).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear consulta con veterinario inexistente")
    void testCreateConsultation_VeterinarioNotFound() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultationService.createConsultation(consultationCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Veterinario no encontrado");

        verify(consultationRepository, never()).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe obtener una consulta por ID exitosamente")
    void testGetConsultationById_Success() {
        // Given
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));

        // When
        ConsultationResponseDTO result = consultationService.getConsultationById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdConsulta()).isEqualTo(1L);
        assertThat(result.getMotivo()).isEqualTo("Vacunación anual");
        assertThat(result.getTotalTratamientos()).isEqualTo(2);
        verify(consultationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar consulta inexistente")
    void testGetConsultationById_NotFound() {
        // Given
        when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultationService.getConsultationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Consulta no encontrada");

        verify(consultationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener todas las consultas de un historial")
    void testGetConsultationsByHistoria_Success() {
        // Given
        when(consultationRepository.findByMedicalHistory_IdHistoria(1L)).thenReturn(Arrays.asList(testConsultation));

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByHistoria(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdHistoria()).isEqualTo(1L);
        verify(consultationRepository, times(1)).findByMedicalHistory_IdHistoria(1L);
    }

    @Test
    @DisplayName("Debe obtener todas las consultas de una mascota")
    void testGetConsultationsByMascota_Success() {
        // Given
        when(consultationRepository.findByMascota_IdMascota(1L)).thenReturn(Arrays.asList(testConsultation));

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByMascota(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdConsulta()).isEqualTo(1L);
        verify(consultationRepository, times(1)).findByMascota_IdMascota(1L);
    }

    @Test
    @DisplayName("Debe actualizar una consulta exitosamente")
    void testUpdateConsultation_Success() {
        // Given
        ConsultationUpdateDTO updateDTO = ConsultationUpdateDTO.builder()
                .motivo("Revisión post-operatoria")
                .diagnostico("Recuperación satisfactoria")
                .indicaciones("Mantener reposo")
                .build();

        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(testConsultation);

        // When
        ConsultationResponseDTO result = consultationService.updateConsultation(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(consultationRepository, times(1)).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe eliminar una consulta exitosamente")
    void testDeleteConsultation_Success() {
        // Given
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));
        doNothing().when(consultationRepository).delete(testConsultation);

        // When
        consultationService.deleteConsultation(1L);

        // Then
        verify(consultationRepository, times(1)).delete(testConsultation);
    }

    @Test
    @DisplayName("Debe obtener todas las consultas")
    void testGetAllConsultations_Success() {
        // Given
        when(consultationRepository.findAll()).thenReturn(Arrays.asList(testConsultation));

        // When
        List<ConsultationResponseDTO> result = consultationService.getAllConsultations();

        // Then
        assertThat(result).hasSize(1);
        verify(consultationRepository, times(1)).findAll();
    }

    // ====================== TESTS FALTANTES PARA CONSULTATIONSERVICE ======================

    @Test
    @DisplayName("Debe obtener consultas por veterinario exitosamente")
    void testGetConsultationsByVeterinario_Success() {
        // Given
        List<Consultation> consultations = Arrays.asList(testConsultation);
        when(consultationRepository.findByVeterinarian_IdUsuario(1)).thenReturn(consultations);

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByVeterinario(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdVeterinario()).isEqualTo(1);
        assertThat(result.get(0).getNombreVeterinario()).isEqualTo("veterinario");
        verify(consultationRepository, times(1)).findByVeterinarian_IdUsuario(1);
    }

    @Test
    @DisplayName("Debe obtener consultas entre fechas exitosamente")
    void testGetConsultationsByFechaHora_Success() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now().plusDays(7);
        List<Consultation> consultations = Arrays.asList(testConsultation);

        when(consultationRepository.findByFechaHoraBetween(inicio, fin)).thenReturn(consultations);

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByFechaHora(inicio, fin);

        // Then
        assertThat(result).hasSize(1);
        verify(consultationRepository, times(1)).findByFechaHoraBetween(inicio, fin);
    }

    @Test
    @DisplayName("Debe obtener la última consulta de una mascota exitosamente")
    void testGetLastConsultationByMascota_Success() {
        // Given
        when(consultationRepository.findLastConsultationByMascota(1L)).thenReturn(Optional.of(testConsultation));

        // When
        ConsultationResponseDTO result = consultationService.getLastConsultationByMascota(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdConsulta()).isEqualTo(1L);
        assertThat(result.getDiagnostico()).isEqualTo("Mascota en buen estado");
        verify(consultationRepository, times(1)).findLastConsultationByMascota(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no hay consultas para la mascota")
    void testGetLastConsultationByMascota_NotFound() {
        // Given
        when(consultationRepository.findLastConsultationByMascota(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultationService.getLastConsultationByMascota(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hay consultas registradas para la mascota: 999");

        verify(consultationRepository, times(1)).findLastConsultationByMascota(999L);
    }

    @Test
    @DisplayName("Debe actualizar todos los campos de la consulta exitosamente")
    void testUpdateConsultation_FullUpdate_Success() {
        // Given
        ConsultationUpdateDTO updateDTO = ConsultationUpdateDTO.builder()
                .motivo("Urgencia por herida")
                .diagnostico("Herida superficial en pata")
                .indicaciones("Curación diaria y antibiótico")
                .build();

        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(i -> i.getArgument(0));

        // When
        ConsultationResponseDTO result = consultationService.updateConsultation(1L, updateDTO);

        // Then
        assertThat(result.getMotivo()).isEqualTo("Urgencia por herida");
        assertThat(result.getDiagnostico()).isEqualTo("Herida superficial en pata");
        assertThat(result.getIndicaciones()).isEqualTo("Curación diaria y antibiótico");
        verify(consultationRepository, times(1)).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar consulta inexistente")
    void testUpdateConsultation_NotFound() {
        // Given
        ConsultationUpdateDTO updateDTO = ConsultationUpdateDTO.builder()
                .motivo("Nuevo motivo")
                .build();

        when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultationService.updateConsultation(999L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Consulta no encontrada con ID: 999");

        verify(consultationRepository, never()).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar consulta inexistente")
    void testDeleteConsultation_NotFound() {
        // Given
        when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> consultationService.deleteConsultation(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Consulta no encontrada con ID: 999");

        verify(consultationRepository, never()).delete(any(Consultation.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consultas para un historial")
    void testGetConsultationsByHistoria_EmptyList() {
        // Given
        when(consultationRepository.findByMedicalHistory_IdHistoria(999L)).thenReturn(List.of());

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByHistoria(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consultas para una mascota")
    void testGetConsultationsByMascota_EmptyList() {
        // Given
        when(consultationRepository.findByMascota_IdMascota(999L)).thenReturn(List.of());

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByMascota(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consultas para un veterinario")
    void testGetConsultationsByVeterinario_EmptyList() {
        // Given
        when(consultationRepository.findByVeterinarian_IdUsuario(999)).thenReturn(List.of());

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByVeterinario(999);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consultas entre fechas")
    void testGetConsultationsByFechaHora_EmptyList() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now().minusDays(20);

        when(consultationRepository.findByFechaHoraBetween(inicio, fin)).thenReturn(List.of());

        // When
        List<ConsultationResponseDTO> result = consultationService.getConsultationsByFechaHora(inicio, fin);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consultas en general")
    void testGetAllConsultations_EmptyList() {
        // Given
        when(consultationRepository.findAll()).thenReturn(List.of());

        // When
        List<ConsultationResponseDTO> result = consultationService.getAllConsultations();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe mapear correctamente el total de tratamientos en el DTO")
    void testMapToResponseDTO_TotalTratamientos() {
        // Given
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));

        // When
        ConsultationResponseDTO result = consultationService.getConsultationById(1L);

        // Then
        assertThat(result.getTotalTratamientos()).isEqualTo(2); // tiene 2 tratamientos mockeados
    }
}
