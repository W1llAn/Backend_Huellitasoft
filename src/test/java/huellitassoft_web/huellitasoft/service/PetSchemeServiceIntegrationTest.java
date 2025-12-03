package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeResponseDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeUpdateStateDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import huellitassoft_web.huellitasoft.enums.Sex;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.PetSchemeRepository;
import huellitassoft_web.huellitasoft.repository.VaccinationSchemeRepository;
import huellitassoft_web.huellitasoft.service.impl.PetSchemeServiceImpl;
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
@DisplayName("PetSchemeService Integration Tests")
class PetSchemeServiceIntegrationTest {

    @Mock
    private PetSchemeRepository petSchemeRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VaccinationSchemeRepository vaccinationSchemeRepository;

    @InjectMocks
    private PetSchemeServiceImpl petSchemeService;

    private Pet testPet;
    private VaccinationScheme testScheme;
    private PetScheme testPetScheme;
    private PetSchemeCreateDTO petSchemeCreateDTO;

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

        // Vacuna
        Vaccine testVaccine = Vaccine.builder()
                .idVacuna(1L)
                .nombre("Parvovirus")
                .descripcion("Vacuna contra el parvovirus canino")
                .specie(specie)
                .build();

        // Esquema de vacunación
        testScheme = VaccinationScheme.builder()
                .idEsquema(1L)
                .vaccine(testVaccine)
                .dosisNumero(1)
                .edadSemanas(8)
                .observaciones("Primera dosis")
                .build();

        // PetScheme
        testPetScheme = PetScheme.builder()
                .idMascotaEsquema(1L)
                .pet(testPet)
                .scheme(testScheme)
                .estado(PetSchemeState.ACTIVO)
                .build();

        // DTO de creación
        petSchemeCreateDTO = PetSchemeCreateDTO.builder()
                .idMascota(1L)
                .idEsquema(1L)
                .estado(PetSchemeState.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("Debe asignar esquema a mascota exitosamente")
    void testAssignSchemeToPet_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(petSchemeRepository.save(any(PetScheme.class))).thenReturn(testPetScheme);

        // When
        PetSchemeResponseDTO result = petSchemeService.assignSchemeToPet(petSchemeCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdMascotaEsquema()).isEqualTo(1L);
        assertThat(result.getEstado()).isEqualTo(PetSchemeState.ACTIVO);
        verify(petSchemeRepository, times(1)).save(any(PetScheme.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al asignar esquema con mascota inexistente")
    void testAssignSchemeToPet_PetNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petSchemeService.assignSchemeToPet(petSchemeCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(petSchemeRepository, never()).save(any(PetScheme.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al asignar esquema inexistente")
    void testAssignSchemeToPet_SchemeNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petSchemeService.assignSchemeToPet(petSchemeCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Esquema no encontrado");

        verify(petSchemeRepository, never()).save(any(PetScheme.class));
    }

    @Test
    @DisplayName("Debe obtener esquema por ID exitosamente")
    void testGetPetSchemeById_Success() {
        // Given
        when(petSchemeRepository.findById(1L)).thenReturn(Optional.of(testPetScheme));

        // When
        PetSchemeResponseDTO result = petSchemeService.getPetSchemeById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdMascotaEsquema()).isEqualTo(1L);
        assertThat(result.getNombreMascota()).isEqualTo("Max");
        verify(petSchemeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar esquema inexistente")
    void testGetPetSchemeById_NotFound() {
        // Given
        when(petSchemeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petSchemeService.getPetSchemeById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Relación mascota-esquema no encontrada");

        verify(petSchemeRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener esquemas por mascota")
    void testGetPetSchemes_Success() {
        // Given
        when(petSchemeRepository.findByPet_IdMascota(1L)).thenReturn(Arrays.asList(testPetScheme));

        // When
        List<PetSchemeResponseDTO> result = petSchemeService.getPetSchemes(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdMascota()).isEqualTo(1L);
        verify(petSchemeRepository, times(1)).findByPet_IdMascota(1L);
    }

    @Test
    @DisplayName("Debe desasignar esquema exitosamente")
    void testUnassignScheme_Success() {
        // Given
        when(petSchemeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(petSchemeRepository).deleteById(1L);

        // When
        petSchemeService.unassignScheme(1L);

        // Then
        verify(petSchemeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al asignar esquema si la mascota ya tiene ese esquema")
    void testAssignScheme_DuplicatePetAndScheme_ThrowsException() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(petSchemeRepository.existsByPet_IdMascotaAndScheme_IdEsquema(1L, 1L)).thenReturn(true);

        PetSchemeCreateDTO dto = PetSchemeCreateDTO.builder()
                .idMascota(1L)
                .idEsquema(1L)
                .build();

        // When & Then
        assertThatThrownBy(() -> petSchemeService.assignSchemeToPet(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("La mascota ya tiene asignado este esquema");

        verify(petSchemeRepository, never()).save(any(PetScheme.class));
    }

    @Test
    @DisplayName("Debe actualizar estado del esquema asignado a una mascota")
    void testUpdateSchemeState_Success() {
        // Given
        PetScheme existing = PetScheme.builder()
                .idMascotaEsquema(1L)
                .pet(testPet)
                .scheme(testScheme)
                .estado(PetSchemeState.ACTIVO)
                .build();

        when(petSchemeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(petSchemeRepository.save(any(PetScheme.class))).thenAnswer(i -> i.getArgument(0));

        PetSchemeUpdateStateDTO dto = PetSchemeUpdateStateDTO.builder()
                .estado(PetSchemeState.COMPLETADO)
                .build();

        // When
        PetSchemeResponseDTO result = petSchemeService.updateSchemeState(1L, dto);

        // Then
        assertThat(result.getEstado()).isEqualTo(PetSchemeState.COMPLETADO);
        verify(petSchemeRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar estado si la relación mascota-esquema no existe")
    void testUpdateSchemeState_NotFound() {
        // Given
        when(petSchemeRepository.findById(999L)).thenReturn(Optional.empty());

        PetSchemeUpdateStateDTO dto = PetSchemeUpdateStateDTO.builder()
                .estado(PetSchemeState.COMPLETADO)
                .build();

        // When & Then
        assertThatThrownBy(() -> petSchemeService.updateSchemeState(999L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Relación mascota-esquema no encontrada");

        verify(petSchemeRepository, never()).save(any(PetScheme.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al desasignar esquema inexistente")
    void testUnassignScheme_NotFound() {
        // Given
        when(petSchemeRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> petSchemeService.unassignScheme(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Relación mascota-esquema no encontrada");

        verify(petSchemeRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe asignar esquema con estado por defecto ACTIVO si no se envía")
    void testAssignScheme_DefaultStateActive_WhenNull() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccinationSchemeRepository.findById(1L)).thenReturn(Optional.of(testScheme));
        when(petSchemeRepository.existsByPet_IdMascotaAndScheme_IdEsquema(1L, 1L)).thenReturn(false);
        when(petSchemeRepository.save(any(PetScheme.class))).thenAnswer(i -> {
            PetScheme ps = i.getArgument(0);
            ps.setIdMascotaEsquema(1L);
            return ps;
        });

        PetSchemeCreateDTO dto = PetSchemeCreateDTO.builder()
                .idMascota(1L)
                .idEsquema(1L)
                .estado(null) // ← No enviamos estado
                .build();

        // When
        PetSchemeResponseDTO result = petSchemeService.assignSchemeToPet(dto);

        // Then
        assertThat(result.getEstado()).isEqualTo(PetSchemeState.ACTIVO);
    }
}
