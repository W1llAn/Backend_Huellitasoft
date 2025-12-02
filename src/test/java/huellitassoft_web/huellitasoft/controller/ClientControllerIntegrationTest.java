package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.client.ClientCreateDTO;
import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
import huellitassoft_web.huellitasoft.enums.ClientState;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.service.ClientService;
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
 * Pruebas de integración para ClientController
 * Utiliza MockMvc para simular peticiones HTTP
 */
@WebMvcTest(ClientController.class)
@DisplayName("ClientController Integration Tests")
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private ClientService clientService;

    private ClientResponseDTO clientResponseDTO;
    private ClientCreateDTO clientCreateDTO;

    @BeforeEach
    void setUp() {
        clientResponseDTO = ClientResponseDTO.builder()
                .idCliente(1L)
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("juan.garcia@example.com")
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .estado(ClientState.ACTIVO)
                .idUsuario(1L)
                .build();

        clientCreateDTO = ClientCreateDTO.builder()
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("juan.garcia@example.com")
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .idUsuario(1L)
                .build();
    }

    @Test
    @DisplayName("GET /api/clientes - Debe obtener todos los clientes")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetAllClients_Success() throws Exception {
        // Given
        List<ClientResponseDTO> clients = Arrays.asList(clientResponseDTO);
        when(clientService.getAllClients()).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente", is(1)))
                .andExpect(jsonPath("$[0].nombres", is("Juan Pedro")))
                .andExpect(jsonPath("$[0].apellidos", is("García López")));

        verify(clientService, times(1)).getAllClients();
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Debe obtener cliente por ID")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientById_Success() throws Exception {
        // Given
        when(clientService.getClientById(1L)).thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan Pedro")))
                .andExpect(jsonPath("$.email", is("juan.garcia@example.com")));

        verify(clientService, times(1)).getClientById(1L);
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Debe retornar 404 cuando el cliente no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientById_NotFound() throws Exception {
        // Given
        when(clientService.getClientById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Cliente con ID 999 no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).getClientById(999L);
    }

    @Test
    @DisplayName("POST /api/clientes - Debe crear un nuevo cliente")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateClient_Success() throws Exception {
        // Given
        when(clientService.createClient(any(ClientCreateDTO.class)))
                .thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan Pedro")))
                .andExpect(jsonPath("$.email", is("juan.garcia@example.com")));

        verify(clientService, times(1)).createClient(any(ClientCreateDTO.class));
    }

    @Test
    @DisplayName("POST /api/clientes - Debe retornar 400 cuando los datos son inválidos")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateClient_InvalidData() throws Exception {
        // Given
        ClientCreateDTO invalidDTO = ClientCreateDTO.builder()
                .nombres("") // Nombre vacío - inválido
                .apellidos("García")
                .build();

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).createClient(any(ClientCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/clientes/documento/{documento} - Debe obtener cliente por documento")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientByDocumento_Success() throws Exception {
        // Given
        when(clientService.getClientByDocumento("1234567890"))
                .thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/documento/1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentoIdentidad", is("1234567890")));

        verify(clientService, times(1)).getClientByDocumento("1234567890");
    }

    @Test
    @DisplayName("GET /api/clientes/email/{email} - Debe obtener cliente por email")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientByEmail_Success() throws Exception {
        // Given
        when(clientService.getClientByEmail("juan.garcia@example.com"))
                .thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/email/juan.garcia@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("juan.garcia@example.com")));

        verify(clientService, times(1)).getClientByEmail("juan.garcia@example.com");
    }

    @Test
    @DisplayName("GET /api/clientes/estado/{estado} - Debe obtener clientes por estado")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientsByEstado_Success() throws Exception {
        // Given
        List<ClientResponseDTO> clients = Arrays.asList(clientResponseDTO);
        when(clientService.getClientsByEstado(ClientState.ACTIVO))
                .thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clientes/estado/ACTIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")));

        verify(clientService, times(1)).getClientsByEstado(ClientState.ACTIVO);
    }

    @Test
    @DisplayName("GET /api/clientes/buscar/apellidos - Debe buscar clientes por apellidos")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testSearchByApellidos_Success() throws Exception {
        // Given
        List<ClientResponseDTO> clients = Arrays.asList(clientResponseDTO);
        when(clientService.searchByApellidos("García")).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clientes/buscar/apellidos")
                        .param("apellidos", "García")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].apellidos", is("García López")));

        verify(clientService, times(1)).searchByApellidos("García");
    }

    @Test
    @DisplayName("GET /api/clientes/buscar/nombres - Debe buscar clientes por nombres")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testSearchByNombres_Success() throws Exception {
        // Given
        List<ClientResponseDTO> clients = Arrays.asList(clientResponseDTO);
        when(clientService.searchByNombres("Juan")).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clientes/buscar/nombres")
                        .param("nombres", "Juan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombres", is("Juan Pedro")));

        verify(clientService, times(1)).searchByNombres("Juan");
    }

    @Test
    @DisplayName("GET /api/clientes - Debe retornar lista vacía cuando no hay clientes")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetAllClients_EmptyList() throws Exception {
        // Given
        when(clientService.getAllClients()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clientService, times(1)).getAllClients();
    }

    // ====================== TESTS FALTANTES PARA 100% COBERTURA DE CLIENTCONTROLLER ======================

    @Test
    @DisplayName("GET /api/clientes/usuario/{idUsuario} - Debe obtener cliente por ID de usuario")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientByUsuarioId_Success() throws Exception {
        // Given
        when(clientService.getClientByUsuarioId(1L)).thenReturn(clientResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario", is(1)));

        verify(clientService, times(1)).getClientByUsuarioId(1L);
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id}/cambiar-estado - Debe cambiar estado del cliente")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testChangeClientState_Success() throws Exception {
        // Given
        ClientResponseDTO updated = ClientResponseDTO.builder()
                .idCliente(1L)
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("juan.garcia@example.com")
                .estado(ClientState.INACTIVO)
                .idUsuario(1L)
                .build();

        when(clientService.changeClientState(1L, ClientState.INACTIVO)).thenReturn(updated);

        // When & Then
        mockMvc.perform(patch("/api/clientes/1/cambiar-estado")
                        .param("nuevoEstado", "INACTIVO")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("INACTIVO")));

        verify(clientService, times(1)).changeClientState(1L, ClientState.INACTIVO);
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Debe eliminar cliente exitosamente")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteClient_Success() throws Exception {
        // Given
        doNothing().when(clientService).deleteClient(1L);

        // When & Then
        mockMvc.perform(delete("/api/clientes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(clientService, times(1)).deleteClient(1L);
    }

    @Test
    @DisplayName("GET /api/clientes/documento/{documento} - Debe retornar 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientByDocumento_NotFound() throws Exception {
        // Given
        when(clientService.getClientByDocumento("9999999999"))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/clientes/documento/9999999999"))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).getClientByDocumento("9999999999");
    }

    @Test
    @DisplayName("GET /api/clientes/email/{email} - Debe retornar 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientByEmail_NotFound() throws Exception {
        // Given
        when(clientService.getClientByEmail("noexiste@example.com"))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/clientes/email/noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).getClientByEmail("noexiste@example.com");
    }

    @Test
    @DisplayName("GET /api/clientes/usuario/{idUsuario} - Debe retornar 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetClientByUsuarioId_NotFound() throws Exception {
        // Given
        when(clientService.getClientByUsuarioId(999L))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/clientes/usuario/999"))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).getClientByUsuarioId(999L);
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id}/cambiar-estado - Debe retornar 404 si cliente no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testChangeClientState_NotFound() throws Exception {
        // Given
        when(clientService.changeClientState(999L, ClientState.INACTIVO))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(patch("/api/clientes/999/cambiar-estado")
                        .param("nuevoEstado", "INACTIVO")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).changeClientState(999L, ClientState.INACTIVO);
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Debe retornar 404 si cliente no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteClient_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Cliente no encontrado")).when(clientService).deleteClient(999L);

        // When & Then
        mockMvc.perform(delete("/api/clientes/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).deleteClient(999L);
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Debe retornar 404 si cliente no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testUpdateClient_NotFound() throws Exception {
        // Given
        when(clientService.updateClient(anyLong(), any(ClientCreateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/clientes/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientCreateDTO)))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).updateClient(eq(999L), any(ClientCreateDTO.class));
    }
}

