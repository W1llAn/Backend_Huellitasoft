package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.pet.PetCreateDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetResponseDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetUpdateDTO;
import huellitassoft_web.huellitasoft.entity.Client;
import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.entity.Race;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.ClientState;
import huellitassoft_web.huellitasoft.enums.Sex;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.repository.ClientRepository;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.RaceRepository;
import huellitassoft_web.huellitasoft.service.impl.PetServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para PetService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Integration Tests")
class PetServiceIntegrationTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private PetServiceImpl petService;

    private User testUser;
    private Client testClient;
    private Specie testSpecie;
    private Race testRace;
    private Pet testPet;
    private PetCreateDTO petCreateDTO;
    private PetUpdateDTO petUpdateDTO;

    @BeforeEach
    void setUp() {
        // Usuario de prueba
        testUser = User.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .username("testuser")
                .contrasena("password123")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        // Cliente de prueba
        testClient = Client.builder()
                .idCliente(1L)
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("juan.garcia@example.com")
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .estado(ClientState.ACTIVO)
                .usuario(testUser)
                .build();

        // Especie de prueba
        testSpecie = Specie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        // Raza de prueba
        testRace = Race.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .specie(testSpecie)
                .build();

        // Mascota de prueba
        testPet = Pet.builder()
                .idMascota(1L)
                .nombre("Max")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .sexo(Sex.M)
                .estado(true)
                .imagen("http://cloudinary.com/image.jpg")
                .observaciones("Mascota saludable")
                .eliminado(false)
                .cliente(testClient)
                .raza(testRace)
                .build();

        // DTO de creación
        petCreateDTO = PetCreateDTO.builder()
                .nombre("Max")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .sexo(Sex.M)
                .estado(true)
                .imagen("base64image")
                .observaciones("Mascota saludable")
                .idCliente(1L)
                .idRaza(1L)
                .build();

        // DTO de actualización
        petUpdateDTO = PetUpdateDTO.builder()
                .nombre("Max Updated")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .sexo(Sex.M)
                .estado(true)
                .observaciones("Mascota actualizada")
                .eliminado(false)
                .build();
    }

    @Test
    @DisplayName("Debe obtener todas las mascotas no eliminadas exitosamente")
    void testGetAllPet_Success() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        when(petRepository.findByEliminadoFalse()).thenReturn(pets);

        // When
        List<PetResponseDTO> result = petService.getAllPet();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Max");
        verify(petRepository, times(1)).findByEliminadoFalse();
    }

    @Test
    @DisplayName("Debe obtener mascota por ID exitosamente")
    void testGetPetById_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // When
        PetResponseDTO result = petService.getPetById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdMascota()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Max");
        assertThat(result.getSexo()).isEqualTo(Sex.M);
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la mascota no existe")
    void testGetPetById_NotFound() {
        // Given
        when(petRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.getPetById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(petRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener mascotas por cliente exitosamente")
    void testGetPetsByClient_Success() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        when(petRepository.findByClienteIdCliente(1L)).thenReturn(pets);

        // When
        List<PetResponseDTO> result = petService.getPetsByClient(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdCliente()).isEqualTo(1L);
        verify(petRepository, times(1)).findByClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe crear mascota exitosamente")
    void testCreatePet_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(cloudinaryService.uploadImageMascotas(anyString()))
                .thenReturn("http://cloudinary.com/image.jpg");
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        // When
        PetResponseDTO result = petService.createPet(petCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Max");
        assertThat(result.getIdCliente()).isEqualTo(1L);
        assertThat(result.getIdRaza()).isEqualTo(1L);
        verify(clientRepository, times(1)).findById(1L);
        verify(raceRepository, times(1)).findById(1L);
        verify(cloudinaryService, times(1)).uploadImageMascotas(anyString());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe al crear mascota")
    void testCreatePet_ClientNotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.createPet(petCreateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");

        verify(clientRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la raza no existe al crear mascota")
    void testCreatePet_RaceNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(raceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.createPet(petCreateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Raza no encontrada");

        verify(clientRepository, times(1)).findById(1L);
        verify(raceRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe actualizar mascota exitosamente")
    void testUpdatePet_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        // When
        PetResponseDTO result = petService.updatePet(1L, petUpdateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository, times(1)).findById(1L);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la mascota no existe al actualizar")
    void testUpdatePet_NotFound() {
        // Given
        when(petRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.updatePet(999L, petUpdateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(petRepository, times(1)).findById(999L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay mascotas")
    void testGetAllPet_EmptyList() {
        // Given
        when(petRepository.findByEliminadoFalse()).thenReturn(List.of());

        // When
        List<PetResponseDTO> result = petService.getAllPet();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(petRepository, times(1)).findByEliminadoFalse();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el cliente no tiene mascotas")
    void testGetPetsByClient_EmptyList() {
        // Given
        when(petRepository.findByClienteIdCliente(1L)).thenReturn(List.of());

        // When
        List<PetResponseDTO> result = petService.getPetsByClient(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(petRepository, times(1)).findByClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe crear mascota sin imagen")
    void testCreatePet_WithoutImage() {
        // Given
        petCreateDTO.setImagen(null);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(raceRepository.findById(1L)).thenReturn(Optional.of(testRace));
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        // When
        PetResponseDTO result = petService.createPet(petCreateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(cloudinaryService, never()).uploadImageMascotas(anyString());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe actualizar mascota con nueva imagen (eliminando la anterior)")
    void testUpdatePet_WithNewImage_Success() {
        // Given
        testPet.setImagen("https://res.cloudinary.com/old-image.jpg");
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(cloudinaryService.uploadImageMascotas(anyString()))
                .thenReturn("https://res.cloudinary.com/new-image.jpg");
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        PetUpdateDTO updateDTO = PetUpdateDTO.builder()
                .imagen("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...") // base64 simulado
                .build();

        // When
        PetResponseDTO result = petService.updatePet(1L, updateDTO);

        // Then
        assertThat(result.getImagen()).isEqualTo("https://res.cloudinary.com/new-image.jpg");
        verify(cloudinaryService, times(1)).deleteImageMascotas("https://res.cloudinary.com/old-image.jpg");
        verify(cloudinaryService, times(1)).uploadImageMascotas(anyString());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe actualizar cliente y raza de la mascota")
    void testUpdatePet_ChangeClientAndRace_Success() {
        // Given - Crear usuario para el nuevo cliente
        User newClientUser = User.builder()
                .idUsuario(999L)
                .username("anaperez")
                .email("ana@example.com")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .build();

        Client newClient = Client.builder()
                .idCliente(99L)
                .nombres("Ana María")
                .apellidos("Pérez")
                .usuario(newClientUser)  // ¡¡AQUÍ ESTABA EL PROBLEMA!!
                .build();

        Race newRace = Race.builder()
                .idRaza(88L)
                .nombre("Golden Retriever")
                .specie(Specie.builder().idEspecie(1L).nombre("Canino").build())
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(clientRepository.findById(99L)).thenReturn(Optional.of(newClient));
        when(raceRepository.findById(88L)).thenReturn(Optional.of(newRace));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        PetUpdateDTO updateDTO = PetUpdateDTO.builder()
                .idCliente(99L)
                .idRaza(88L)
                .nombre("Rex")
                .build();

        // When
        PetResponseDTO result = petService.updatePet(1L, updateDTO);

        // Then
        assertThat(result.getIdCliente()).isEqualTo(99L);
        assertThat(result.getIdRaza()).isEqualTo(88L);
        assertThat(result.getNombre()).isEqualTo("Rex");
        assertThat(result.getNombreCliente()).isEqualTo("Ana María Pérez");
        assertThat(result.getNombreRaza()).isEqualTo("Golden Retriever");
    }

    @Test
    @DisplayName("Debe eliminar mascota y su imagen de Cloudinary exitosamente")
    void testDeletePet_Success_WithImage() {
        // Given
        testPet.setImagen("https://res.cloudinary.com/pet123.jpg");
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        doNothing().when(petRepository).deleteById(1L);

        // When
        petService.deletePet(1L);

        // Then
        verify(cloudinaryService, times(1)).deleteImageMascotas("https://res.cloudinary.com/pet123.jpg");
        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe eliminar mascota sin imagen exitosamente")
    void testDeletePet_Success_WithoutImage() {
        // Given
        testPet.setImagen(null);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        doNothing().when(petRepository).deleteById(1L);

        // When
        petService.deletePet(1L);

        // Then
        verify(cloudinaryService, never()).deleteImageMascotas(anyString());
        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar si la mascota no existe")
    void testDeletePet_NotFound() {
        // Given
        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.deletePet(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Mascota no encontrada");

        verify(petRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe obtener mascotas por raza exitosamente")
    void testGetPetsByRace_Success() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        when(petRepository.findByRazaIdRaza(1L)).thenReturn(pets);

        // When
        List<PetResponseDTO> result = petService.getPetsByRace(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdRaza()).isEqualTo(1L);
        assertThat(result.get(0).getNombreRaza()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("Debe obtener mascotas por sexo exitosamente")
    void testGetPetsBySex_Success() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        when(petRepository.findBySexo(Sex.M)).thenReturn(pets);

        // When
        List<PetResponseDTO> result = petService.getPetsBySex(Sex.M);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSexo()).isEqualTo(Sex.M);
    }

    @Test
    @DisplayName("Debe obtener mascotas por estado (activas/inactivas) exitosamente")
    void testGetPetsByState_Success() {
        // Given
        List<Pet> activePets = Arrays.asList(testPet);
        when(petRepository.findByEstado(true)).thenReturn(activePets);

        // When
        List<PetResponseDTO> result = petService.getPetsByState(true);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isTrue();
    }

    @Test
    @DisplayName("Debe contar correctamente las mascotas de un cliente")
    void testCountPetsByClient_Success() {
        // Given
        when(petRepository.countByClienteIdCliente(1L)).thenReturn(5L);

        // When
        long count = petService.countPetsByClient(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(petRepository, times(1)).countByClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe eliminar todas las mascotas de un cliente")
    void testDeletePetsByClient_Success() {
        // Given
        doNothing().when(petRepository).deleteByClienteIdCliente(1L);

        // When
        petService.deletePetsByClient(1L);

        // Then
        verify(petRepository, times(1)).deleteByClienteIdCliente(1L);
    }

    @Test
    @DisplayName("Debe marcar mascota como eliminada lógicamente al actualizar")
    void testUpdatePet_LogicalDelete_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        PetUpdateDTO updateDTO = PetUpdateDTO.builder()
                .eliminado(true)
                .build();

        // When
        PetResponseDTO result = petService.updatePet(1L, updateDTO);

        // Then
        assertThat(result.getEliminado()).isTrue();
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar cliente si el nuevo cliente no existe")
    void testUpdatePet_ChangeClient_NotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        PetUpdateDTO updateDTO = PetUpdateDTO.builder()
                .idCliente(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> petService.updatePet(1L, updateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar raza si la nueva raza no existe")
    void testUpdatePet_ChangeRace_NotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(raceRepository.findById(999L)).thenReturn(Optional.empty());

        PetUpdateDTO updateDTO = PetUpdateDTO.builder()
                .idRaza(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> petService.updatePet(1L, updateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Raza no encontrada");
    }
}

