package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineResponseDTO;
import huellitassoft_web.huellitasoft.service.VaccineService;
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
 * Pruebas de integración para VaccineController
 */
@WebMvcTest(VaccineController.class)
@DisplayName("VaccineController Integration Tests")
class VaccineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private VaccineService vaccineService;

    private VaccineResponseDTO vaccineResponseDTO;
    private VaccineCreateDTO vaccineCreateDTO;

    @BeforeEach
    void setUp() {
        vaccineResponseDTO = VaccineResponseDTO.builder()
                .idVacuna(1L)
                .nombre("Rabia")
                .descripcion("Vacuna antirrábica para perros y gatos")
                .idEspecie(1L)
                .build();

        vaccineCreateDTO = VaccineCreateDTO.builder()
                .nombre("Rabia")
                .descripcion("Vacuna antirrábica para perros y gatos")
                .idEspecie(1L)
                .build();
    }

    @Test
    @DisplayName("POST /api/vacunas - Debe crear una vacuna")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateVaccine_Success() throws Exception {
        // Given
        when(vaccineService.createVaccine(any(VaccineCreateDTO.class)))
                .thenReturn(vaccineResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/vacunas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vaccineCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVacuna", is(1)))
                .andExpect(jsonPath("$.nombre", is("Rabia")))
                .andExpect(jsonPath("$.idEspecie", is(1)));

        verify(vaccineService, times(1)).createVaccine(any(VaccineCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/vacunas - Debe listar todas las vacunas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllVaccines_Success() throws Exception {
        // Given
        List<VaccineResponseDTO> vaccines = Arrays.asList(vaccineResponseDTO);
        when(vaccineService.getAllVaccines()).thenReturn(vaccines);

        // When & Then
        mockMvc.perform(get("/api/vacunas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Rabia")));

        verify(vaccineService, times(1)).getAllVaccines();
    }

    @Test
    @DisplayName("GET /api/vacunas/{id} - Debe obtener vacuna por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetVaccineById_Success() throws Exception {
        // Given
        when(vaccineService.getVaccineById(anyLong())).thenReturn(vaccineResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/vacunas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVacuna", is(1)))
                .andExpect(jsonPath("$.nombre", is("Rabia")));

        verify(vaccineService, times(1)).getVaccineById(1L);
    }

    @Test
    @DisplayName("PUT /api/vacunas/{id} - Debe actualizar vacuna")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testUpdateVaccine_Success() throws Exception {
        // Given
        VaccineResponseDTO updated = VaccineResponseDTO.builder()
                .idVacuna(1L)
                .nombre("Rabia Actualizada")
                .descripcion("Vacuna antirrábica actualizada")
                .idEspecie(1L)
                .build();
        when(vaccineService.updateVaccine(anyLong(), any(VaccineCreateDTO.class)))
                .thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/vacunas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vaccineCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Rabia Actualizada")));

        verify(vaccineService, times(1)).updateVaccine(anyLong(), any(VaccineCreateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/vacunas/{id} - Debe eliminar vacuna")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteVaccine_Success() throws Exception {
        // Given
        doNothing().when(vaccineService).deleteVaccine(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/vacunas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vaccineService, times(1)).deleteVaccine(1L);
    }
}
