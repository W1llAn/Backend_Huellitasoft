package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.vaccine.VaccineCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineResponseDTO;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.entity.Vaccine;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SpecieRepository;
import huellitassoft_web.huellitasoft.repository.VaccineRepository;
import huellitassoft_web.huellitasoft.service.impl.VaccineServiceImpl;
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
@DisplayName("VaccineService Integration Tests")
class VaccineServiceIntegrationTest {

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private SpecieRepository specieRepository;

    @InjectMocks
    private VaccineServiceImpl vaccineService;

    private Specie testSpecie;
    private Vaccine testVaccine;
    private VaccineCreateDTO vaccineCreateDTO;

    @BeforeEach
    void setUp() {
        testSpecie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        testVaccine = Vaccine.builder()
                .idVacuna(1L)
                .nombre("Parvovirus")
                .descripcion("Vacuna contra el parvovirus canino")
                .specie(testSpecie)
                .build();

        vaccineCreateDTO = VaccineCreateDTO.builder()
                .nombre("Parvovirus")
                .descripcion("Vacuna contra el parvovirus canino")
                .idEspecie(1L)
                .build();
    }

    @Test
    @DisplayName("Debe crear una vacuna exitosamente")
    void testCreateVaccine_Success() {
        // Given
        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));
        when(vaccineRepository.save(any(Vaccine.class))).thenReturn(testVaccine);

        // When
        VaccineResponseDTO result = vaccineService.createVaccine(vaccineCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdVacuna()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Parvovirus");
        verify(vaccineRepository, times(1)).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear vacuna con especie inexistente")
    void testCreateVaccine_SpecieNotFound() {
        // Given
        when(specieRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vaccineService.createVaccine(vaccineCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada");

        verify(vaccineRepository, never()).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe obtener vacuna por ID exitosamente")
    void testGetVaccineById_Success() {
        // Given
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));

        // When
        VaccineResponseDTO result = vaccineService.getVaccineById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdVacuna()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Parvovirus");
        verify(vaccineRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar vacuna inexistente")
    void testGetVaccineById_NotFound() {
        // Given
        when(vaccineRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vaccineService.getVaccineById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacuna no encontrada");

        verify(vaccineRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener todas las vacunas")
    void testGetAllVaccines_Success() {
        // Given
        when(vaccineRepository.findAll()).thenReturn(Arrays.asList(testVaccine));

        // When
        List<VaccineResponseDTO> result = vaccineService.getAllVaccines();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Parvovirus");
        verify(vaccineRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar vacuna exitosamente")
    void testUpdateVaccine_Success() {
        // Given
        VaccineCreateDTO updateDTO = VaccineCreateDTO.builder()
                .nombre("Parvovirus CPV-2")
                .descripcion("Vacuna actualizada contra parvovirus")
                .idEspecie(1L)
                .build();

        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccineRepository.save(any(Vaccine.class))).thenReturn(testVaccine);

        // When
        VaccineResponseDTO result = vaccineService.updateVaccine(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(vaccineRepository, times(1)).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe eliminar vacuna exitosamente")
    void testDeleteVaccine_Success() {
        // Given
        when(vaccineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vaccineRepository).deleteById(1L);

        // When
        vaccineService.deleteVaccine(1L);

        // Then
        verify(vaccineRepository, times(1)).deleteById(1L);
    }

    // ====================== TESTS FALTANTES PARA VACCINESERVICE ======================

    @Test
    @DisplayName("Debe lanzar excepción al crear vacuna con nombre duplicado")
    void testCreateVaccine_NameAlreadyExists() {
        // Given
        when(vaccineRepository.existsByNombre("Parvovirus")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> vaccineService.createVaccine(vaccineCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una vacuna con ese nombre: Parvovirus");

        verify(vaccineRepository, never()).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe actualizar vacuna con nuevo nombre y nueva especie exitosamente")
    void testUpdateVaccine_ChangeNameAndSpecie_Success() {
        // Given - Nueva especie
        Specie felino = Specie.builder()
                .idEspecie(2L)
                .nombre("Felino")
                .build();

        VaccineCreateDTO updateDTO = VaccineCreateDTO.builder()
                .nombre("Leucemia Felina")
                .descripcion("Vacuna contra leucemia en gatos")
                .idEspecie(2L)
                .build();

        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(specieRepository.findById(2L)).thenReturn(Optional.of(felino));
        when(vaccineRepository.save(any(Vaccine.class))).thenAnswer(i -> i.getArgument(0));

        // When
        VaccineResponseDTO result = vaccineService.updateVaccine(1L, updateDTO);

        // Then
        assertThat(result.getNombre()).isEqualTo("Leucemia Felina");
        assertThat(result.getIdEspecie()).isEqualTo(2L);
        verify(specieRepository, times(1)).findById(2L);
        verify(vaccineRepository, times(1)).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo nombre ya existe (otra vacuna)")
    void testUpdateVaccine_NameAlreadyExists() {
        // Given
        VaccineCreateDTO updateDTO = VaccineCreateDTO.builder()
                .nombre("Rabia")  // nombre que ya existe en otra vacuna
                .build();

        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccineRepository.existsByNombre("Rabia")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> vaccineService.updateVaccine(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una vacuna con el nombre: Rabia");

        verify(vaccineRepository, never()).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si la nueva especie no existe")
    void testUpdateVaccine_SpecieNotFound() {
        // Given
        VaccineCreateDTO updateDTO = VaccineCreateDTO.builder()
                .idEspecie(999L)
                .build();

        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(specieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vaccineService.updateVaccine(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada con ID: 999");

        verify(vaccineRepository, never()).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe mantener la especie actual cuando se envía el mismo idEspecie (sin revalidar)")
    void testUpdateVaccine_NoChangeSpecie_KeepsExisting() {
        // Given
        VaccineCreateDTO updateDTO = VaccineCreateDTO.builder()
                .nombre("Parvovirus Actualizado")
                .descripcion("Descripción actualizada")
                .idEspecie(1L)
                .build();

        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(vaccineRepository.save(any(Vaccine.class))).thenAnswer(i -> i.getArgument(0));

        // When
        VaccineResponseDTO result = vaccineService.updateVaccine(1L, updateDTO);

        // Then
        assertThat(result.getIdEspecie()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Parvovirus Actualizado");

        verify(specieRepository, never()).findById(anyLong()); // Correcto: no buscó
        verify(vaccineRepository, times(1)).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Debe obtener vacunas por especie exitosamente")
    void testGetVaccinesBySpecie_Success() {
        // Given
        List<Vaccine> vaccines = Arrays.asList(testVaccine);
        when(specieRepository.existsById(1L)).thenReturn(true);
        when(vaccineRepository.findBySpecie_IdEspecie(1L)).thenReturn(vaccines);

        // When
        List<VaccineResponseDTO> result = vaccineService.getVaccinesBySpecie(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdEspecie()).isEqualTo(1L);
        verify(vaccineRepository, times(1)).findBySpecie_IdEspecie(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar vacunas por especie inexistente")
    void testGetVaccinesBySpecie_SpecieNotFound() {
        // Given
        when(specieRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> vaccineService.getVaccinesBySpecie(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada con ID: 999");

        verify(vaccineRepository, never()).findBySpecie_IdEspecie(anyLong());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay vacunas")
    void testGetAllVaccines_EmptyList() {
        // Given
        when(vaccineRepository.findAll()).thenReturn(List.of());

        // When
        List<VaccineResponseDTO> result = vaccineService.getAllVaccines();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía al buscar vacunas por especie sin resultados")
    void testGetVaccinesBySpecie_EmptyList() {
        // Given
        when(specieRepository.existsById(1L)).thenReturn(true);
        when(vaccineRepository.findBySpecie_IdEspecie(1L)).thenReturn(List.of());

        // When
        List<VaccineResponseDTO> result = vaccineService.getVaccinesBySpecie(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar vacuna inexistente")
    void testDeleteVaccine_NotFound() {
        // Given
        when(vaccineRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> vaccineService.deleteVaccine(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacuna no encontrada con ID: 999");

        verify(vaccineRepository, never()).deleteById(anyLong());
    }
}
