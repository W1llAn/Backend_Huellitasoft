package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeResponseDTO;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.entity.VaccinationScheme;
import huellitassoft_web.huellitasoft.entity.Vaccine;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.VaccinationSchemeRepository;
import huellitassoft_web.huellitasoft.repository.VaccineRepository;
import huellitassoft_web.huellitasoft.service.impl.VaccinationSchemeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VaccinationSchemeService Integration Tests")
class VaccinationSchemeServiceIntegrationTest {

    @Mock
    private VaccinationSchemeRepository vaccinationSchemeRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @InjectMocks
    private VaccinationSchemeServiceImpl vaccinationSchemeService;

    private Vaccine testVaccine;
    private VaccinationScheme testScheme;
    private VaccinationSchemeCreateDTO schemeCreateDTO;

    @BeforeEach
    void setUp() {
        Specie testSpecie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        testVaccine = Vaccine.builder()
                .idVacuna(1L)
                .nombre("Parvovirus")
                .descripcion("Vacuna contra el parvovirus canino")
                .specie(testSpecie)
                .build();

        testScheme = VaccinationScheme.builder()
                .idEsquema(1L)
                .vaccine(testVaccine)
                .dosisNumero(1)
                .edadSemanas(8)
                .observaciones("Primera dosis")
                .build();

        schemeCreateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(1L)
                .dosisNumero(1)
                .edadSemanas(8)
                .observaciones("Primera dosis")
                .build();
    }

    @Test
    @DisplayName("Debe crear un esquema de vacunación exitosamente")
    void testCreateScheme_Success() {
        // Given
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccinationSchemeRepository.save(any(VaccinationScheme.class))).thenReturn(testScheme);

