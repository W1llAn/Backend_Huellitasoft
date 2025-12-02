package huellitassoft_web.huellitasoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import huellitassoft_web.huellitasoft.dto.user.UserCreateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserResponseDTO;
import huellitassoft_web.huellitasoft.dto.user.UserUpdateDTO;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.service.UserService;
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
 * Pruebas de integración para UserController
 * Utiliza MockMvc para simular peticiones HTTP
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean()
    private UserService userService;

    private UserResponseDTO userResponseDTO;
    private UserCreateDTO userCreateDTO;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        userResponseDTO = UserResponseDTO.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .username("testuser")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        userCreateDTO = UserCreateDTO.builder()
                .email("newuser@example.com")
                .username("newuser")
                .contrasena("password123")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .build();

        userUpdateDTO = UserUpdateDTO.builder()
                .email("updated@example.com")
                .username("updateduser")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("GET /api/users - Debe obtener todos los usuarios")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetAllUsers_Success() throws Exception {
        // Given
        List<UserResponseDTO> users = Arrays.asList(userResponseDTO);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idUsuario", is(1)))
                .andExpect(jsonPath("$[0].email", is("test@example.com")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users/{id} - Debe obtener usuario por ID")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("POST /api/users - Debe crear un nuevo usuario")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testCreateUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(userResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).createUser(any(UserCreateDTO.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Debe actualizar un usuario")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testUpdateUser_Success() throws Exception {
        // Given
        UserResponseDTO updated = UserResponseDTO.builder()
                .idUsuario(1L)
                .email("updated@example.com")
                .username("updateduser")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();
        when(userService.updateUser(anyLong(), any(UserUpdateDTO.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).updateUser(anyLong(), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Debe eliminar un usuario")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Debe obtener usuario por email")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserByEmail_Success() throws Exception {
        // Given
        when(userService.getUserByEmail("test@example.com")).thenReturn(userResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/users/email/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("GET /api/users/{id} - Debe devolver 404 si usuario no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Debe devolver 404 si email no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserByEmail_NotFound() throws Exception {
        // Given
        when(userService.getUserByEmail("noexiste@example.com"))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/users/email/noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail("noexiste@example.com");
    }

    @Test
    @DisplayName("GET /api/users/usuario/{usuario} - Debe devolver 404 si username no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserByUsuario_NotFound() throws Exception {
        // Given
        when(userService.getUserByUsuario("noexiste"))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/users/usuario/noexiste"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByUsuario("noexiste");
    }

    @Test
    @DisplayName("PATCH /api/users/{id}/state - Debe devolver 404 si usuario no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testChangeUserState_NotFound() throws Exception {
        // Given
        when(userService.changeUserState(999L, UserState.INACTIVO))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(patch("/api/users/999/state")
                        .param("nuevoEstado", "INACTIVO")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).changeUserState(999L, UserState.INACTIVO);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Debe devolver 404 si usuario no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testDeleteUser_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Usuario no encontrado")).when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/users/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    @DisplayName("GET /api/users/creados-por/{creadoPorId}/role/{rol} - Debe obtener usuarios creados por rol")
    @WithMockUser(roles = "ADMINISTRADOR_VETERINARIA")
    void testGetUsersCreatedByWithRole_Success() throws Exception {
        // Given
        UserResponseDTO vet = UserResponseDTO.builder()
                .idUsuario(10L)
                .username("vet1")
                .email("vet@clinica.com")
                .rol(UserRol.ROLE_VETERINARIO)
                .estado(UserState.ACTIVO)
                .creadoPorId(99L)
                .build();

        when(userService.getUsersCreatedByWithRole(99L, UserRol.ROLE_VETERINARIO))
                .thenReturn(Arrays.asList(vet));

        // When & Then
        mockMvc.perform(get("/api/users/creados-por/99/role/ROLE_VETERINARIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rol", is("ROLE_VETERINARIO")))
                .andExpect(jsonPath("$[0].creadoPorId", is(99)));

        verify(userService, times(1)).getUsersCreatedByWithRole(99L, UserRol.ROLE_VETERINARIO);
    }

    @Test
    @DisplayName("PATCH /api/users/{id}/state - Debe cambiar estado del usuario")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testChangeUserState_Success() throws Exception {
        // Given
        UserResponseDTO updated = UserResponseDTO.builder()
                .idUsuario(1L)
                .username("testuser")
                .email("test@example.com")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.INACTIVO)
                .build();

        when(userService.changeUserState(1L, UserState.INACTIVO)).thenReturn(updated);

        // When & Then
        mockMvc.perform(patch("/api/users/1/state")
                        .param("nuevoEstado", "INACTIVO")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("INACTIVO")));

        verify(userService, times(1)).changeUserState(1L, UserState.INACTIVO);
    }

    @Test
    @DisplayName("GET /api/users/sucursal/{idSucursal}/rol/{rol} - Debe obtener usuarios por sucursal y rol")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUsersBySucursalAndRol_Success() throws Exception {
        // Given
        UserResponseDTO vet = UserResponseDTO.builder()
                .idUsuario(15L)
                .username("vet-sucursal5")
                .rol(UserRol.ROLE_VETERINARIO)
                .idSucursal(5L)
                .sucursalNombre("Sucursal Norte")
                .build();

        when(userService.getUsersBySucursalAndRol(5L, UserRol.ROLE_VETERINARIO))
                .thenReturn(Arrays.asList(vet));

        // When & Then
        mockMvc.perform(get("/api/users/sucursal/5/rol/ROLE_VETERINARIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rol", is("ROLE_VETERINARIO")))
                .andExpect(jsonPath("$[0].idSucursal", is(5)));

        verify(userService, times(1)).getUsersBySucursalAndRol(5L, UserRol.ROLE_VETERINARIO);
    }

    @Test
    @DisplayName("GET /api/users/usuario/{usuario} - Debe obtener usuario por username")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUserByUsuario_Success() throws Exception {
        // Given
        UserResponseDTO user = UserResponseDTO.builder()
                .idUsuario(1L)
                .username("testuser")
                .email("test@example.com")
                .rol(UserRol.ROLE_CLIENTE)
                .build();

        when(userService.getUserByUsuario("testuser")).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/api/users/usuario/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService, times(1)).getUserByUsuario("testuser");
    }

    @Test
    @DisplayName("GET /api/users/role/{rol} - Debe obtener usuarios por rol")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUsersByRole_Success() throws Exception {
        // Given
        UserResponseDTO vet = UserResponseDTO.builder()
                .idUsuario(20L)
                .username("vet2")
                .rol(UserRol.ROLE_VETERINARIO)
                .build();

        when(userService.getUsersByRole(UserRol.ROLE_VETERINARIO))
                .thenReturn(Arrays.asList(vet));

        // When & Then
        mockMvc.perform(get("/api/users/role/ROLE_VETERINARIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rol", is("ROLE_VETERINARIO")));

        verify(userService, times(1)).getUsersByRole(UserRol.ROLE_VETERINARIO);
    }

    @Test
    @DisplayName("GET /api/users/creados-por/{creadoPorId} - Debe obtener usuarios creados por alguien")
    @WithMockUser(roles = "ADMINISTRADOR_VETERINARIA")
    void testGetUsersCreatedBy_Success() throws Exception {
        // Given
        UserResponseDTO createdUser = UserResponseDTO.builder()
                .idUsuario(30L)
                .username("nuevo-vet")
                .rol(UserRol.ROLE_VETERINARIO)
                .creadoPorId(99L)
                .build();

        when(userService.getUsersCreatedBy(99L)).thenReturn(Arrays.asList(createdUser));

        // When & Then
        mockMvc.perform(get("/api/users/creados-por/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].creadoPorId", is(99)));

        verify(userService, times(1)).getUsersCreatedBy(99L);
    }

    @Test
    @DisplayName("GET /api/users/sucursal/{idSucursal} - Debe obtener usuarios por sucursal")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUsersBySucursal_Success() throws Exception {
        // Given
        UserResponseDTO userInSucursal = UserResponseDTO.builder()
                .idUsuario(40L)
                .username("vet-sucursal10")
                .rol(UserRol.ROLE_VETERINARIO)
                .idSucursal(10L)
                .build();

        when(userService.getUsersBySucursal(10L)).thenReturn(Arrays.asList(userInSucursal));

        // When & Then
        mockMvc.perform(get("/api/users/sucursal/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idSucursal", is(10)));

        verify(userService, times(1)).getUsersBySucursal(10L);
    }

    @Test
    @DisplayName("GET /api/users/creados-por/{id} - Lista vacía")
    @WithMockUser(roles = "ADMINISTRADOR_VETERINARIA")
    void testGetUsersCreatedBy_Empty() throws Exception {
        when(userService.getUsersCreatedBy(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/creados-por/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/users/sucursal/{id} - Lista vacía")
    @WithMockUser(roles = "ADMINISTRADOR")
    void testGetUsersBySucursal_Empty() throws Exception {
        when(userService.getUsersBySucursal(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/sucursal/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
