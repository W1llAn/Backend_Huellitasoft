package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentUpdateDTO;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.service.TreatmentService;
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
 * Pruebas de integración para TreatmentController
 */
@WebMvcTest(TreatmentController.class)
@DisplayName("TreatmentController Integration Tests")
class TreatmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private TreatmentService treatmentService;

    private TreatmentResponseDTO treatmentResponseDTO;
    private TreatmentCreateDTO treatmentCreateDTO;
    private TreatmentUpdateDTO treatmentUpdateDTO;

    @BeforeEach
    void setUp() {
        treatmentResponseDTO = TreatmentResponseDTO.builder()
                .idTratamiento(1L)
                .medication("Antibiótico")
                .dosage("10mg")
                .frequency("Cada 8 horas")
                .durationDays(7)
                .description("Tratamiento para infección")
                .idConsulta(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .build();

        treatmentCreateDTO = TreatmentCreateDTO.builder()
                .medication("Antibiótico")
                .dosage("10mg")
                .frequency("Cada 8 horas")
                .durationDays(7)
                .description("Tratamiento para infección")
                .idConsulta(1L)
                .idMascota(1L)
                .build();

        treatmentUpdateDTO = TreatmentUpdateDTO.builder()
                .dosage("15mg")
                .frequency("Cada 12 horas")
                .durationDays(10)
                .description("Actualización de dosis")
                .build();
    }

    @Test
    @DisplayName("POST /api/tratamientos - Debe crear un nuevo tratamiento")
    @WithMockUser(roles = "VETERINARIO")
    void testCreateTreatment_Success() throws Exception {
        // Given
        when(treatmentService.createTreatment(any(TreatmentCreateDTO.class))).thenReturn(treatmentResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/tratamientos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(treatmentCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTratamiento", is(1)))
                .andExpect(jsonPath("$.medication", is("Antibiótico")));

        verify(treatmentService, times(1)).createTreatment(any(TreatmentCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/tratamientos/{id} - Debe obtener tratamiento por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetTreatmentById_Success() throws Exception {
        // Given
        when(treatmentService.getTreatmentById(1L)).thenReturn(treatmentResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/tratamientos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTratamiento", is(1)))
                .andExpect(jsonPath("$.medication", is("Antibiótico")));

        verify(treatmentService, times(1)).getTreatmentById(1L);
    }

    @Test
    @DisplayName("GET /api/tratamientos - Debe obtener todos los tratamientos")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllTreatments_Success() throws Exception {
        // Given
        List<TreatmentResponseDTO> treatments = Arrays.asList(treatmentResponseDTO);
        when(treatmentService.getAllTreatments()).thenReturn(treatments);

        // When & Then
        mockMvc.perform(get("/api/tratamientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idTratamiento", is(1)));

        verify(treatmentService, times(1)).getAllTreatments();
    }

    @Test
    @DisplayName("PUT /api/tratamientos/{id} - Debe actualizar un tratamiento")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateTreatment_Success() throws Exception {
        // Given
        TreatmentResponseDTO updated = TreatmentResponseDTO.builder()
                .idTratamiento(1L)
                .medication("Antibiótico")
                .dosage("15mg")
                .frequency("Cada 12 horas")
                .durationDays(10)
                .description("Actualización de dosis")
                .idConsulta(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .build();
        when(treatmentService.updateTreatment(anyLong(), any(TreatmentUpdateDTO.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/tratamientos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(treatmentUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dosage", is("15mg")));

        verify(treatmentService, times(1)).updateTreatment(anyLong(), any(TreatmentUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/tratamientos/{id} - Debe eliminar un tratamiento")
    @WithMockUser(roles = "VETERINARIO")
    void testDeleteTreatment_Success() throws Exception {
        // Given
        doNothing().when(treatmentService).deleteTreatment(1L);

        // When & Then
        mockMvc.perform(delete("/api/tratamientos/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(treatmentService, times(1)).deleteTreatment(1L);
    }

    @Test
    @DisplayName("GET /api/tratamientos/consulta/{idConsulta} - Debe obtener tratamientos por consulta")
    @WithMockUser(roles = "VETERINARIO")
    void testGetTreatmentsByConsultation_Success() throws Exception {
        // Given
        List<TreatmentResponseDTO> treatments = Arrays.asList(treatmentResponseDTO);
        when(treatmentService.getTreatmentsByConsultation(1L)).thenReturn(treatments);

        // When & Then
        mockMvc.perform(get("/api/tratamientos/consulta/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idConsulta", is(1)));

        verify(treatmentService, times(1)).getTreatmentsByConsultation(1L);
    }

    @Test
    @DisplayName("GET /api/tratamientos/mascota/{idMascota} - Debe obtener tratamientos por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetTreatmentsByMascota_Success() throws Exception {
        // Given
        List<TreatmentResponseDTO> treatments = Arrays.asList(treatmentResponseDTO);
        when(treatmentService.getTreatmentsByMascota(1L)).thenReturn(treatments);

        // When & Then
        mockMvc.perform(get("/api/tratamientos/mascota/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idMascota", is(1)))
                .andExpect(jsonPath("$[0].nombreMascota", is("Max")));

        verify(treatmentService, times(1)).getTreatmentsByMascota(1L);
    }

    @Test
    @DisplayName("GET /api/tratamientos/mascota/{idMascota}/activos - Debe obtener tratamientos activos por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetActiveTreatmentsByMascota_Success() throws Exception {
        // Given
        List<TreatmentResponseDTO> activeTreatments = Arrays.asList(treatmentResponseDTO);
        when(treatmentService.getActiveTreatmentsByMascota(1L)).thenReturn(activeTreatments);

        // When & Then
        mockMvc.perform(get("/api/tratamientos/mascota/1/activos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(treatmentService, times(1)).getActiveTreatmentsByMascota(1L);
    }

    @Test
    @DisplayName("PATCH /api/tratamientos/{id}/estado - Debe cambiar estado del tratamiento")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateTreatmentStatus_Success() throws Exception {
        // Given
        TreatmentResponseDTO updated = TreatmentResponseDTO.builder()
                .idTratamiento(1L)
                .medication("Antibiótico")
                .dosage("10mg")
                .frequency("Cada 8 horas")
                .durationDays(7)
                .description("Tratamiento para infección")
                .idConsulta(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .status(false)
                .build();

        when(treatmentService.updateTreatmentStatus(1L, false)).thenReturn(updated);

        // When & Then
        mockMvc.perform(patch("/api/tratamientos/1/estado")
                        .param("nuevoEstado", "false")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(false)));

        verify(treatmentService, times(1)).updateTreatmentStatus(1L, false);
    }

    @Test
    @DisplayName("GET /api/tratamientos/{id} - Debe retornar 404 si tratamiento no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testGetTreatmentById_NotFound() throws Exception {
        // Given
        when(treatmentService.getTreatmentById(999L))
                .thenThrow(new ResourceNotFoundException("Tratamiento no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/tratamientos/999"))
                .andExpect(status().isNotFound());

        verify(treatmentService, times(1)).getTreatmentById(999L);
    }

    @Test
    @DisplayName("PUT /api/tratamientos/{id} - Debe retornar 404 si tratamiento no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateTreatment_NotFound() throws Exception {
        // Given
        when(treatmentService.updateTreatment(anyLong(), any(TreatmentUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Tratamiento no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/tratamientos/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(treatmentUpdateDTO)))
                .andExpect(status().isNotFound());

        verify(treatmentService, times(1)).updateTreatment(eq(999L), any(TreatmentUpdateDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/tratamientos/{id}/estado - Debe retornar 404 si tratamiento no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateTreatmentStatus_NotFound() throws Exception {
        // Given
        when(treatmentService.updateTreatmentStatus(999L, true))
                .thenThrow(new ResourceNotFoundException("Tratamiento no encontrado"));

        // When & Then
        mockMvc.perform(patch("/api/tratamientos/999/estado")
                        .param("nuevoEstado", "true")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(treatmentService, times(1)).updateTreatmentStatus(999L, true);
    }

    @Test
    @DisplayName("DELETE /api/tratamientos/{id} - Debe retornar 404 si tratamiento no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testDeleteTreatment_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Tratamiento no encontrado"))
                .when(treatmentService).deleteTreatment(999L);

        // When & Then
        mockMvc.perform(delete("/api/tratamientos/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(treatmentService, times(1)).deleteTreatment(999L);
    }

    @Test
    @DisplayName("GET /api/tratamientos - Debe retornar lista vacía cuando no hay tratamientos")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllTreatments_EmptyList() throws Exception {
        // Given
        when(treatmentService.getAllTreatments()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/tratamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(treatmentService, times(1)).getAllTreatments();
    }

    @Test
    @DisplayName("GET /api/tratamientos/consulta/{id} - Debe retornar lista vacía si no hay tratamientos")
    @WithMockUser(roles = "VETERINARIO")
    void testGetTreatmentsByConsultation_EmptyList() throws Exception {
        // Given
        when(treatmentService.getTreatmentsByConsultation(999L)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/tratamientos/consulta/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(treatmentService, times(1)).getTreatmentsByConsultation(999L);
    }
}
