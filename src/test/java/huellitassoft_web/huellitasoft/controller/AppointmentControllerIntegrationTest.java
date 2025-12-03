package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentUpdateDTO;
import huellitassoft_web.huellitasoft.enums.EstadoCita;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para AppointmentController
 * Utiliza MockMvc para simular peticiones HTTP
 */
@WebMvcTest(AppointmentController.class)
@DisplayName("AppointmentController Integration Tests")
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private AppointmentService appointmentService;

    private AppointmentResponseDTO appointmentResponseDTO;
    private AppointmentCreateDTO appointmentCreateDTO;
    private AppointmentUpdateDTO appointmentUpdateDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        appointmentResponseDTO = AppointmentResponseDTO.builder()
                .idCita(1L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .motivo("Consulta general")
                .estado(EstadoCita.CONFIRMADA)
                .idMascota(1L)
                .nombreMascota("Max")
                .idCliente(1L)
                .nombreCliente("Juan García")
                .idUsuario(1L)
                .nombreUsuario("Dr. López")
                .idSucursal(1L)
                .nombreSucursal("Sucursal Central")
                .build();

        appointmentCreateDTO = AppointmentCreateDTO.builder()
                .fechaHora(LocalDateTime.now().plusDays(1))
                .motivo("Consulta general")
                .estado(EstadoCita.PENDIENTE)
                .idMascota(1L)
                .idCliente(1L)
                .idUsuario(1L)
                .idSucursal(1L)
                .build();

        appointmentUpdateDTO = AppointmentUpdateDTO.builder()
                .fechaHora(LocalDateTime.now().plusDays(2))
                .motivo("Consulta actualizada")
                .estado(EstadoCita.CONFIRMADA)
                .idUsuario(1L)
                .build();
    }

    @Test
    @DisplayName("GET /api/citas - Debe obtener todas las citas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAllAppointments_Success() throws Exception {
        // Given
        List<AppointmentResponseDTO> appointments = Arrays.asList(appointmentResponseDTO);
        when(appointmentService.findAll()).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCita", is(1)))
                .andExpect(jsonPath("$[0].motivo", is("Consulta general")));

        verify(appointmentService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/citas/{id} - Debe obtener cita por ID")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentById_Success() throws Exception {
        // Given
        when(appointmentService.findById(1L)).thenReturn(Optional.of(appointmentResponseDTO));

        // When & Then
        mockMvc.perform(get("/api/citas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCita", is(1)))
                .andExpect(jsonPath("$.motivo", is("Consulta general")));

        verify(appointmentService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/citas/{id} - Debe retornar 404 cuando no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentById_NotFound() throws Exception {
        // Given
        when(appointmentService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/citas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("POST /api/citas - Debe crear una nueva cita")
    @WithMockUser(roles = "CLIENTE")
    void testCreateAppointment_Success() throws Exception {
        // Given
        when(appointmentService.create(any(AppointmentCreateDTO.class))).thenReturn(appointmentResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/citas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCita", is(1)))
                .andExpect(jsonPath("$.motivo", is("Consulta general")));

        verify(appointmentService, times(1)).create(any(AppointmentCreateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/citas/{id} - Debe actualizar una cita")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateAppointment_Success() throws Exception {
        // Given
        AppointmentResponseDTO updated = AppointmentResponseDTO.builder()
                .idCita(1L)
                .fechaHora(LocalDateTime.now().plusDays(2))
                .motivo("Consulta actualizada")
                .estado(EstadoCita.CONFIRMADA)
                .idMascota(1L)
                .nombreMascota("Max")
                .idCliente(1L)
                .nombreCliente("Juan García")
                .idUsuario(1L)
                .nombreUsuario("Dr. López")
                .idSucursal(1L)
                .nombreSucursal("Sucursal Central")
                .build();
        when(appointmentService.update(anyLong(), any(AppointmentUpdateDTO.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/citas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo", is("Consulta actualizada")));

        verify(appointmentService, times(1)).update(anyLong(), any(AppointmentUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/citas/{id} - Debe eliminar una cita")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteAppointment_Success() throws Exception {
        // Given
        doNothing().when(appointmentService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/citas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(appointmentService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("POST /api/citas - Actualmente devuelve 500 en conflicto de horario (sin ExceptionHandler)")
    @WithMockUser(roles = "CLIENTE")
    void testCreateAppointment_Conflict_Returns500_CurrentBehavior() throws Exception {
        // Given
        when(appointmentService.create(any(AppointmentCreateDTO.class)))
                .thenThrow(new IllegalArgumentException("El veterinario ya tiene una cita programada"));

        // When & Then
        mockMvc.perform(post("/api/citas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentCreateDTO)))
                .andExpect(status().isInternalServerError());

        verify(appointmentService, times(1)).create(any(AppointmentCreateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/citas/{id} - Debe devolver 404 si la cita no existe")
    @WithMockUser(roles = "VETERINARIO")
    void testUpdateAppointment_NotFound() throws Exception {
        // Given
        when(appointmentService.update(anyLong(), any(AppointmentUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Cita no encontrada"));

        // When & Then
        mockMvc.perform(put("/api/citas/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentUpdateDTO)))
                .andExpect(status().isNotFound());

        // CORREGIDO: todos con matchers o con eq()
        verify(appointmentService, times(1)).update(eq(999L), any(AppointmentUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/citas/{id} - Debe devolver 404 si la cita no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteAppointment_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Cita no encontrada")).when(appointmentService).delete(999L);

        // When & Then
        mockMvc.perform(delete("/api/citas/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).delete(999L);
    }

    @Test
    @DisplayName("GET /api/citas/cliente/{idCliente} - Debe obtener citas por cliente")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByClient_Success() throws Exception {
        // Given
        List<AppointmentResponseDTO> citas = Arrays.asList(appointmentResponseDTO);
        when(appointmentService.findByCliente(1L)).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente", is(1)));

        verify(appointmentService, times(1)).findByCliente(1L);
    }

    @Test
    @DisplayName("GET /api/citas/mascota/{idMascota} - Debe obtener citas por mascota")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByPet_Success() throws Exception {
        // Given
        List<AppointmentResponseDTO> citas = Arrays.asList(appointmentResponseDTO);
        when(appointmentService.findByMascota(1L)).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas/mascota/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idMascota", is(1)));

        verify(appointmentService, times(1)).findByMascota(1L);
    }

    @Test
    @DisplayName("GET /api/citas/usuario/{idUsuario} - Debe obtener citas por usuario")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByUser_Success() throws Exception {
        // Given
        List<AppointmentResponseDTO> citas = Arrays.asList(appointmentResponseDTO);
        when(appointmentService.findByUsuario(1L)).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idUsuario", is(1)));

        verify(appointmentService, times(1)).findByUsuario(1L);
    }

    @Test
    @DisplayName("GET /api/citas/sucursal/{idSucursal} - Debe obtener citas por sucursal")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByBranch_Success() throws Exception {
        // Given
        List<AppointmentResponseDTO> citas = Arrays.asList(appointmentResponseDTO);
        when(appointmentService.findBySucursal(1L)).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas/sucursal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idSucursal", is(1)));

        verify(appointmentService, times(1)).findBySucursal(1L);
    }

    @Test
    @DisplayName("GET /api/citas/estado/{estado} - Debe obtener citas por estado")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByState_Success() throws Exception {
        // Given
        List<AppointmentResponseDTO> citas = Arrays.asList(appointmentResponseDTO);
        when(appointmentService.findByEstado(EstadoCita.CONFIRMADA)).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas/estado/CONFIRMADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("CONFIRMADA")));

        verify(appointmentService, times(1)).findByEstado(EstadoCita.CONFIRMADA);
    }

    @Test
    @DisplayName("GET /api/citas/fechas - Debe obtener citas entre fechas")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsBetweenDates_Success() throws Exception {
        // Given
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().plusDays(7);
        List<AppointmentResponseDTO> citas = Arrays.asList(appointmentResponseDTO);

        when(appointmentService.findByFechaBetween(inicio, fin)).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas/fechas")
                        .param("fechaInicio", inicio.toString())
                        .param("fechaFin", fin.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1)).findByFechaBetween(inicio, fin);
    }

    @Test
    @DisplayName("GET /api/citas/cliente/{id}/count - Debe contar citas por cliente")
    @WithMockUser(roles = "VETERINARIO")
    void testCountAppointmentsByClient_Success() throws Exception {
        // Given
        when(appointmentService.countByCliente(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/citas/cliente/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(appointmentService, times(1)).countByCliente(1L);
    }

    @Test
    @DisplayName("GET /api/citas/estado/{estado}/count - Debe contar citas por estado")
    @WithMockUser(roles = "VETERINARIO")
    void testCountAppointmentsByState_Success() throws Exception {
        // Given
        when(appointmentService.countByEstado(EstadoCita.PENDIENTE)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/citas/estado/PENDIENTE/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(appointmentService, times(1)).countByEstado(EstadoCita.PENDIENTE);
    }

    @Test
    @DisplayName("GET /api/citas/cliente/{id} - Lista vacía")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByClient_Empty() throws Exception {
        when(appointmentService.findByCliente(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/citas/cliente/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/citas/mascota/{id} - Lista vacía")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByPet_Empty() throws Exception {
        when(appointmentService.findByMascota(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/citas/mascota/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/citas/usuario/{id} - Lista vacía")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByUser_Empty() throws Exception {
        when(appointmentService.findByUsuario(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/citas/usuario/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/citas/sucursal/{id} - Lista vacía")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByBranch_Empty() throws Exception {
        when(appointmentService.findBySucursal(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/citas/sucursal/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/citas/estado/{estado} - Lista vacía")
    @WithMockUser(roles = "VETERINARIO")
    void testGetAppointmentsByState_Empty() throws Exception {
        when(appointmentService.findByEstado(EstadoCita.CANCELADA)).thenReturn(List.of());

        mockMvc.perform(get("/api/citas/estado/CANCELADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
