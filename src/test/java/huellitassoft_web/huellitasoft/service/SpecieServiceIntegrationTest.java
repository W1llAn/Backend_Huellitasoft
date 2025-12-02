package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.specie.SpecieCreateDTO;
import huellitassoft_web.huellitasoft.dto.specie.SpecieResponseDTO;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SpecieRepository;
import huellitassoft_web.huellitasoft.service.impl.SpecieServiceImpl;
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
@DisplayName("SpecieService Integration Tests")
class SpecieServiceIntegrationTest {

    @Mock
    private SpecieRepository specieRepository;

    @InjectMocks
    private SpecieServiceImpl specieService;

    private Specie testSpecie;
    private SpecieCreateDTO specieCreateDTO;

    @BeforeEach
    void setUp() {
        testSpecie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        specieCreateDTO = SpecieCreateDTO.builder()
                .nombre("Canino")
                .build();
    }

    @Test
    @DisplayName("Debe crear una especie exitosamente")
    void testCreateSpecie_Success() {
        // Given
        when(specieRepository.save(any(Specie.class))).thenReturn(testSpecie);

        // When
        SpecieResponseDTO result = specieService.createSpecie(specieCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdEspecie()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Canino");
        verify(specieRepository, times(1)).save(any(Specie.class));
    }

    @Test
    @DisplayName("Debe obtener especie por ID exitosamente")
    void testGetSpecieById_Success() {
        // Given
        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));

        // When
        SpecieResponseDTO result = specieService.getSpecieById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdEspecie()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Canino");
        verify(specieRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar especie inexistente")
    void testGetSpecieById_NotFound() {
        // Given
        when(specieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> specieService.getSpecieById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada");

        verify(specieRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener todas las especies")
    void testGetAllSpecies_Success() {
        // Given
        when(specieRepository.findAll()).thenReturn(Arrays.asList(testSpecie));

        // When
        List<SpecieResponseDTO> result = specieService.getAllSpecies();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Canino");
        verify(specieRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe actualizar especie exitosamente")
    void testUpdateSpecie_Success() {
        // Given
        SpecieCreateDTO updateDTO = SpecieCreateDTO.builder()
                .nombre("Canino Doméstico")
                .build();

        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));
        when(specieRepository.save(any(Specie.class))).thenReturn(testSpecie);

        // When
        SpecieResponseDTO result = specieService.updateSpecie(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(specieRepository, times(1)).save(any(Specie.class));
    }

    @Test
    @DisplayName("Debe eliminar especie exitosamente")
    void testDeleteSpecie_Success() {
        // Given
        when(specieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(specieRepository).deleteById(1L);

        // When
        specieService.deleteSpecie(1L);

        // Then
        verify(specieRepository, times(1)).deleteById(1L);
    }

    // ====================== TESTS FALTANTES PARA SPECIESERVICE ======================

    @Test
    @DisplayName("Debe lanzar excepción al crear especie con nombre duplicado")
    void testCreateSpecie_NameAlreadyExists() {
        // Given
        when(specieRepository.existsByNombre("Canino")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> specieService.createSpecie(specieCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una especie con el nombre: Canino");

        verify(specieRepository, never()).save(any(Specie.class));
    }

    @Test
    @DisplayName("Debe actualizar especie con nuevo nombre exitosamente")
    void testUpdateSpecie_ChangeName_Success() {
        // Given
        SpecieCreateDTO updateDTO = SpecieCreateDTO.builder()
                .nombre("Felino")
                .build();

        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));
        when(specieRepository.save(any(Specie.class))).thenAnswer(i -> i.getArgument(0));

        // When
        SpecieResponseDTO result = specieService.updateSpecie(1L, updateDTO);

        // Then
        assertThat(result.getNombre()).isEqualTo("Felino");
        verify(specieRepository, times(1)).save(any(Specie.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo nombre ya existe (otra especie)")
    void testUpdateSpecie_NameAlreadyExists() {
        // Given
        SpecieCreateDTO updateDTO = SpecieCreateDTO.builder()
                .nombre("Felino")
                .build();

        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));
        when(specieRepository.existsByNombre("Felino")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> specieService.updateSpecie(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una especie con el nombre: Felino");

        verify(specieRepository, never()).save(any(Specie.class));
    }

    @Test
    @DisplayName("Debe permitir actualizar con el mismo nombre (no lanza duplicado)")
    void testUpdateSpecie_SameName_NoDuplicateCheck() {
        // Given - Intentamos actualizar con el mismo nombre
        SpecieCreateDTO updateDTO = SpecieCreateDTO.builder()
                .nombre("Canino")  // mismo nombre
                .build();

        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));
        when(specieRepository.save(any(Specie.class))).thenAnswer(i -> i.getArgument(0));

        // When
        SpecieResponseDTO result = specieService.updateSpecie(1L, updateDTO);

        // Then
        assertThat(result.getNombre()).isEqualTo("Canino");
        verify(specieRepository, times(1)).save(any(Specie.class));
        // No lanza excepción porque es el mismo registro
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar especie inexistente")
    void testUpdateSpecie_NotFound() {
        // Given
        SpecieCreateDTO updateDTO = SpecieCreateDTO.builder()
                .nombre("Nuevo Nombre")
                .build();

        when(specieRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> specieService.updateSpecie(999L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada con ID: 999");

        verify(specieRepository, never()).save(any(Specie.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar especie inexistente")
    void testDeleteSpecie_NotFound() {
        // Given
        when(specieRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> specieService.deleteSpecie(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especie no encontrada con ID: 999");

        verify(specieRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay especies")
    void testGetAllSpecies_EmptyList() {
        // Given
        when(specieRepository.findAll()).thenReturn(List.of());

        // When
        List<SpecieResponseDTO> result = specieService.getAllSpecies();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe mapear correctamente el DTO de respuesta")
    void testMapToResponseDTO_CompleteMapping() {
        // Given
        when(specieRepository.findById(1L)).thenReturn(Optional.of(testSpecie));

        // When
        SpecieResponseDTO result = specieService.getSpecieById(1L);

        // Then
        assertThat(result.getIdEspecie()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Canino");
    }
}
