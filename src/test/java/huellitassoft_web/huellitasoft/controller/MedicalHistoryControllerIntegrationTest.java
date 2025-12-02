package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryCreateDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryDetailDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryResponseDTO;
import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import huellitassoft_web.huellitasoft.service.MedicalHistoryService;
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
 * Pruebas de integración para MedicalHistoryController
 */
@WebMvcTest(MedicalHistoryController.class)
@DisplayName("MedicalHistoryController Integration Tests")
class MedicalHistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private MedicalHistoryService medicalHistoryService;

    private MedicalHistoryResponseDTO medicalHistoryResponseDTO;
    private MedicalHistoryDetailDTO medicalHistoryDetailDTO;
    private MedicalHistoryCreateDTO medicalHistoryCreateDTO;

    @BeforeEach
    void setUp() {
        medicalHistoryResponseDTO = MedicalHistoryResponseDTO.builder()
                .idHistoria(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .numero("HC-2025-001")
                .estado(MedicalHistoryState.ACTIVO)
                .build();

        medicalHistoryDetailDTO = MedicalHistoryDetailDTO.builder()
                .idHistoria(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .numero("HC-2025-001")
                .estado(MedicalHistoryState.ACTIVO)
                .consultas(Arrays.asList())
                .tratamientos(Arrays.asList())
                .totalConsultas(0)
                .totalTratamientos(0)
                .build();

        medicalHistoryCreateDTO = MedicalHistoryCreateDTO.builder()
                .idMascota(1L)
                .numero("HC-2025-001")
                .estado(MedicalHistoryState.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("POST /api/historiales-clinicos - Debe crear un historial clínico")
    @WithMockUser(roles = "VETERINARIO")
    void testCreateMedicalHistory_Success() throws Exception {
        // Given
        when(medicalHistoryService.createMedicalHistory(any(MedicalHistoryCreateDTO.class)))
                .thenReturn(medicalHistoryResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/historiales-clinicos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalHistoryCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idHistoria", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(medicalHistoryService, times(1)).createMedicalHistory(any(MedicalHistoryCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/historiales-clinicos/{id} - Debe obtener detalle de historial clínico")
    @WithMockUser(roles = "VETERINARIO")
    void testGetMedicalHistoryDetail_Success() throws Exception {
        // Given
        when(medicalHistoryService.getMedicalHistoryDetail(anyLong()))
                .thenReturn(medicalHistoryDetailDTO);

        // When & Then
        mockMvc.perform(get("/api/historiales-clinicos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHistoria", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")));

        verify(medicalHistoryService, times(1)).getMedicalHistoryDetail(1L);
    }

    @Test
    @DisplayName("GET /api/historiales-clinicos/simple/{id} - Debe obtener historial básico por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetMedicalHistoryById_Success() throws Exception {
        // Given
        when(medicalHistoryService.getMedicalHistoryById(anyLong()))
                .thenReturn(medicalHistoryResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/historiales-clinicos/simple/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHistoria", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(medicalHistoryService, times(1)).getMedicalHistoryById(1L);
    }

    @Test
    @DisplayName("GET /api/historiales-clinicos/mascota/{idMascota} - Debe obtener historial por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetMedicalHistoryByPet_Success() throws Exception {
        // Given
        when(medicalHistoryService.getMedicalHistoryByMascota(anyLong()))
                .thenReturn(medicalHistoryResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/historiales-clinicos/mascota/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota", is(1)))
                .andExpect(jsonPath("$.nombreMascota", is("Max")));

        verify(medicalHistoryService, times(1)).getMedicalHistoryByMascota(1L);
    }

    @Test
    @DisplayName("GET /api/historiales-clinicos - Debe listar todos los historiales")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllMedicalHistories_Success() throws Exception {
        // Given
        List<MedicalHistoryResponseDTO> histories = Arrays.asList(medicalHistoryResponseDTO);
        when(medicalHistoryService.getAllMedicalHistories()).thenReturn(histories);

        // When & Then
        mockMvc.perform(get("/api/historiales-clinicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreMascota", is("Max")));

        verify(medicalHistoryService, times(1)).getAllMedicalHistories();
    }

    @Test
    @DisplayName("GET /api/historiales-clinicos/estado/{estado} - Debe listar historiales por estado")
    @WithMockUser(roles = "VETERINARIO")
    void testGetMedicalHistoriesByEstado_Success() throws Exception {
        // Given
        MedicalHistoryResponseDTO history1 = MedicalHistoryResponseDTO.builder()
                .idHistoria(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .numero("HC-2025-001")
                .estado(MedicalHistoryState.ACTIVO)
                .build();
        MedicalHistoryResponseDTO history2 = MedicalHistoryResponseDTO.builder()
                .idHistoria(2L)
                .idMascota(2L)
                .nombreMascota("Luna")
                .numero("HC-2025-002")
                .estado(MedicalHistoryState.ACTIVO)
                .build();
        List<MedicalHistoryResponseDTO> activeHistories = Arrays.asList(history1, history2);
        when(medicalHistoryService.getMedicalHistoriesByEstado(MedicalHistoryState.ACTIVO))
                .thenReturn(activeHistories);

        // When & Then
        mockMvc.perform(get("/api/historiales-clinicos/estado/ACTIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")))
                .andExpect(jsonPath("$[1].estado", is("ACTIVO")));

        verify(medicalHistoryService, times(1)).getMedicalHistoriesByEstado(MedicalHistoryState.ACTIVO);
    }

    @Test
    @DisplayName("PUT /api/historiales-clinicos/{id}/estado - Debe actualizar estado")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateMedicalHistoryState_Success() throws Exception {
        // Given
        MedicalHistoryResponseDTO updated = MedicalHistoryResponseDTO.builder()
                .idHistoria(1L)
                .idMascota(1L)
                .nombreMascota("Max")
                .numero("HC-2025-001")
                .estado(MedicalHistoryState.INACTIVO)
                .build();
        when(medicalHistoryService.updateEstadoMedicalHistory(anyLong(), any(MedicalHistoryState.class)))
                .thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/historiales-clinicos/1/estado")
                        .with(csrf())
                        .param("nuevoEstado", "INACTIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("INACTIVO")));

        verify(medicalHistoryService, times(1)).updateEstadoMedicalHistory(1L, MedicalHistoryState.INACTIVO);
    }

    @Test
    @DisplayName("DELETE /api/historiales-clinicos/{id} - Debe eliminar historial")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteMedicalHistory_Success() throws Exception {
        // Given
        doNothing().when(medicalHistoryService).deleteMedicalHistory(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/historiales-clinicos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(medicalHistoryService, times(1)).deleteMedicalHistory(1L);
    }
}
