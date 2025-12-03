package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.race.RaceCreateDTO;
import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;
import huellitassoft_web.huellitasoft.entity.Race;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.RaceRepository;
import huellitassoft_web.huellitasoft.repository.SpecieRepository;
import huellitassoft_web.huellitasoft.service.impl.RaceServiceImpl;
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
@DisplayName("RaceService Integration Tests")
class RaceServiceIntegrationTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private SpecieRepository specieRepository;

    @InjectMocks
    private RaceServiceImpl raceService;

    private Specie testSpecie;
    private Race testRace;
    private RaceCreateDTO raceCreateDTO;

    @BeforeEach
    void setUp() {
        testSpecie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        testRace = Race.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .specie(testSpecie)
                .build();

        raceCreateDTO = RaceCreateDTO.builder()
                .nombre("Labrador")
                .idEspecie(1L)
                .build();
    }

    @Test
    @DisplayName("Debe crear una raza exitosamente")
    void testCreateRace_Success() {
        // Given
        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));
        when(raceRepository.save(any(Race.class))).thenReturn(testRace);

        // When
        RaceResponseDTO result = raceService.createRace(raceCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdRaza()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Labrador");
        verify(raceRepository, times(1)).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear raza con especie inexistente")
    void testCreateRace_SpecieNotFound() {
        // Given
        when(specieRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> raceService.createRace(raceCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada");

        verify(raceRepository, never()).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe obtener raza por ID exitosamente")
    void testGetRaceById_Success() {
        // Given
        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));

        // When
        RaceResponseDTO result = raceService.getRaceById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdRaza()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Labrador");
        verify(raceRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar raza inexistente")
    void testGetRaceById_NotFound() {
        // Given
        when(raceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> raceService.getRaceById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raza no encontrada");

        verify(raceRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener todas las razas")
    void testGetAllRaces_Success() {
        // Given
        when(raceRepository.findAll()).thenReturn(Arrays.asList(testRace));

        // When
        List<RaceResponseDTO> result = raceService.getAllRaces();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Labrador");
        verify(raceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener razas por especie")
    void testGetRacesBySpecie_Success() {
        // Given
        when(specieRepository.existsById(1L)).thenReturn(true);
        when(raceRepository.findBySpecie_IdEspecie(1L)).thenReturn(Arrays.asList(testRace));

        // When
        List<RaceResponseDTO> result = raceService.getRacesBySpecie(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdEspecie()).isEqualTo(1L);
        verify(raceRepository, times(1)).findBySpecie_IdEspecie(1L);
    }

    @Test
    @DisplayName("Debe actualizar raza exitosamente")
    void testUpdateRace_Success() {
        // Given
        RaceCreateDTO updateDTO = RaceCreateDTO.builder()
                .nombre("Labrador Retriever")
                .idEspecie(1L)
                .build();

        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(raceRepository.save(any(Race.class))).thenReturn(testRace);

        // When
        RaceResponseDTO result = raceService.updateRace(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(raceRepository, times(1)).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe eliminar raza exitosamente")
    void testDeleteRace_Success() {
        // Given
        when(raceRepository.existsById(1L)).thenReturn(true);
        doNothing().when(raceRepository).deleteById(1L);

        // When
        raceService.deleteRace(1L);

        // Then
        verify(raceRepository, times(1)).deleteById(1L);
    }

    // ====================== TESTS FALTANTES PARA RACESERVICE ======================

    @Test
    @DisplayName("Debe lanzar excepción al crear raza con nombre duplicado")
    void testCreateRace_NameAlreadyExists() {
        // Given
        when(raceRepository.existsByNombre("Labrador")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> raceService.createRace(raceCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una raza con el nombre: Labrador");

        verify(raceRepository, never()).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe actualizar raza con nuevo nombre y nueva especie exitosamente")
    void testUpdateRace_ChangeNameAndSpecie_Success() {
        // Given - Nueva especie
        Specie felino = Specie.builder()
                .idEspecie(2L)
                .nombre("Felino")
                .build();

        RaceCreateDTO updateDTO = RaceCreateDTO.builder()
                .nombre("Persa")
                .idEspecie(2L)
                .build();

        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(specieRepository.findById(2L)).thenReturn(Optional.of(felino));
        when(raceRepository.save(any(Race.class))).thenAnswer(i -> i.getArgument(0));

        // When
        RaceResponseDTO result = raceService.updateRace(1L, updateDTO);

        // Then
        assertThat(result.getNombre()).isEqualTo("Persa");
        assertThat(result.getIdEspecie()).isEqualTo(2L);
        assertThat(result.getNombreEspecie()).isEqualTo("Felino");
        verify(specieRepository, times(1)).findById(2L);
        verify(raceRepository, times(1)).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo nombre ya existe (otra raza)")
    void testUpdateRace_NameAlreadyExists() {
        // Given
        RaceCreateDTO updateDTO = RaceCreateDTO.builder()
                .nombre("Golden Retriever")  // nombre que ya existe
                .idEspecie(1L)
                .build();

        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(raceRepository.existsByNombre("Golden Retriever")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> raceService.updateRace(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una raza con el nombre: Golden Retriever");

        verify(raceRepository, never()).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si la nueva especie no existe")
    void testUpdateRace_SpecieNotFound() {
        // Given
        RaceCreateDTO updateDTO = RaceCreateDTO.builder()
                .nombre("Labrador")
                .idEspecie(999L)
                .build();

        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(specieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> raceService.updateRace(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada con ID: 999");

        verify(raceRepository, never()).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe mantener la especie actual cuando se envía el mismo idEspecie (sin revalidar)")
    void testUpdateRace_NoChangeSpecie_KeepsExisting() {
        // Given
        RaceCreateDTO updateDTO = RaceCreateDTO.builder()
                .nombre("Labrador Retriever")
                .idEspecie(1L)  // mismo idEspecie → no debe hacer nada con la especie
                .build();

        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(raceRepository.save(any(Race.class))).thenAnswer(i -> i.getArgument(0));

        // When
        RaceResponseDTO result = raceService.updateRace(1L, updateDTO);

        // Then
        assertThat(result.getIdEspecie()).isEqualTo(1L);
        assertThat(result.getNombreEspecie()).isEqualTo("Canino");

        // CORREGIDO: no se llama a specieRepository cuando el idEspecie es el mismo
        verify(specieRepository, never()).findById(anyLong());
        verify(raceRepository, times(1)).save(any(Race.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar razas por especie inexistente")
    void testGetRacesBySpecie_SpecieNotFound() {
        // Given
        when(specieRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> raceService.getRacesBySpecie(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada con ID: 999");

        verify(raceRepository, never()).findBySpecie_IdEspecie(anyLong());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay razas")
    void testGetAllRaces_EmptyList() {
        // Given
        when(raceRepository.findAll()).thenReturn(List.of());

        // When
        List<RaceResponseDTO> result = raceService.getAllRaces();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía al buscar razas por especie sin resultados")
    void testGetRacesBySpecie_EmptyList() {
        // Given
        when(specieRepository.existsById(1L)).thenReturn(true);
        when(raceRepository.findBySpecie_IdEspecie(1L)).thenReturn(List.of());

        // When
        List<RaceResponseDTO> result = raceService.getRacesBySpecie(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar raza inexistente")
    void testDeleteRace_NotFound() {
        // Given
        when(raceRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> raceService.deleteRace(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raza no encontrada con ID: 999");

        verify(raceRepository, never()).deleteById(anyLong());
    }
}
