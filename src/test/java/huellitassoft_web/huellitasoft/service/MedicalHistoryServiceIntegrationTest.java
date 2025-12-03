package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryCreateDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryDetailDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryResponseDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.*;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.*;
import huellitassoft_web.huellitasoft.service.impl.MedicalHistoryServiceImpl;
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
@DisplayName("MedicalHistoryService Integration Tests")
class MedicalHistoryServiceIntegrationTest {

    @Mock
    private MedicalHistoryRepository medicalHistoryRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @InjectMocks
    private MedicalHistoryServiceImpl medicalHistoryService;

    private Pet testPet;
    private Client testClient;
    private MedicalHistory testMedicalHistory;
    private MedicalHistoryCreateDTO medicalHistoryCreateDTO;

    @BeforeEach
    void setUp() {
        // Cliente
        User clientUser = User.builder()
                .idUsuario(1L)
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

        // DTO de creación
        medicalHistoryCreateDTO = MedicalHistoryCreateDTO.builder()
                .idMascota(1L)
                .numero("MH-001")
                .estado(MedicalHistoryState.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("Debe crear un historial médico exitosamente")
    void testCreateMedicalHistory_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(testMedicalHistory);

        // When
        MedicalHistoryResponseDTO result = medicalHistoryService.createMedicalHistory(medicalHistoryCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdHistoria()).isEqualTo(1L);
        assertThat(result.getNumero()).isEqualTo("MH-001");
        assertThat(result.getEstado()).isEqualTo(MedicalHistoryState.ACTIVO);
        verify(medicalHistoryRepository, times(1)).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear historial con mascota inexistente")
    void testCreateMedicalHistory_PetNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> medicalHistoryService.createMedicalHistory(medicalHistoryCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(medicalHistoryRepository, never()).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe obtener historial por ID exitosamente")
    void testGetMedicalHistoryById_Success() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));

        // When
        MedicalHistoryResponseDTO result = medicalHistoryService.getMedicalHistoryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdHistoria()).isEqualTo(1L);
        assertThat(result.getNombreMascota()).isEqualTo("Max");
        verify(medicalHistoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar historial inexistente")
    void testGetMedicalHistoryById_NotFound() {
        // Given
        when(medicalHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> medicalHistoryService.getMedicalHistoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Historial clínico no encontrado");

        verify(medicalHistoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener historial por mascota exitosamente")
    void testGetMedicalHistoryByMascota_Success() {
        // Given
        when(medicalHistoryRepository.findByMascota_IdMascota(1L)).thenReturn(Optional.of(testMedicalHistory));

        // When
        MedicalHistoryResponseDTO result = medicalHistoryService.getMedicalHistoryByMascota(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdMascota()).isEqualTo(1L);
        verify(medicalHistoryRepository, times(1)).findByMascota_IdMascota(1L);
    }

    @Test
    @DisplayName("Debe obtener todos los historiales por estado")
    void testGetMedicalHistoriesByEstado_Success() {
        // Given
        when(medicalHistoryRepository.findByEstado(MedicalHistoryState.ACTIVO))
                .thenReturn(Arrays.asList(testMedicalHistory));

        // When
        List<MedicalHistoryResponseDTO> result = medicalHistoryService.getMedicalHistoriesByEstado(MedicalHistoryState.ACTIVO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(MedicalHistoryState.ACTIVO);
        verify(medicalHistoryRepository, times(1)).findByEstado(MedicalHistoryState.ACTIVO);
    }

    @Test
    @DisplayName("Debe actualizar estado de historial exitosamente")
    void testUpdateEstadoMedicalHistory_Success() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(testMedicalHistory);

        // When
        MedicalHistoryResponseDTO result = medicalHistoryService.updateEstadoMedicalHistory(1L, MedicalHistoryState.INACTIVO);

        // Then
        assertThat(result).isNotNull();
        verify(medicalHistoryRepository, times(1)).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe eliminar historial exitosamente")
    void testDeleteMedicalHistory_Success() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(testMedicalHistory);

        // When
        medicalHistoryService.deleteMedicalHistory(1L);

        // Then
        verify(medicalHistoryRepository, times(1)).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe obtener todos los historiales")
    void testGetAllMedicalHistories_Success() {
        // Given
        when(medicalHistoryRepository.findAll()).thenReturn(Arrays.asList(testMedicalHistory));

        // When
        List<MedicalHistoryResponseDTO> result = medicalHistoryService.getAllMedicalHistories();

        // Then
        assertThat(result).hasSize(1);
        verify(medicalHistoryRepository, times(1)).findAll();
    }

    // ====================== TESTS FALTANTES PARA MEDICALHISTORYSERVICE ======================

    @Test
    @DisplayName("Debe lanzar excepción al crear historial si ya existe uno activo para la mascota")
    void testCreateMedicalHistory_AlreadyExistsForPet() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(medicalHistoryRepository.existsByMascota_IdMascota(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> medicalHistoryService.createMedicalHistory(medicalHistoryCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un historial clínico para esta mascota");

        verify(medicalHistoryRepository, never()).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe obtener detalle completo del historial clínico con total de consultas")
    void testGetMedicalHistoryDetail_Success() {
        // Given
        Consultation consulta1 = Consultation.builder().idConsulta(10L).build();
        Consultation consulta2 = Consultation.builder().idConsulta(11L).build();
        testMedicalHistory.setConsultas(Arrays.asList(consulta1, consulta2)); // 2 consultas

        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));

        // When
        MedicalHistoryDetailDTO result = medicalHistoryService.getMedicalHistoryDetail(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdHistoria()).isEqualTo(1L);
        assertThat(result.getNombreMascota()).isEqualTo("Max");
        assertThat(result.getRazaMascota()).isEqualTo("Labrador");
        assertThat(result.getTotalConsultas()).isEqualTo(2);
        assertThat(result.getNumero()).isEqualTo("MH-001");
        verify(medicalHistoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe obtener todos los historiales clínicos de una mascota (incluyendo inactivos)")
    void testGetAllMedicalHistoriesByMascota_Success() {
        // Given
        MedicalHistory history2 = MedicalHistory.builder()
                .idHistoria(2L)
                .mascota(testPet)
                .numero("MH-002")
                .estado(MedicalHistoryState.INACTIVO)
                .build();

        when(medicalHistoryRepository.findAllByMascota_IdMascota(1L))
                .thenReturn(Arrays.asList(testMedicalHistory, history2));

        // When
        List<MedicalHistoryResponseDTO> result = medicalHistoryService.getAllMedicalHistoriesByMascota(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MedicalHistoryResponseDTO::getNumero)
                .containsExactlyInAnyOrder("MH-001", "MH-002");
        assertThat(result).extracting(MedicalHistoryResponseDTO::getEstado)
                .containsExactlyInAnyOrder(MedicalHistoryState.ACTIVO, MedicalHistoryState.INACTIVO);
    }

    @Test
    @DisplayName("Debe retornar lista vacía si la mascota no tiene historiales")
    void testGetAllMedicalHistoriesByMascota_EmptyList() {
        // Given
        when(medicalHistoryRepository.findAllByMascota_IdMascota(1L)).thenReturn(List.of());

        // When
        List<MedicalHistoryResponseDTO> result = medicalHistoryService.getAllMedicalHistoriesByMascota(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe realizar eliminación lógica cambiando estado a ELIMINADO")
    void testDeleteMedicalHistory_LogicalDelete_Success() {
        // Given
        when(medicalHistoryRepository.findById(1L)).thenReturn(Optional.of(testMedicalHistory));
        when(medicalHistoryRepository.save(any(MedicalHistory.class)))
                .thenAnswer(i -> i.getArgument(0));

        // When
        medicalHistoryService.deleteMedicalHistory(1L);

        // Then
        assertThat(testMedicalHistory.getEstado()).isEqualTo(MedicalHistoryState.ELIMINADO);
        verify(medicalHistoryRepository, times(1)).save(testMedicalHistory);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar estado si el historial no existe")
    void testUpdateEstadoMedicalHistory_NotFound() {
        // Given
        when(medicalHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> medicalHistoryService.updateEstadoMedicalHistory(999L, MedicalHistoryState.INACTIVO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Historial clínico no encontrado con ID: 999");

        verify(medicalHistoryRepository, never()).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar si el historial no existe")
    void testDeleteMedicalHistory_NotFound() {
        // Given
        when(medicalHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> medicalHistoryService.deleteMedicalHistory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Historial clínico no encontrado con ID: 999");

        verify(medicalHistoryRepository, never()).save(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("Debe obtener historial por mascota aunque esté inactivo (si existe)")
    void testGetMedicalHistoryByMascota_InactiveHistory_Success() {
        // Given
        testMedicalHistory.setEstado(MedicalHistoryState.INACTIVO);
        when(medicalHistoryRepository.findByMascota_IdMascota(1L)).thenReturn(Optional.of(testMedicalHistory));

        // When
        MedicalHistoryResponseDTO result = medicalHistoryService.getMedicalHistoryByMascota(1L);

        // Then
        assertThat(result.getEstado()).isEqualTo(MedicalHistoryState.INACTIVO);
    }

    @Test
    @DisplayName("Debe retornar lista vacía al buscar historiales por estado si no hay coincidencias")
    void testGetMedicalHistoriesByEstado_EmptyList() {
        // Given
        when(medicalHistoryRepository.findByEstado(MedicalHistoryState.INACTIVO)).thenReturn(List.of());

        // When
        List<MedicalHistoryResponseDTO> result = medicalHistoryService.getMedicalHistoriesByEstado(MedicalHistoryState.INACTIVO);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía al obtener todos los historiales si la base está vacía")
    void testGetAllMedicalHistories_EmptyList() {
        // Given
        when(medicalHistoryRepository.findAll()).thenReturn(List.of());

        // When
        List<MedicalHistoryResponseDTO> result = medicalHistoryService.getAllMedicalHistories();

        // Then
        assertThat(result).isEmpty();
    }
}
