package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.treatment.TreatmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentUpdateDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.*;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.*;
import huellitassoft_web.huellitasoft.service.impl.TreatmentServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TreatmentService Integration Tests")
class TreatmentServiceIntegrationTest {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private TreatmentServiceImpl treatmentService;

    private Consultation testConsultation;
    private Treatment testTreatment;
    private TreatmentCreateDTO treatmentCreateDTO;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        // Usuario veterinario
        User testVeterinarian = User.builder()
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

        Client testClient = Client.builder()
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
        MedicalHistory testMedicalHistory = MedicalHistory.builder()
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

        // Tratamiento
        testTreatment = Treatment.builder()
                .id(1L)
                .consultation(testConsultation)
                .pet(testPet)
                .medication("Amoxicilina")
                .dosage("500mg")
                .frequency("Cada 8 horas")
                .durationDays(7)
                .observations("Tomar con comida")
                .build();

        // DTO de creación
        treatmentCreateDTO = TreatmentCreateDTO.builder()
                .idConsulta(1L)
                .idMascota(1L)
                .medication("Amoxicilina")
                .dosage("500mg")
                .frequency("Cada 8 horas")
                .durationDays(7)
                .observations("Tomar con comida")
                .build();
    }

    @Test
    @DisplayName("Debe crear un tratamiento exitosamente")
    void testCreateTreatment_Success() {
        // Given
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(treatmentRepository.save(any(Treatment.class))).thenReturn(testTreatment);

        // When
        TreatmentResponseDTO result = treatmentService.createTreatment(treatmentCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdTratamiento()).isEqualTo(1L);
        assertThat(result.getMedication()).isEqualTo("Amoxicilina");
        verify(treatmentRepository, times(1)).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear tratamiento con consulta inexistente")
    void testCreateTreatment_ConsultationNotFound() {
        // Given
        when(consultationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentService.createTreatment(treatmentCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Consulta no encontrada");

        verify(treatmentRepository, never()).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe obtener tratamiento por ID exitosamente")
    void testGetTreatmentById_Success() {
        // Given
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(testTreatment));

        // When
        TreatmentResponseDTO result = treatmentService.getTreatmentById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdTratamiento()).isEqualTo(1L);
        assertThat(result.getMedication()).isEqualTo("Amoxicilina");
        verify(treatmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar tratamiento inexistente")
    void testGetTreatmentById_NotFound() {
        // Given
        when(treatmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentService.getTreatmentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tratamiento no encontrado");

        verify(treatmentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener tratamientos por consulta")
    void testGetTreatmentsByConsultation_Success() {
        // Given
        when(treatmentRepository.findByConsultation_IdConsulta(1L)).thenReturn(Arrays.asList(testTreatment));

        // When
        List<TreatmentResponseDTO> result = treatmentService.getTreatmentsByConsultation(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdConsulta()).isEqualTo(1L);
        verify(treatmentRepository, times(1)).findByConsultation_IdConsulta(1L);
    }

    @Test
    @DisplayName("Debe actualizar tratamiento exitosamente")
    void testUpdateTreatment_Success() {
        // Given
        TreatmentUpdateDTO updateDTO = TreatmentUpdateDTO.builder()
                .medication("Amoxicilina 875mg")
                .dosage("875mg")
                .frequency("Cada 12 horas")
                .durationDays(10)
                .observations("Tomar con alimento")
                .build();

        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(testTreatment));
        when(treatmentRepository.save(any(Treatment.class))).thenReturn(testTreatment);

        // When
        TreatmentResponseDTO result = treatmentService.updateTreatment(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(treatmentRepository, times(1)).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe eliminar tratamiento exitosamente")
    void testDeleteTreatment_Success() {
        // Given
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(testTreatment));
        doNothing().when(treatmentRepository).delete(testTreatment);

        // When
        treatmentService.deleteTreatment(1L);

        // Then
        verify(treatmentRepository, times(1)).delete(testTreatment);
    }

    @Test
    @DisplayName("Debe obtener todos los tratamientos")
    void testGetAllTreatments_Success() {
        // Given
        when(treatmentRepository.findAll()).thenReturn(Arrays.asList(testTreatment));

        // When
        List<TreatmentResponseDTO> result = treatmentService.getAllTreatments();

        // Then
        assertThat(result).hasSize(1);
        verify(treatmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear tratamiento con mascota inexistente")
    void testCreateTreatment_PetNotFound() {
        // Given
        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentService.createTreatment(treatmentCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada con ID: 1");

        verify(treatmentRepository, never()).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe crear tratamiento con descripción y estado por defecto")
    void testCreateTreatment_WithDescriptionAndDefaultStatus() {
        // Given
        TreatmentCreateDTO dtoWithDescription = TreatmentCreateDTO.builder()
                .idConsulta(1L)
                .idMascota(1L)
                .medication("Paracetamol")
                .dosage("500mg")
                .description("Tratamiento para fiebre")
                .build();

        when(consultationRepository.findById(1L)).thenReturn(Optional.of(testConsultation));
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(treatmentRepository.save(any(Treatment.class))).thenAnswer(i -> {
            Treatment t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // When
        TreatmentResponseDTO result = treatmentService.createTreatment(dtoWithDescription);

        // Then
        assertThat(result.getDescription()).isEqualTo("Tratamiento para fiebre");
        assertThat(result.getStatus()).isTrue(); // valor por defecto
    }

    @Test
    @DisplayName("Debe obtener tratamientos por mascota exitosamente")
    void testGetTreatmentsByMascota_Success() {
        // Given
        List<Treatment> treatments = Arrays.asList(testTreatment);
        when(treatmentRepository.findByPet_IdMascota(1L)).thenReturn(treatments);

        // When
        List<TreatmentResponseDTO> result = treatmentService.getTreatmentsByMascota(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdMascota()).isEqualTo(1L);
        assertThat(result.get(0).getNombreMascota()).isEqualTo("Max");
        verify(treatmentRepository, times(1)).findByPet_IdMascota(1L);
    }

    @Test
    @DisplayName("Debe obtener tratamientos activos por mascota exitosamente")
    void testGetActiveTreatmentsByMascota_Success() {
        // Given
        testTreatment.setStatus(true);
        List<Treatment> activeTreatments = Arrays.asList(testTreatment);
        when(treatmentRepository.findActiveByMascota(1L)).thenReturn(activeTreatments);

        // When
        List<TreatmentResponseDTO> result = treatmentService.getActiveTreatmentsByMascota(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isTrue();
        verify(treatmentRepository, times(1)).findActiveByMascota(1L);
    }

    @Test
    @DisplayName("Debe actualizar solo el estado del tratamiento exitosamente")
    void testUpdateTreatmentStatus_Success() {
        // Given
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(testTreatment));
        when(treatmentRepository.save(any(Treatment.class))).thenAnswer(i -> i.getArgument(0));

        // When
        TreatmentResponseDTO result = treatmentService.updateTreatmentStatus(1L, false);

        // Then
        assertThat(result.getStatus()).isFalse();
        verify(treatmentRepository, times(1)).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar estado si el tratamiento no existe")
    void testUpdateTreatmentStatus_NotFound() {
        // Given
        when(treatmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentService.updateTreatmentStatus(999L, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tratamiento no encontrado con ID: 999");

        verify(treatmentRepository, never()).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe actualizar todos los campos del tratamiento")
    void testUpdateTreatment_FullUpdate_Success() {
        // Given
        TreatmentUpdateDTO fullUpdate = TreatmentUpdateDTO.builder()
                .description("Tratamiento post-operatorio")
                .medication("Cefalexina")
                .dosage("250mg")
                .frequency("Cada 12 horas")
                .durationDays(14)
                .observations("Administrar después de las comidas")
                .status(false)
                .build();

        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(testTreatment));
        when(treatmentRepository.save(any(Treatment.class))).thenAnswer(i -> i.getArgument(0));

        // When
        TreatmentResponseDTO result = treatmentService.updateTreatment(1L, fullUpdate);

        // Then
        assertThat(result.getDescription()).isEqualTo("Tratamiento post-operatorio");
        assertThat(result.getMedication()).isEqualTo("Cefalexina");
        assertThat(result.getDosage()).isEqualTo("250mg");
        assertThat(result.getFrequency()).isEqualTo("Cada 12 horas");
        assertThat(result.getDurationDays()).isEqualTo(14);
        assertThat(result.getObservations()).isEqualTo("Administrar después de las comidas");
        assertThat(result.getStatus()).isFalse();
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tratamiento si no existe")
    void testUpdateTreatment_NotFound() {
        // Given
        TreatmentUpdateDTO updateDTO = TreatmentUpdateDTO.builder()
                .medication("Nuevo medicamento")
                .build();

        when(treatmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentService.updateTreatment(999L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tratamiento no encontrado con ID: 999");

        verify(treatmentRepository, never()).save(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar tratamiento si no existe")
    void testDeleteTreatment_NotFound() {
        // Given
        when(treatmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> treatmentService.deleteTreatment(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tratamiento no encontrado con ID: 999");

        verify(treatmentRepository, never()).delete(any(Treatment.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tratamientos para una consulta")
    void testGetTreatmentsByConsultation_EmptyList() {
        // Given
        when(treatmentRepository.findByConsultation_IdConsulta(999L)).thenReturn(List.of());

        // When
        List<TreatmentResponseDTO> result = treatmentService.getTreatmentsByConsultation(999L);

        // Then
        assertThat(result).isEmpty();
        verify(treatmentRepository, times(1)).findByConsultation_IdConsulta(999L);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tratamientos activos para una mascota")
    void testGetActiveTreatmentsByMascota_EmptyList() {
        // Given
        when(treatmentRepository.findActiveByMascota(1L)).thenReturn(List.of());

        // When
        List<TreatmentResponseDTO> result = treatmentService.getActiveTreatmentsByMascota(1L);

        // Then
        assertThat(result).isEmpty();
    }
}
