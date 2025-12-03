package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeResponseDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeUpdateStateDTO;
import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import huellitassoft_web.huellitasoft.service.PetSchemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
 * Pruebas de integración para PetSchemeController
 */
@WebMvcTest(PetSchemeController.class)
@DisplayName("PetSchemeController Integration Tests")
class PetSchemeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private PetSchemeService petSchemeService;

    private PetSchemeResponseDTO petSchemeResponseDTO;
    private PetSchemeCreateDTO petSchemeCreateDTO;
    private PetSchemeUpdateStateDTO petSchemeUpdateStateDTO;

    @BeforeEach
    void setUp() {
        petSchemeResponseDTO = PetSchemeResponseDTO.builder()
                .idMascotaEsquema(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .idEsquema(1L)
                .idVacuna(1L)
                .nombreVacuna("Parvovirus")
                .dosisNumero(1)
                .edadSemanas(8)
                .estado(PetSchemeState.ACTIVO)
                .build();

        petSchemeCreateDTO = PetSchemeCreateDTO.builder()
                .idMascota(1L)
                .idEsquema(1L)
                .estado(PetSchemeState.ACTIVO)
                .build();

        petSchemeUpdateStateDTO = PetSchemeUpdateStateDTO.builder()
                .estado(PetSchemeState.COMPLETADO)
                .build();
    }

    @Test
    @DisplayName("POST /api/mascota-esquemas - Debe asignar esquema a mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testAssignSchemeToPet_Success() throws Exception {
        // Given
        when(petSchemeService.assignSchemeToPet(any(PetSchemeCreateDTO.class)))
                .thenReturn(petSchemeResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/mascota-esquemas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petSchemeCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMascotaEsquema", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(petSchemeService, times(1)).assignSchemeToPet(any(PetSchemeCreateDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/mascota-esquemas/{id}/estado - Debe actualizar estado del esquema")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdatePetSchemeState_Success() throws Exception {
        // Given
        PetSchemeResponseDTO updated = PetSchemeResponseDTO.builder()
                .idMascotaEsquema(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .idEsquema(1L)
                .idVacuna(1L)
                .nombreVacuna("Parvovirus")
                .dosisNumero(1)
                .edadSemanas(8)
                .estado(PetSchemeState.COMPLETADO)
                .build();
        when(petSchemeService.updateSchemeState(anyLong(), any(PetSchemeUpdateStateDTO.class)))
                .thenReturn(updated);

        // When & Then
        mockMvc.perform(patch("/api/mascota-esquemas/1/estado")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petSchemeUpdateStateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("COMPLETADO")));

        verify(petSchemeService, times(1)).updateSchemeState(anyLong(), any(PetSchemeUpdateStateDTO.class));
    }

    @Test
    @DisplayName("GET /api/mascota-esquemas/{id} - Debe obtener esquema asignado por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetPetSchemeById_Success() throws Exception {
        // Given
        when(petSchemeService.getPetSchemeById(anyLong())).thenReturn(petSchemeResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/mascota-esquemas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascotaEsquema", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")));

        verify(petSchemeService, times(1)).getPetSchemeById(1L);
    }

    @Test
    @DisplayName("GET /api/mascota-esquemas/mascota/{idMascota} - Debe listar esquemas por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetSchemesByPet_Success() throws Exception {
        // Given
        List<PetSchemeResponseDTO> schemes = Arrays.asList(petSchemeResponseDTO);
        when(petSchemeService.getPetSchemes(anyLong())).thenReturn(schemes);

        // When & Then
        mockMvc.perform(get("/api/mascota-esquemas/mascota/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idMascota", is(1)));

        verify(petSchemeService, times(1)).getPetSchemes(1L);
    }

    @Test
    @DisplayName("GET /api/mascota-esquemas/mascota/{idMascota}/activos - Debe listar esquemas activos por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetActivePetSchemes_Success() throws Exception {
        // Given
        List<PetSchemeResponseDTO> schemes = Arrays.asList(petSchemeResponseDTO);
        when(petSchemeService.getActivePetSchemes(anyLong())).thenReturn(schemes);

        // When & Then
        mockMvc.perform(get("/api/mascota-esquemas/mascota/1/activos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")));

        verify(petSchemeService, times(1)).getActivePetSchemes(1L);
    }

    @Test
    @DisplayName("DELETE /api/mascota-esquemas/{id} - Debe eliminar asignación de esquema")
    @WithMockUser(roles = "VETERINARIO")
    void testDeletePetScheme_Success() throws Exception {
        // Given
        doNothing().when(petSchemeService).unassignScheme(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/mascota-esquemas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(petSchemeService, times(1)).unassignScheme(1L);
    }
}