        // When
        VaccinationSchemeResponseDTO result = vaccinationSchemeService.createScheme(schemeCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdEsquema()).isEqualTo(1L);
        assertThat(result.getDosisNumero()).isEqualTo(1);
        assertThat(result.getEdadSemanas()).isEqualTo(8);
        verify(vaccinationSchemeRepository, times(1)).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear esquema con vacuna inexistente")
    void testCreateScheme_VaccineNotFound() {
        // Given
        when(vaccineRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.createScheme(schemeCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacuna no encontrada");

        verify(vaccinationSchemeRepository, never()).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe obtener esquema por ID exitosamente")
    void testGetSchemeById_Success() {
        // Given
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));

        // When
        VaccinationSchemeResponseDTO result = vaccinationSchemeService.getSchemeById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdEsquema()).isEqualTo(1L);
        assertThat(result.getNombreVacuna()).isEqualTo("Parvovirus");
        verify(vaccinationSchemeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar esquema inexistente")
    void testGetSchemeById_NotFound() {
        // Given
        when(vaccinationSchemeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.getSchemeById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Esquema no encontrado");

        verify(vaccinationSchemeRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener todos los esquemas")
    void testGetAllSchemes_Success() {
        // Given
        when(vaccinationSchemeRepository.findAll()).thenReturn(Arrays.asList(testScheme));

        // When
        List<VaccinationSchemeResponseDTO> result = vaccinationSchemeService.getAllSchemes();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDosisNumero()).isEqualTo(1);
        verify(vaccinationSchemeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar esquema exitosamente")
    void testUpdateScheme_Success() {
        // Given
        VaccinationSchemeCreateDTO updateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(1L)
                .dosisNumero(2)
                .edadSemanas(12)
                .observaciones("Segunda dosis de refuerzo")
                .build();

        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccinationSchemeRepository.save(any(VaccinationScheme.class))).thenReturn(testScheme);

        // When
        VaccinationSchemeResponseDTO result = vaccinationSchemeService.updateScheme(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(vaccinationSchemeRepository, times(1)).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe eliminar esquema exitosamente")
    void testDeleteScheme_Success() {
        // Given
        when(vaccinationSchemeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vaccinationSchemeRepository).deleteById(1L);

        // When
        vaccinationSchemeService.deleteScheme(1L);

        // Then
        verify(vaccinationSchemeRepository, times(1)).deleteById(1L);
    }

    // ====================== TESTS FALTANTES PARA VACCINATIONSCHEMASERVICE ======================

    @Test
    @DisplayName("Debe lanzar excepción al crear esquema si ya existe para esa vacuna y dosis")
    void testCreateScheme_DuplicateVaccineAndDose() {
        // Given
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccinationSchemeRepository.existsByVaccine_IdVacunaAndDosisNumero(1L, 1))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.createScheme(schemeCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un esquema para esa vacuna con la dosis 1");

        verify(vaccinationSchemeRepository, never()).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe actualizar esquema cambiando vacuna y dosis (sin duplicado)")
    void testUpdateScheme_ChangeVaccineAndDose_Success() {
        // Given
        Vaccine nuevaVacuna = Vaccine.builder()
                .idVacuna(99L)
                .nombre("Rabia")
                .build();

        VaccinationSchemeCreateDTO updateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(99L)
                .dosisNumero(1)
                .edadSemanas(16)
                .observaciones("Primera dosis antirrábica")
                .build();

        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(vaccineRepository.findById(99L)).thenReturn(Optional.of(nuevaVacuna));
        when(vaccinationSchemeRepository.existsByVaccine_IdVacunaAndDosisNumero(99L, 1))
                .thenReturn(false);
        when(vaccinationSchemeRepository.save(any(VaccinationScheme.class)))
                .thenAnswer(i -> i.getArgument(0));

        // When
        VaccinationSchemeResponseDTO result = vaccinationSchemeService.updateScheme(1L, updateDTO);

        // Then
        assertThat(result.getIdVacuna()).isEqualTo(99L);
        assertThat(result.getNombreVacuna()).isEqualTo("Rabia");
        assertThat(result.getDosisNumero()).isEqualTo(1);
        assertThat(result.getEdadSemanas()).isEqualTo(16);
        verify(vaccinationSchemeRepository, times(1)).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si nueva combinación vacuna+dosis ya existe")
    void testUpdateScheme_DuplicateVaccineAndDose() {
        // Given
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccinationSchemeRepository.existsByVaccine_IdVacunaAndDosisNumero(1L, 2))
                .thenReturn(true);

        VaccinationSchemeCreateDTO updateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(1L)
                .dosisNumero(2)
                .build();

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.updateScheme(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un esquema para esa vacuna con la dosis 2");

        verify(vaccinationSchemeRepository, never()).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si la nueva vacuna no existe")
    void testUpdateScheme_VaccineNotFound() {
        // Given
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(vaccineRepository.findById(999L)).thenReturn(Optional.empty());

        VaccinationSchemeCreateDTO updateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(999L)
                .dosisNumero(1)
                .build();

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.updateScheme(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacuna no encontrada");

        verify(vaccinationSchemeRepository, never()).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe obtener esquemas por vacuna exitosamente")
    void testGetSchemesByVaccine_Success() {
        // Given
        List<VaccinationScheme> schemes = Arrays.asList(testScheme);
        when(vaccineRepository.existsById(1L)).thenReturn(true);
        when(vaccinationSchemeRepository.findByVaccine_IdVacuna(1L)).thenReturn(schemes);

        // When
        List<VaccinationSchemeResponseDTO> result = vaccinationSchemeService.getSchemesByVaccine(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdVacuna()).isEqualTo(1L);
        assertThat(result.get(0).getNombreVacuna()).isEqualTo("Parvovirus");
        verify(vaccinationSchemeRepository, times(1)).findByVaccine_IdVacuna(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar esquemas por vacuna inexistente")
    void testGetSchemesByVaccine_VaccineNotFound() {
        // Given
        when(vaccineRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.getSchemesByVaccine(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacuna no encontrada: 999");

        verify(vaccinationSchemeRepository, never()).findByVaccine_IdVacuna(anyLong());
    }

    @Test
    @DisplayName("Debe mantener la misma vacuna cuando se envía el mismo idVacuna")
    void testUpdateScheme_NoChangeVaccine_KeepsExisting() {
        // Given
        VaccinationSchemeCreateDTO updateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(1L)           // ← Enviamos el mismo idVacuna que ya tiene
                .dosisNumero(2)
                .edadSemanas(12)
                .observaciones("Refuerzo")
                .build();

        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccinationSchemeRepository.existsByVaccine_IdVacunaAndDosisNumero(1L, 2))
                .thenReturn(false);
        when(vaccinationSchemeRepository.save(any(VaccinationScheme.class)))
                .thenAnswer(i -> i.getArgument(0));

        // When
        VaccinationSchemeResponseDTO result = vaccinationSchemeService.updateScheme(1L, updateDTO);

        // Then
        assertThat(result.getIdVacuna()).isEqualTo(1L); // sigue siendo la misma
        assertThat(result.getNombreVacuna()).isEqualTo("Parvovirus");
        assertThat(result.getDosisNumero()).isEqualTo(2);
        verify(vaccineRepository, times(1)).findById(1L); // sí lo buscó, pero es válido
        verify(vaccinationSchemeRepository, times(1)).save(any(VaccinationScheme.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay esquemas")
    void testGetAllSchemes_EmptyList() {
        // Given
        when(vaccinationSchemeRepository.findAll()).thenReturn(List.of());

        // When
        List<VaccinationSchemeResponseDTO> result = vaccinationSchemeService.getAllSchemes();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía al buscar esquemas por vacuna sin resultados")
    void testGetSchemesByVaccine_EmptyList() {
        // Given
        when(vaccineRepository.existsById(1L)).thenReturn(true);
        when(vaccinationSchemeRepository.findByVaccine_IdVacuna(1L)).thenReturn(List.of());

        // When
        List<VaccinationSchemeResponseDTO> result = vaccinationSchemeService.getSchemesByVaccine(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar esquema inexistente")
    void testDeleteScheme_NotFound() {
        // Given
        when(vaccinationSchemeRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> vaccinationSchemeService.deleteScheme(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Esquema no encontrado: 999");

        verify(vaccinationSchemeRepository, never()).deleteById(anyLong());
    }
}
