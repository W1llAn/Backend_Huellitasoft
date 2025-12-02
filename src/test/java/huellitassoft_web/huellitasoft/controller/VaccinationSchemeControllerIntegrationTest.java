package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeResponseDTO;
import huellitassoft_web.huellitasoft.service.VaccinationSchemeService;
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
 * Pruebas de integración para VaccinationSchemeController
 */
@WebMvcTest(VaccinationSchemeController.class)
@DisplayName("VaccinationSchemeController Integration Tests")
class VaccinationSchemeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private VaccinationSchemeService vaccinationSchemeService;

    private VaccinationSchemeResponseDTO vaccinationSchemeResponseDTO;
    private VaccinationSchemeCreateDTO vaccinationSchemeCreateDTO;

    @BeforeEach
    void setUp() {
        vaccinationSchemeResponseDTO = VaccinationSchemeResponseDTO.builder()
                .idEsquema(1L)
                .idVacuna(1L)
                .nombreVacuna("Rabia")
                .dosisNumero(1)
                .edadSemanas(12)
                .observaciones("Primera dosis de vacuna antirrábica")
                .build();

        vaccinationSchemeCreateDTO = VaccinationSchemeCreateDTO.builder()
                .idVacuna(1L)
                .dosisNumero(1)
                .edadSemanas(12)
                .observaciones("Primera dosis de vacuna antirrábica")
                .build();
    }

    @Test
    @DisplayName("POST /api/esquemas-vacunacion - Debe crear un esquema de vacunación")
    @WithMockUser(roles = "VETERINARIO")
    void testCreateVaccinationScheme_Success() throws Exception {
        // Given
        when(vaccinationSchemeService.createScheme(any(VaccinationSchemeCreateDTO.class)))
                .thenReturn(vaccinationSchemeResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/esquemas-vacunacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vaccinationSchemeCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEsquema", is(1)))
                .andExpect(jsonPath("$.nombreVacuna", is("Rabia")))
                .andExpect(jsonPath("$.dosisNumero", is(1)))
                .andExpect(jsonPath("$.edadSemanas", is(12)));

        verify(vaccinationSchemeService, times(1)).createScheme(any(VaccinationSchemeCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/esquemas-vacunacion - Debe listar todos los esquemas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllVaccinationSchemes_Success() throws Exception {
        // Given
        List<VaccinationSchemeResponseDTO> schemes = Arrays.asList(vaccinationSchemeResponseDTO);
        when(vaccinationSchemeService.getAllSchemes()).thenReturn(schemes);

        // When & Then
        mockMvc.perform(get("/api/esquemas-vacunacion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreVacuna", is("Rabia")));

        verify(vaccinationSchemeService, times(1)).getAllSchemes();
    }

    @Test
    @DisplayName("GET /api/esquemas-vacunacion/{id} - Debe obtener esquema por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetVaccinationSchemeById_Success() throws Exception {
        // Given
        when(vaccinationSchemeService.getSchemeById(anyLong())).thenReturn(vaccinationSchemeResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/esquemas-vacunacion/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEsquema", is(1)))
                .andExpect(jsonPath("$.nombreVacuna", is("Rabia")));

        verify(vaccinationSchemeService, times(1)).getSchemeById(1L);
    }

    @Test
    @DisplayName("GET /api/esquemas-vacunacion/vacuna/{idVacuna} - Debe listar esquemas por vacuna")
    @WithMockUser(roles = "VETERINARIO")
    void testGetSchemesByVaccine_Success() throws Exception {
        // Given
        List<VaccinationSchemeResponseDTO> schemes = Arrays.asList(vaccinationSchemeResponseDTO);
        when(vaccinationSchemeService.getSchemesByVaccine(anyLong())).thenReturn(schemes);

        // When & Then
        mockMvc.perform(get("/api/esquemas-vacunacion/vacuna/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idVacuna", is(1)));

        verify(vaccinationSchemeService, times(1)).getSchemesByVaccine(1L);
    }

    @Test
    @DisplayName("PUT /api/esquemas-vacunacion/{id} - Debe actualizar esquema")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateVaccinationScheme_Success() throws Exception {
        // Given
        VaccinationSchemeResponseDTO updated = VaccinationSchemeResponseDTO.builder()
                .idEsquema(1L)
                .idVacuna(1L)
                .nombreVacuna("Rabia")
                .dosisNumero(2)
                .edadSemanas(24)
                .observaciones("Segunda dosis de vacuna antirrábica")
                .build();
        when(vaccinationSchemeService.updateScheme(anyLong(), any(VaccinationSchemeCreateDTO.class)))
                .thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/esquemas-vacunacion/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vaccinationSchemeCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dosisNumero", is(2)))
                .andExpect(jsonPath("$.edadSemanas", is(24)));

        verify(vaccinationSchemeService, times(1)).updateScheme(anyLong(), any(VaccinationSchemeCreateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/esquemas-vacunacion/{id} - Debe eliminar esquema")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteVaccinationScheme_Success() throws Exception {
        // Given
        doNothing().when(vaccinationSchemeService).deleteScheme(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/esquemas-vacunacion/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vaccinationSchemeService, times(1)).deleteScheme(1L);
    }
}
