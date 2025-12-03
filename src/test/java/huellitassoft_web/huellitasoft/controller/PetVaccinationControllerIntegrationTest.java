package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationCreateDTO;
import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationResponseDTO;
import huellitassoft_web.huellitasoft.service.PetVaccinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para PetVaccinationController
 */
@WebMvcTest(PetVaccinationController.class)
@DisplayName("PetVaccinationController Integration Tests")
class PetVaccinationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private PetVaccinationService petVaccinationService;

    private PetVaccinationResponseDTO petVaccinationResponseDTO;
    private PetVaccinationCreateDTO petVaccinationCreateDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        petVaccinationResponseDTO = PetVaccinationResponseDTO.builder()
                .idVacunacion(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .idVacuna(1L)
                .nombreVacuna("Rabia")
                .fechaAplicada(LocalDateTime.now())
                .idUsuario(2L)
                .nombreUsuario("drlopez")
                .build();

        petVaccinationCreateDTO = PetVaccinationCreateDTO.builder()
                .idMascota(1L)
                .idVacuna(1L)
                .build();
    }

    @Test
    @DisplayName("POST /api/vacunacion-mascota - Debe registrar vacunación")
    @WithMockUser(username = "drlopez", roles = "VETERINARIO")
    void testCreatePetVaccination_Success() throws Exception {
        // Given
        when(petVaccinationService.create(any(PetVaccinationCreateDTO.class), anyString()))
                .thenReturn(petVaccinationResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/vacunacion-mascota")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petVaccinationCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVacunacion", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")))
                .andExpect(jsonPath("$.nombreVacuna", is("Rabia")));

        verify(petVaccinationService, times(1)).create(any(PetVaccinationCreateDTO.class), eq("drlopez"));
    }

    @Test
    @DisplayName("GET /api/vacunacion-mascota/{id} - Debe obtener vacunación por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetVaccinationById_Success() throws Exception {
        // Given
        when(petVaccinationService.getById(anyLong())).thenReturn(petVaccinationResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/vacunacion-mascota/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVacunacion", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")));

        verify(petVaccinationService, times(1)).getById(1L);
    }

    @Test
    @DisplayName("GET /api/vacunacion-mascota/mascota/{idMascota} - Debe listar vacunaciones por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetVaccinationsByPet_Success() throws Exception {
        // Given
        List<PetVaccinationResponseDTO> vaccinations = Arrays.asList(petVaccinationResponseDTO);
        when(petVaccinationService.getByPet(anyLong())).thenReturn(vaccinations);

        // When & Then
        mockMvc.perform(get("/api/vacunacion-mascota/mascota/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idMascota", is(1)));

        verify(petVaccinationService, times(1)).getByPet(1L);
    }

    @Test
    @DisplayName("GET /api/vacunacion-mascota/vacuna/{idVacuna} - Debe listar vacunaciones por vacuna")
    @WithMockUser(roles = "VETERINARIO")
    void testGetVaccinationsByVaccine_Success() throws Exception {
        // Given
        List<PetVaccinationResponseDTO> vaccinations = Arrays.asList(petVaccinationResponseDTO);
        when(petVaccinationService.getByVaccine(anyLong())).thenReturn(vaccinations);

        // When & Then
        mockMvc.perform(get("/api/vacunacion-mascota/vacuna/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idVacuna", is(1)));

        verify(petVaccinationService, times(1)).getByVaccine(1L);
    }

    @Test
    @DisplayName("GET /api/vacunacion-mascota/veterinario/{idUsuario} - Debe listar vacunaciones por veterinario")
    @WithMockUser(roles = "VETERINARIO")
    void testGetVaccinationsByVeterinarian_Success() throws Exception {
        // Given
        List<PetVaccinationResponseDTO> vaccinations = Arrays.asList(petVaccinationResponseDTO);
        when(petVaccinationService.getByVeterinarian(anyLong())).thenReturn(vaccinations);

        // When & Then
        mockMvc.perform(get("/api/vacunacion-mascota/veterinario/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idUsuario", is(2)));

        verify(petVaccinationService, times(1)).getByVeterinarian(2L);
    }

    @Test
    @DisplayName("DELETE /api/vacunacion-mascota/{id} - Debe eliminar vacunación")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeletePetVaccination_Success() throws Exception {
        // Given
        doNothing().when(petVaccinationService).delete(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/vacunacion-mascota/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(petVaccinationService, times(1)).delete(1L);
    }
}
