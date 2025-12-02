package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.race.RaceCreateDTO;
import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;
import huellitassoft_web.huellitasoft.service.RaceService;
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
 * Pruebas de integración para RaceController
 */
@WebMvcTest(RaceController.class)
@DisplayName("RaceController Integration Tests")
class RaceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private RaceService raceService;

    private RaceResponseDTO raceResponseDTO;
    private RaceCreateDTO raceCreateDTO;

    @BeforeEach
    void setUp() {
        raceResponseDTO = RaceResponseDTO.builder()
                .idRaza(1L)
                .nombre("Labrador")
                .idEspecie(1L)
                .nombreEspecie("Canino")
                .build();

        raceCreateDTO = RaceCreateDTO.builder()
                .nombre("Labrador")
                .idEspecie(1L)
                .build();
    }

    @Test
    @DisplayName("GET /api/razas - Debe obtener todas las razas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllRaces_Success() throws Exception {
        // Given
        List<RaceResponseDTO> races = Arrays.asList(raceResponseDTO);
        when(raceService.getAllRaces()).thenReturn(races);

        // When & Then
        mockMvc.perform(get("/api/razas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Labrador")));

        verify(raceService, times(1)).getAllRaces();
    }

    @Test
    @DisplayName("GET /api/razas/{id} - Debe obtener raza por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetRaceById_Success() throws Exception {
        // Given
        when(raceService.getRaceById(1L)).thenReturn(raceResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/razas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRaza", is(1)))
                .andExpect(jsonPath("$.nombre", is("Labrador")));

        verify(raceService, times(1)).getRaceById(1L);
    }

    @Test
    @DisplayName("POST /api/razas - Debe crear una nueva raza")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateRace_Success() throws Exception {
        // Given
        when(raceService.createRace(any(RaceCreateDTO.class))).thenReturn(raceResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/razas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(raceCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Labrador")));

        verify(raceService, times(1)).createRace(any(RaceCreateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/razas/{id} - Debe eliminar una raza")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteRace_Success() throws Exception {
        // Given
        doNothing().when(raceService).deleteRace(1L);

        // When & Then
        mockMvc.perform(delete("/api/razas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(raceService, times(1)).deleteRace(1L);
    }

    @Test
    @DisplayName("GET /api/razas/especie/{idEspecie} - Debe obtener razas por especie")
    @WithMockUser(roles = "VETERINARIO")
    void testGetRacesBySpecie_Success() throws Exception {
        // Given
        List<RaceResponseDTO> races = Arrays.asList(raceResponseDTO);
        when(raceService.getRacesBySpecie(1L)).thenReturn(races);

        // When & Then
        mockMvc.perform(get("/api/razas/especie/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idEspecie", is(1)));

        verify(raceService, times(1)).getRacesBySpecie(1L);
    }
}
