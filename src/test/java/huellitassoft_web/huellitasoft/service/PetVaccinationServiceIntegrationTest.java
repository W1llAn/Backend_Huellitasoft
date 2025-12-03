package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationCreateDTO;
import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationResponseDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.Sex;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.PetVaccinationRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.repository.VaccineRepository;
import huellitassoft_web.huellitasoft.service.impl.PetVaccinationServiceImpl;
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
@DisplayName("PetVaccinationService Integration Tests")
class PetVaccinationServiceIntegrationTest {

    @Mock
    private PetVaccinationRepository petVaccinationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PetVaccinationServiceImpl petVaccinationService;

    private Pet testPet;
    private Vaccine testVaccine;
    private User testVeterinarian;
    private PetVaccination testPetVaccination;
    private PetVaccinationCreateDTO petVaccinationCreateDTO;

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
        testVaccine = Vaccine.builder()
                .idVacuna(1L)
                .nombre("Parvovirus")
                .descripcion("Vacuna contra el parvovirus canino")
                .specie(specie)
                .build();

        // PetVaccination
        testPetVaccination = PetVaccination.builder()
                .idVacunacion(1L)
                .pet(testPet)
                .vaccine(testVaccine)
                .fechaAplicada(LocalDateTime.now())
                .user(testVeterinarian)
                .build();

        // DTO de creación
        petVaccinationCreateDTO = PetVaccinationCreateDTO.builder()
                .idMascota(1L)
                .idVacuna(1L)
                .build();
    }

    @Test
    @DisplayName("Debe crear una vacunación exitosamente")
    void testCreate_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testVeterinarian));
        when(petVaccinationRepository.save(any(PetVaccination.class))).thenReturn(testPetVaccination);

        // When
        PetVaccinationResponseDTO result = petVaccinationService.create(petVaccinationCreateDTO, "auth0|1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdVacunacion()).isEqualTo(1L);
        assertThat(result.getNombreMascota()).isEqualTo("Max");
        assertThat(result.getNombreVacuna()).isEqualTo("Parvovirus");
        verify(petVaccinationRepository, times(1)).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear vacunación con mascota inexistente")
    void testCreate_PetNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "auth0|1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear vacunación con vacuna inexistente")
    void testCreate_VaccineNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "auth0|1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacuna no encontrada");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear vacunación con usuario inexistente")
    void testCreate_UserNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "auth0|1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe obtener vacunación por ID exitosamente")
    void testGetById_Success() {
        // Given
        when(petVaccinationRepository.findById(1L)).thenReturn(Optional.of(testPetVaccination));

        // When
        PetVaccinationResponseDTO result = petVaccinationService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdVacunacion()).isEqualTo(1L);
        assertThat(result.getNombreMascota()).isEqualTo("Max");
        verify(petVaccinationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar vacunación inexistente")
    void testGetById_NotFound() {
        // Given
        when(petVaccinationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petVaccinationService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vacunación no encontrada");

        verify(petVaccinationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener vacunaciones por mascota")
    void testGetByPet_Success() {
        // Given
        when(petVaccinationRepository.findByPet_IdMascota(1L)).thenReturn(Arrays.asList(testPetVaccination));

        // When
        List<PetVaccinationResponseDTO> result = petVaccinationService.getByPet(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdMascota()).isEqualTo(1L);
        verify(petVaccinationRepository, times(1)).findByPet_IdMascota(1L);
    }

    @Test
    @DisplayName("Debe obtener vacunaciones por vacuna")
    void testGetByVaccine_Success() {
        // Given
        when(petVaccinationRepository.findByVaccine_IdVacuna(1L)).thenReturn(Arrays.asList(testPetVaccination));

        // When
        List<PetVaccinationResponseDTO> result = petVaccinationService.getByVaccine(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdVacuna()).isEqualTo(1L);
        verify(petVaccinationRepository, times(1)).findByVaccine_IdVacuna(1L);
    }

    @Test
    @DisplayName("Debe eliminar vacunación exitosamente")
    void testDelete_Success() {
        // Given
        when(petVaccinationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(petVaccinationRepository).deleteById(1L);

        // When
        petVaccinationService.delete(1L);

        // Then
        verify(petVaccinationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no es veterinario")
    void testCreate_UserIsNotVeterinarian() {
        // Given
        User nonVetUser = User.builder()
                .idUsuario(2L)
                .email("client@example.com")
                .username("cliente")
                .contrasena("password")
                .rol(UserRol.ROLE_CLIENTE) // No es veterinario
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(userRepository.findById(2L)).thenReturn(Optional.of(nonVetUser));

        // When & Then
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "auth0|2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El usuario autenticado no tiene rol de VETERINARIO");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el rol del usuario es null")
    void testCreate_UserRoleIsNull() {
        // Given
        User userWithoutRole = User.builder()
                .idUsuario(3L)
                .email("norol@example.com")
                .username("sinrol")
                .contrasena("password")
                .rol(null) // Rol null
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));
        when(userRepository.findById(3L)).thenReturn(Optional.of(userWithoutRole));

        // When & Then
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "auth0|3"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("El usuario autenticado no tiene rol de VETERINARIO");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si auth0Subject tiene formato inválido (NumberFormatException)")
    void testCreate_InvalidAuth0SubjectFormat() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));

        // When & Then - auth0Subject con texto no numérico después de "auth0|"
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "auth0|abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de subject inválido");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si auth0Subject no tiene formato auth0|ID")
    void testCreate_Auth0SubjectWithoutPrefix() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));

        // When & Then - auth0Subject sin el prefijo "auth0|"
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, "123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subject no tiene formato auth0|ID");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si auth0Subject es null")
    void testCreate_Auth0SubjectIsNull() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(testVaccine));

        // When & Then - auth0Subject null
        assertThatThrownBy(() -> petVaccinationService.create(petVaccinationCreateDTO, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subject no tiene formato auth0|ID");

        verify(petVaccinationRepository, never()).save(any(PetVaccination.class));
    }
}
