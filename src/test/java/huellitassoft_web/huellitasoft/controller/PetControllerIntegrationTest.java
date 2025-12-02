package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import huellitassoft_web.huellitasoft.dto.pet.PetCreateDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetResponseDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetUpdateDTO;
import huellitassoft_web.huellitasoft.enums.Sex;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para PetController
 * Utiliza MockMvc para simular peticiones HTTP
 */
@WebMvcTest(PetController.class)
@DisplayName("PetController Integration Tests")
class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private PetService petService;

    private PetResponseDTO petResponseDTO;
    private PetCreateDTO petCreateDTO;
    private PetUpdateDTO petUpdateDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        petResponseDTO = PetResponseDTO.builder()
                .idMascota(1L)
                .nombre("Max")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .sexo(Sex.M)
                .estado(true)
                .idCliente(1L)
                .idRaza(1L)
                .nombreCliente("Juan Pedro García")
                .nombreRaza("Labrador")
                .nombreEspecie("Canino")
                .idEspecie(1L)
                .observaciones("Mascota saludable")
                .eliminado(false)
                .imagen("http://cloudinary.com/image.jpg")
                .build();

        petCreateDTO = PetCreateDTO.builder()
                .nombre("Max")
                .fechaNacimiento(LocalDate.of(2020, 5, 15))
                .sexo(Sex.M)
                .estado(true)
                .observaciones("Mascota saludable")
                .idCliente(1L)
                .idRaza(1L)
                .imagen("base64image")
                .build();

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
    @DisplayName("GET /api/mascotas - Debe obtener todas las mascotas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllPets_Success() throws Exception {
        // Given
        List<PetResponseDTO> pets = Arrays.asList(petResponseDTO);
        when(petService.getAllPet()).thenReturn(pets);

        // When & Then
        mockMvc.perform(get("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idMascota", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Max")))
                .andExpect(jsonPath("$[0].sexo", is("M")));

        verify(petService, times(1)).getAllPet();
    }

    @Test
    @DisplayName("GET /api/mascotas/{id} - Debe obtener mascota por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetById_Success() throws Exception {
        // Given
        when(petService.getPetById(1L)).thenReturn(petResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota", is(1)))
                .andExpect(jsonPath("$.nombre", is("Max")))
                .andExpect(jsonPath("$.nombreRaza", is("Labrador")));

        verify(petService, times(1)).getPetById(1L);
    }

    @Test
    @DisplayName("GET /api/mascotas/{id} - Debe retornar 404 cuando la mascota no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetById_NotFound() throws Exception {
        // Given
        when(petService.getPetById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Mascota no encontrada con id: 999"));

        // When & Then
        mockMvc.perform(get("/api/mascotas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(petService, times(1)).getPetById(999L);
    }

    @Test
    @DisplayName("GET /api/mascotas/cliente/{idCliente} - Debe obtener mascotas por cliente")
    @WithMockUser(roles = "CLIENTE")
    void testGetPetsByClient_Success() throws Exception {
        // Given
        List<PetResponseDTO> pets = Arrays.asList(petResponseDTO);
        when(petService.getPetsByClient(1L)).thenReturn(pets);

        // When & Then
        mockMvc.perform(get("/api/mascotas/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente", is(1)));

        verify(petService, times(1)).getPetsByClient(1L);
    }

    @Test
    @DisplayName("POST /api/mascotas - Debe crear una nueva mascota")
    @WithMockUser(roles = "CLIENTE")
    void testCreatePet_Success() throws Exception {
        // Given
        when(petService.createPet(any(PetCreateDTO.class)))
                .thenReturn(petResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/mascotas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMascota", is(1)))
                .andExpect(jsonPath("$.nombre", is("Max")))
                .andExpect(jsonPath("$.sexo", is("M")));

        verify(petService, times(1)).createPet(any(PetCreateDTO.class));
    }

    @Test
    @DisplayName("POST /api/mascotas - Debe retornar 400 cuando los datos son inválidos")
    @WithMockUser(roles = "CLIENTE")
    void testCreatePet_InvalidData() throws Exception {
        // Given
        PetCreateDTO invalidDTO = PetCreateDTO.builder()
                .nombre("") // Nombre vacío - inválido
                .build();

        // When & Then
        mockMvc.perform(post("/api/mascotas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(petService, never()).createPet(any(PetCreateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/mascotas/{id} - Debe actualizar una mascota")
    @WithMockUser(roles = "CLIENTE")
    void testUpdatePet_Success() throws Exception {
        // Given
        when(petService.updatePet(anyLong(), any(PetUpdateDTO.class)))
                .thenReturn(petResponseDTO);

        // When & Then
        mockMvc.perform(put("/api/mascotas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota", is(1)))
                .andExpect(jsonPath("$.nombre", is("Max")));

        verify(petService, times(1)).updatePet(anyLong(), any(PetUpdateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/mascotas/{id} - Debe retornar 404 cuando la mascota no existe")
    @WithMockUser(roles = "CLIENTE")
    void testUpdatePet_NotFound() throws Exception {
        // Given
        when(petService.updatePet(anyLong(), any(PetUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Mascota no encontrada con id: 999"));

        // When & Then
        mockMvc.perform(put("/api/mascotas/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petUpdateDTO)))
                .andExpect(status().isNotFound());

        verify(petService, times(1)).updatePet(anyLong(), any(PetUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/mascotas/{id} - Debe eliminar una mascota")
    @WithMockUser(roles = "CLIENTE")
    void testDeletePet_Success() throws Exception {
        // Given
        doNothing().when(petService).deletePet(1L);

        // When & Then
        mockMvc.perform(delete("/api/mascotas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletePet(1L);
    }

    @Test
    @DisplayName("GET /api/mascotas - Debe retornar lista vacía cuando no hay mascotas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllPets_EmptyList() throws Exception {
        // Given
        when(petService.getAllPet()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(petService, times(1)).getAllPet();
    }


    @Test
    @DisplayName("DELETE /api/mascotas/cliente/{idCliente} - Debe eliminar todas las mascotas de un cliente")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeletePetsByClient_Success() throws Exception {
        // Given
        doNothing().when(petService).deletePetsByClient(1L);

        // When & Then
        mockMvc.perform(delete("/api/mascotas/cliente/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletePetsByClient(1L);
    }

    @Test
    @DisplayName("GET /api/mascotas/raza/{idRaza} - Debe obtener mascotas por raza")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetsByRace_Success() throws Exception {
        // Given
        List<PetResponseDTO> pets = Arrays.asList(petResponseDTO);
        when(petService.getPetsByRace(1L)).thenReturn(pets);

        // When & Then
        mockMvc.perform(get("/api/mascotas/raza/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idRaza", is(1)));

        verify(petService, times(1)).getPetsByRace(1L);
    }

    @Test
    @DisplayName("GET /api/mascotas/sexo/{sexo} - Debe obtener mascotas por sexo")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetsBySex_Success() throws Exception {
        // Given
        List<PetResponseDTO> pets = Arrays.asList(petResponseDTO);
        when(petService.getPetsBySex(Sex.M)).thenReturn(pets);

        // When & Then
        mockMvc.perform(get("/api/mascotas/sexo/M")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sexo", is("M")));

        verify(petService, times(1)).getPetsBySex(Sex.M);
    }

    @Test
    @DisplayName("GET /api/mascotas/estado/{estado} - Debe obtener mascotas por estado")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetsByState_Success() throws Exception {
        // Given
        List<PetResponseDTO> pets = Arrays.asList(petResponseDTO);
        when(petService.getPetsByState(true)).thenReturn(pets);

        // When & Then
        mockMvc.perform(get("/api/mascotas/estado/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is(true)));

        verify(petService, times(1)).getPetsByState(true);
    }

    @Test
    @DisplayName("GET /api/mascotas/cliente/{id}/count - Debe contar mascotas por cliente")
    @WithMockUser(roles = "VETERINARIO")
    void testCountPetsByClient_Success() throws Exception {
        // Given
        when(petService.countPetsByClient(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/mascotas/cliente/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(petService, times(1)).countPetsByClient(1L);
    }

    // ====================== TESTS DE EXCEPCIONES 404 ======================

    @Test
    @DisplayName("DELETE /api/mascotas/{id} - Debe retornar 404 si la mascota no existe")
    @WithMockUser(roles = "CLIENTE")
    void testDeletePet_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Mascota no encontrada"))
                .when(petService).deletePet(999L);

        // When & Then
        mockMvc.perform(delete("/api/mascotas/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(petService, times(1)).deletePet(999L);
    }

    @Test
    @DisplayName("GET /api/mascotas/cliente/{idCliente} - Debe retornar lista vacía si el cliente no tiene mascotas")
    @WithMockUser(roles = "CLIENTE")
    void testGetPetsByClient_EmptyList() throws Exception {
        // Given
        when(petService.getPetsByClient(999L)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/mascotas/cliente/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(petService, times(1)).getPetsByClient(999L);
    }

    @Test
    @DisplayName("GET /api/mascotas/raza/{idRaza} - Debe retornar lista vacía si no hay mascotas de esa raza")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetsByRace_EmptyList() throws Exception {
        // Given
        when(petService.getPetsByRace(999L)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/mascotas/raza/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(petService, times(1)).getPetsByRace(999L);
    }

    @Test
    @DisplayName("GET /api/mascotas/sexo/{sexo} - Debe retornar lista vacía si no hay mascotas de ese sexo")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetsBySex_EmptyList() throws Exception {
        // Given
        when(petService.getPetsBySex(Sex.M)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/mascotas/sexo/M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(petService, times(1)).getPetsBySex(Sex.M);
    }

    @Test
    @DisplayName("GET /api/mascotas/estado/{estado} - Debe retornar lista vacía si no hay mascotas con ese estado")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetsByState_EmptyList() throws Exception {
        // Given
        when(petService.getPetsByState(false)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/mascotas/estado/false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(petService, times(1)).getPetsByState(false);
    }
}

