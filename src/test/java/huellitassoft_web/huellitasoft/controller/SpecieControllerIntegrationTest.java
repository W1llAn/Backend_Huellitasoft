package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.specie.SpecieCreateDTO;
import huellitassoft_web.huellitasoft.dto.specie.SpecieResponseDTO;
import huellitassoft_web.huellitasoft.service.SpecieService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para SpecieController
 */
@WebMvcTest(SpecieController.class)
@DisplayName("SpecieController Integration Tests")
class SpecieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private SpecieService specieService;

    private SpecieResponseDTO specieResponseDTO;
    private SpecieCreateDTO specieCreateDTO;

    @BeforeEach
    void setUp() {
        specieResponseDTO = SpecieResponseDTO.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .build();

        specieCreateDTO = SpecieCreateDTO.builder()
                .nombre("Canino")
                .build();
    }

    @Test
    @DisplayName("GET /api/especies - Debe obtener todas las especies")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllSpecies_Success() throws Exception {
        // Given
        List<SpecieResponseDTO> species = Arrays.asList(specieResponseDTO);
        when(specieService.getAllSpecies()).thenReturn(species);

        // When & Then
        mockMvc.perform(get("/api/especies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Canino")));

        verify(specieService, times(1)).getAllSpecies();
    }

    @Test
    @DisplayName("GET /api/especies/{id} - Debe obtener especie por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetSpecieById_Success() throws Exception {
        // Given
        when(specieService.getSpecieById(1L)).thenReturn(specieResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/especies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspecie", is(1)))
                .andExpect(jsonPath("$.nombre", is("Canino")));

        verify(specieService, times(1)).getSpecieById(1L);
    }

    @Test
    @DisplayName("POST /api/especies - Debe crear una nueva especie")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateSpecie_Success() throws Exception {
        // Given
        when(specieService.createSpecie(any(SpecieCreateDTO.class))).thenReturn(specieResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/especies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specieCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Canino")));

        verify(specieService, times(1)).createSpecie(any(SpecieCreateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/especies/{id} - Debe eliminar una especie")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteSpecie_Success() throws Exception {
        // Given
        doNothing().when(specieService).deleteSpecie(1L);

        // When & Then
        mockMvc.perform(delete("/api/especies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(specieService, times(1)).deleteSpecie(1L);
    }
}
