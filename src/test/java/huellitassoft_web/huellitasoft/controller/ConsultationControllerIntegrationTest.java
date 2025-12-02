package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationCreateDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationResponseDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationUpdateDTO;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.service.ConsultationService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ConsultationController
 */
@WebMvcTest(ConsultationController.class)
@DisplayName("ConsultationController Integration Tests")
class ConsultationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private ConsultationService consultationService;

    private ConsultationResponseDTO consultationResponseDTO;
    private ConsultationCreateDTO consultationCreateDTO;
    private ConsultationUpdateDTO consultationUpdateDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        consultationResponseDTO = ConsultationResponseDTO.builder()
                .idConsulta(1L)
                .fechaHora(LocalDateTime.now())
                .motivo("Chequeo general")
                .diagnostico("Animal saludable")
                .indicaciones("Sin complicaciones")
                .idHistoria(1L)
                .idVeterinario(1)
                .nombreVeterinario("Dr. López")
                .build();

        consultationCreateDTO = ConsultationCreateDTO.builder()
                .motivo("Chequeo general")
                .diagnostico("Animal saludable")
                .indicaciones("Sin complicaciones")
                .idHistoria(1L)
                .fechaHora(LocalDateTime.now())
                .idVeterinario(1)
                .build();

        consultationUpdateDTO = ConsultationUpdateDTO.builder()
                .motivo("Chequeo actualizado")
                .diagnostico("Diagnóstico actualizado")
                .indicaciones("Indicaciones actualizadas")
                .build();
    }

    @Test
    @DisplayName("POST /api/consultas - Debe crear una nueva consulta")
    @WithMockUser(roles = "VETERINARIO")
    void testCreateConsultation_Success() throws Exception {
        // Given
        when(consultationService.createConsultation(any(ConsultationCreateDTO.class))).thenReturn(consultationResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/consultas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consultationCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idConsulta", is(1)))
                .andExpect(jsonPath("$.motivo", is("Chequeo general")));

        verify(consultationService, times(1)).createConsultation(any(ConsultationCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/consultas/{id} - Debe obtener consulta por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetConsultationById_Success() throws Exception {
        // Given
        when(consultationService.getConsultationById(1L)).thenReturn(consultationResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/consultas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConsulta", is(1)))
                .andExpect(jsonPath("$.diagnostico", is("Animal saludable")));

        verify(consultationService, times(1)).getConsultationById(1L);
    }

    @Test
    @DisplayName("GET /api/consultas - Debe obtener todas las consultas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllConsultations_Success() throws Exception {
        // Given
        List<ConsultationResponseDTO> consultations = Arrays.asList(consultationResponseDTO);
        when(consultationService.getAllConsultations()).thenReturn(consultations);

        // When & Then
        mockMvc.perform(get("/api/consultas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idConsulta", is(1)));

        verify(consultationService, times(1)).getAllConsultations();
    }

    @Test
    @DisplayName("PUT /api/consultas/{id} - Debe actualizar una consulta")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateConsultation_Success() throws Exception {
        // Given
        ConsultationResponseDTO updated = ConsultationResponseDTO.builder()
                .idConsulta(1L)
                .fechaHora(LocalDateTime.now())
                .motivo("Chequeo actualizado")
                .diagnostico("Diagnóstico actualizado")
                .indicaciones("Indicaciones actualizadas")
                .idHistoria(1L)
                .idVeterinario(1)
                .nombreVeterinario("Dr. López")
                .build();
        when(consultationService.updateConsultation(anyLong(), any(ConsultationUpdateDTO.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/consultas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consultationUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo", is("Chequeo actualizado")));

        verify(consultationService, times(1)).updateConsultation(anyLong(), any(ConsultationUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/consultas/{id} - Debe eliminar una consulta")
    @WithMockUser(roles = "VETERINARIO")
    void testDeleteConsultation_Success() throws Exception {
        // Given
        doNothing().when(consultationService).deleteConsultation(1L);

        // When & Then
        mockMvc.perform(delete("/api/consultas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(consultationService, times(1)).deleteConsultation(1L);
    }

    // ====================== TESTS FALTANTES PARA 100% COBERTURA DE CONSULTATIONCONTROLLER ======================

    @Test
    @DisplayName("GET /api/consultas/historial/{idHistoria} - Debe obtener consultas por historial")
    @WithMockUser(roles = "VETERINARIO")
    void testGetConsultationsByHistoria_Success() throws Exception {
        // Given
        List<ConsultationResponseDTO> consultations = Arrays.asList(consultationResponseDTO);
        when(consultationService.getConsultationsByHistoria(1L)).thenReturn(consultations);

        // When & Then
        mockMvc.perform(get("/api/consultas/historial/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idHistoria", is(1)));

        verify(consultationService, times(1)).getConsultationsByHistoria(1L);
    }

    @Test
    @DisplayName("GET /api/consultas/mascota/{idMascota} - Debe obtener consultas por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetConsultationsByMascota_Success() throws Exception {
        // Given
        List<ConsultationResponseDTO> consultations = Arrays.asList(consultationResponseDTO);
        when(consultationService.getConsultationsByMascota(1L)).thenReturn(consultations);

        // When & Then
        mockMvc.perform(get("/api/consultas/mascota/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(consultationService, times(1)).getConsultationsByMascota(1L);
    }

    @Test
    @DisplayName("GET /api/consultas/veterinario/{idVeterinario} - Debe obtener consultas por veterinario")
    @WithMockUser(roles = "VETERINARIO")
    void testGetConsultationsByVeterinario_Success() throws Exception {
        // Given
        List<ConsultationResponseDTO> consultations = Arrays.asList(consultationResponseDTO);
        when(consultationService.getConsultationsByVeterinario(1)).thenReturn(consultations);

        // When & Then
        mockMvc.perform(get("/api/consultas/veterinario/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idVeterinario", is(1)));

        verify(consultationService, times(1)).getConsultationsByVeterinario(1);
    }

    @Test
    @DisplayName("GET /api/consultas/rango-fechas - Debe obtener consultas entre fechas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetConsultationsByFechaHora_Success() throws Exception {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now().plusDays(7);
        List<ConsultationResponseDTO> consultations = Arrays.asList(consultationResponseDTO);

        when(consultationService.getConsultationsByFechaHora(inicio, fin)).thenReturn(consultations);

        // When & Then
        mockMvc.perform(get("/api/consultas/rango-fechas")
                        .param("inicio", inicio.toString())
                        .param("fin", fin.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(consultationService, times(1)).getConsultationsByFechaHora(inicio, fin);
    }

    @Test
    @DisplayName("GET /api/consultas/mascota/{id}/ultima - Debe obtener última consulta de mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetLastConsultationByMascota_Success() throws Exception {
        // Given
        when(consultationService.getLastConsultationByMascota(1L)).thenReturn(consultationResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/consultas/mascota/1/ultima")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConsulta", is(1)));

        verify(consultationService, times(1)).getLastConsultationByMascota(1L);
    }

    @Test
    @DisplayName("GET /api/consultas/mascota/{id}/ultima - Debe retornar 404 si no hay consultas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetLastConsultationByMascota_NotFound() throws Exception {
        // Given
        when(consultationService.getLastConsultationByMascota(999L))
                .thenThrow(new ResourceNotFoundException("No hay consultas registradas"));

        // When & Then
        mockMvc.perform(get("/api/consultas/mascota/999/ultima"))
                .andExpect(status().isNotFound());

        verify(consultationService, times(1)).getLastConsultationByMascota(999L);
    }

    @Test
    @DisplayName("GET /api/consultas/{id} - Debe retornar 404 si consulta no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testGetConsultationById_NotFound() throws Exception {
        // Given
        when(consultationService.getConsultationById(999L))
                .thenThrow(new ResourceNotFoundException("Consulta no encontrada"));

        // When & Then
        mockMvc.perform(get("/api/consultas/999"))
                .andExpect(status().isNotFound());

        verify(consultationService, times(1)).getConsultationById(999L);
    }

    @Test
    @DisplayName("PUT /api/consultas/{id} - Debe retornar 404 si consulta no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateConsultation_NotFound() throws Exception {
        // Given
        when(consultationService.updateConsultation(anyLong(), any(ConsultationUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Consulta no encontrada"));

        // When & Then
        mockMvc.perform(put("/api/consultas/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consultationUpdateDTO)))
                .andExpect(status().isNotFound());

        verify(consultationService, times(1)).updateConsultation(eq(999L), any(ConsultationUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/consultas/{id} - Debe retornar 404 si consulta no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testDeleteConsultation_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Consulta no encontrada"))
                .when(consultationService).deleteConsultation(999L);

        // When & Then
        mockMvc.perform(delete("/api/consultas/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(consultationService, times(1)).deleteConsultation(999L);
    }

    @Test
    @DisplayName("GET /api/consultas - Debe retornar lista vacía cuando no hay consultas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllConsultations_EmptyList() throws Exception {
        // Given
        when(consultationService.getAllConsultations()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/consultas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(consultationService, times(1)).getAllConsultations();
    }
}
