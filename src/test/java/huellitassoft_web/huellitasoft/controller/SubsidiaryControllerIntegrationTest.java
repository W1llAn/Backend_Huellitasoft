package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryRequestDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryResponseDTO;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import huellitassoft_web.huellitasoft.service.SubsidiaryService;
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
 * Pruebas de integración para SubsidiaryController
 */
@WebMvcTest(SubsidiaryController.class)
@DisplayName("SubsidiaryController Integration Tests")
class SubsidiaryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private SubsidiaryService subsidiaryService;

    private SubsidiaryResponseDTO subsidiaryResponseDTO;
    private SubsidiaryRequestDTO subsidiaryRequestDTO;

    @BeforeEach
    void setUp() {
        subsidiaryResponseDTO = new SubsidiaryResponseDTO();
        subsidiaryResponseDTO.setIdSubsidiary(1L);
        subsidiaryResponseDTO.setName("Sucursal Central");
        subsidiaryResponseDTO.setAddress("Calle 123 # 45-67");
        subsidiaryResponseDTO.setState(SubsidiaryState.ACTIVO);
        subsidiaryResponseDTO.setIdUsuario(1L);
        subsidiaryResponseDTO.setUsuarioUsername("jperez");
        subsidiaryResponseDTO.setUsuarioEmail("jperez@huellitas.com");
        subsidiaryResponseDTO.setSchedules(Arrays.asList());

        subsidiaryRequestDTO = new SubsidiaryRequestDTO();
        subsidiaryRequestDTO.setName("Sucursal Central");
        subsidiaryRequestDTO.setAddress("Calle 123 # 45-67");
        subsidiaryRequestDTO.setState(SubsidiaryState.ACTIVO);
        subsidiaryRequestDTO.setIdUsuario(1L);
        subsidiaryRequestDTO.setSchedules(Arrays.asList());
    }

    @Test
    @DisplayName("POST /api/subsidiaries - Debe crear una sucursal")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateSubsidiary_Success() throws Exception {
        // Given
        when(subsidiaryService.createSubsidiary(any(SubsidiaryRequestDTO.class)))
                .thenReturn(subsidiaryResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/subsidiaries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsidiaryRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSubsidiary", is(1)))
                .andExpect(jsonPath("$.name", is("Sucursal Central")))
                .andExpect(jsonPath("$.state", is("ACTIVO")));

        verify(subsidiaryService, times(1)).createSubsidiary(any(SubsidiaryRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/subsidiaries/{id} - Debe obtener sucursal por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetSubsidiaryById_Success() throws Exception {
        // Given
        when(subsidiaryService.getSubsidiaryById(anyLong())).thenReturn(subsidiaryResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/subsidiaries/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSubsidiary", is(1)))
                .andExpect(jsonPath("$.name", is("Sucursal Central")));

        verify(subsidiaryService, times(1)).getSubsidiaryById(1L);
    }

    @Test
    @DisplayName("GET /api/subsidiaries - Debe listar todas las sucursales")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllSubsidiaries_Success() throws Exception {
        // Given
        List<SubsidiaryResponseDTO> subsidiaries = Arrays.asList(subsidiaryResponseDTO);
        when(subsidiaryService.getAllSubsidiaries()).thenReturn(subsidiaries);

        // When & Then
        mockMvc.perform(get("/api/subsidiaries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Sucursal Central")));

        verify(subsidiaryService, times(1)).getAllSubsidiaries();
    }

    @Test
    @DisplayName("GET /api/subsidiaries/state/{state} - Debe listar sucursales por estado")
    @WithMockUser(roles = "VETERINARIO")
    void testGetSubsidiariesByState_Success() throws Exception {
        // Given
        List<SubsidiaryResponseDTO> subsidiaries = Arrays.asList(subsidiaryResponseDTO);
        when(subsidiaryService.getSubsidiariesByState(any(SubsidiaryState.class))).thenReturn(subsidiaries);

        // When & Then
        mockMvc.perform(get("/api/subsidiaries/state/ACTIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].state", is("ACTIVO")));

        verify(subsidiaryService, times(1)).getSubsidiariesByState(SubsidiaryState.ACTIVO);
    }

    @Test
    @DisplayName("GET /api/subsidiaries/manager/{idUsuario} - Debe listar sucursales por gestor")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetSubsidiariesByManager_Success() throws Exception {
        // Given
        List<SubsidiaryResponseDTO> subsidiaries = Arrays.asList(subsidiaryResponseDTO);
        when(subsidiaryService.getSubsidiariesByManager(anyLong())).thenReturn(subsidiaries);

        // When & Then
        mockMvc.perform(get("/api/subsidiaries/manager/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idUsuario", is(1)));

        verify(subsidiaryService, times(1)).getSubsidiariesByManager(1L);
    }

    @Test
    @DisplayName("PUT /api/subsidiaries/{id} - Debe actualizar sucursal")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testUpdateSubsidiary_Success() throws Exception {
        // Given
        SubsidiaryResponseDTO updated = new SubsidiaryResponseDTO();
        updated.setIdSubsidiary(1L);
        updated.setName("Sucursal Central Actualizada");
        updated.setAddress("Calle 123 # 45-67");
        updated.setState(SubsidiaryState.ACTIVO);
        updated.setIdUsuario(1L);
        updated.setUsuarioUsername("jperez");
        updated.setUsuarioEmail("jperez@huellitas.com");
        updated.setSchedules(Arrays.asList());
        
        when(subsidiaryService.updateSubsidiary(anyLong(), any(SubsidiaryRequestDTO.class)))
                .thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/subsidiaries/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subsidiaryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sucursal Central Actualizada")));

        verify(subsidiaryService, times(1)).updateSubsidiary(anyLong(), any(SubsidiaryRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/subsidiaries/{id} - Debe eliminar sucursal")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteSubsidiary_Success() throws Exception {
        // Given
        doNothing().when(subsidiaryService).deleteSubsidiary(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/subsidiaries/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(subsidiaryService, times(1)).deleteSubsidiary(1L);
    }
}
