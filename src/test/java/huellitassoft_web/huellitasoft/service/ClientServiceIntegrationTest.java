package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.client.ClientCreateDTO;
import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
import huellitassoft_web.huellitasoft.entity.Client;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.ClientState;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.ClientRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para ClientService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Integration Tests")
class ClientServiceIntegrationTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private User testUser;
    private Client testClient;
    private ClientCreateDTO clientCreateDTO;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        testUser = User.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .username("testuser")
                .contrasena("password123")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        // Configurar cliente de prueba
        testClient = Client.builder()
                .idCliente(1L)
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("juan.garcia@example.com")
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .estado(ClientState.ACTIVO)
                .usuario(testUser)
                .build();

        // Configurar DTO de creación
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
    @DisplayName("Debe obtener todos los clientes exitosamente")
    void testGetAllClients_Success() {
        // Given
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.findAll()).thenReturn(clients);

        // When
        List<ClientResponseDTO> result = clientService.getAllClients();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombres()).isEqualTo("Juan Pedro");
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener cliente por ID exitosamente")
    void testGetClientById_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // When
        ClientResponseDTO result = clientService.getClientById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdCliente()).isEqualTo(1L);
        assertThat(result.getNombres()).isEqualTo("Juan Pedro");
        assertThat(result.getApellidos()).isEqualTo("García López");
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void testGetClientById_NotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.getClientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente con ID 999 no encontrado");

        verify(clientRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener cliente por documento exitosamente")
    void testGetClientByDocumento_Success() {
        // Given
        when(clientRepository.findByDocumentoIdentidad("1234567890"))
                .thenReturn(Optional.of(testClient));

        // When
        ClientResponseDTO result = clientService.getClientByDocumento("1234567890");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDocumentoIdentidad()).isEqualTo("1234567890");
        verify(clientRepository, times(1)).findByDocumentoIdentidad("1234567890");
    }

    @Test
    @DisplayName("Debe obtener cliente por email exitosamente")
    void testGetClientByEmail_Success() {
        // Given
        when(clientRepository.findByEmail("juan.garcia@example.com"))
                .thenReturn(Optional.of(testClient));

        // When
        ClientResponseDTO result = clientService.getClientByEmail("juan.garcia@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("juan.garcia@example.com");
        verify(clientRepository, times(1)).findByEmail("juan.garcia@example.com");
    }

    @Test
    @DisplayName("Debe obtener cliente por ID de usuario exitosamente")
    void testGetClientByUsuarioId_Success() {
        // Given
        when(clientRepository.findByUsuario_IdUsuario(1L))
                .thenReturn(Optional.of(testClient));

        // When
        ClientResponseDTO result = clientService.getClientByUsuarioId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdCliente()).isEqualTo(1L);
        verify(clientRepository, times(1)).findByUsuario_IdUsuario(1L);
    }

    @Test
    @DisplayName("Debe obtener clientes por estado exitosamente")
    void testGetClientsByEstado_Success() {
        // Given
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.findByEstado(ClientState.ACTIVO)).thenReturn(clients);

        // When
        List<ClientResponseDTO> result = clientService.getClientsByEstado(ClientState.ACTIVO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(ClientState.ACTIVO);
        verify(clientRepository, times(1)).findByEstado(ClientState.ACTIVO);
    }

    @Test
    @DisplayName("Debe buscar clientes por apellidos exitosamente")
    void testSearchByApellidos_Success() {
        // Given
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.findByApellidosContainingIgnoreCase("García"))
                .thenReturn(clients);

        // When
        List<ClientResponseDTO> result = clientService.searchByApellidos("García");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getApellidos()).contains("García");
        verify(clientRepository, times(1)).findByApellidosContainingIgnoreCase("García");
    }

    @Test
    @DisplayName("Debe buscar clientes por nombres exitosamente")
    void testSearchByNombres_Success() {
        // Given
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.findByNombresContainingIgnoreCase("Juan"))
                .thenReturn(clients);

        // When
        List<ClientResponseDTO> result = clientService.searchByNombres("Juan");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombres()).contains("Juan");
        verify(clientRepository, times(1)).findByNombresContainingIgnoreCase("Juan");
    }

    @Test
    @DisplayName("Debe crear cliente exitosamente")
    void testCreateClient_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(clientRepository.existsByUsuario_IdUsuario(1L)).thenReturn(false);
        when(clientRepository.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        ClientResponseDTO result = clientService.createClient(clientCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNombres()).isEqualTo("Juan Pedro");
        verify(userRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe al crear cliente")
    void testCreateClient_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(clientCreateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario con ID");

        verify(userRepository, times(1)).findById(1L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no tiene rol CLIENTE")
    void testCreateClient_InvalidUserRole() {
        // Given
        User adminUser = User.builder()
                .idUsuario(1L)
                .email("admin@example.com")
                .username("admin")
                .contrasena("password")
                .rol(UserRol.ROLE_ADMINISTRADOR)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(clientCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("no tiene rol de CLIENTE");

        verify(userRepository, times(1)).findById(1L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay clientes")
    void testGetAllClients_EmptyList() {
        // Given
        when(clientRepository.findAll()).thenReturn(List.of());

        // When
        List<ClientResponseDTO> result = clientService.getAllClients();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ya existe cliente con el mismo documento al crear")
    void testCreateClient_DocumentAlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(clientRepository.existsByUsuario_IdUsuario(1L)).thenReturn(false);
        when(clientRepository.existsByDocumentoIdentidad("1234567890")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(clientCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un cliente con el documento: 1234567890");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ya existe cliente con el mismo email al crear")
    void testCreateClient_EmailAlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(clientRepository.existsByUsuario_IdUsuario(1L)).thenReturn(false);
        when(clientRepository.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail("juan.garcia@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(clientCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un cliente con el email: juan.garcia@example.com");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario ya tiene un cliente asociado")
    void testCreateClient_UserAlreadyHasClient() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(clientRepository.existsByUsuario_IdUsuario(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(clientCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un cliente asociado al usuario con ID 1");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar cliente exitosamente cuando todo es válido")
    void testUpdateClient_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .nombres("María Camila")
                .apellidos("Rodríguez")
                .documentoIdentidad("9876543210")
                .email("maria.new@example.com")
                .telefono("3009998888")
                .direccion("Nueva dirección")
                .idUsuario(1L)
                .build();

        // When
        ClientResponseDTO result = clientService.updateClient(1L, updateDTO);

        // Then
        assertThat(result.getNombres()).isEqualTo("María Camila");
        assertThat(result.getApellidos()).isEqualTo("Rodríguez");
        assertThat(result.getDocumentoIdentidad()).isEqualTo("9876543210");
        assertThat(result.getEmail()).isEqualTo("maria.new@example.com");
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo documento ya existe (otro cliente)")
    void testUpdateClient_DocumentAlreadyExists() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByDocumentoIdentidad("9999999999")).thenReturn(true);

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("9999999999")  // nuevo documento duplicado
                .email("juan.garcia@example.com")
                .telefono("3001234567")
                .direccion("Calle 123")
                .idUsuario(1L)
                .build();

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un cliente con el documento: 9999999999");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo email ya existe (otro cliente)")
    void testUpdateClient_EmailAlreadyExists() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByEmail("otro@email.com")).thenReturn(true);

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("otro@email.com")  // email duplicado
                .telefono("3001234567")
                .direccion("Calle 123")
                .idUsuario(1L)
                .build();

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un cliente con el email: otro@email.com");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cambiar el estado del cliente exitosamente")
    void testChangeClientState_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        // When
        ClientResponseDTO result = clientService.changeClientState(1L, ClientState.INACTIVO);

        // Then
        assertThat(result.getEstado()).isEqualTo(ClientState.INACTIVO);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar estado si el cliente no existe")
    void testChangeClientState_NotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.changeClientState(999L, ClientState.INACTIVO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente con ID 999 no encontrado");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar cliente exitosamente")
    void testDeleteClient_Success() {
        // Given
        when(clientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1L);

        // When
        clientService.deleteClient(1L);

        // Then
        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cliente que no existe")
    void testDeleteClient_NotFound() {
        // Given
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clientService.deleteClient(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente con ID 999 no encontrado");

        verify(clientRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe actualizar el usuario del cliente exitosamente (usuario existe y es CLIENTE)")
    void testUpdateClient_ChangeUser_Success() {
        // Given - Nuevo usuario con rol CLIENTE y sin cliente asociado
        User nuevoUsuarioCliente = User.builder()
                .idUsuario(99L)
                .username("nuevoClienteUser")
                .email("nuevo@cliente.com")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(99L)).thenReturn(Optional.of(nuevoUsuarioCliente));
        when(clientRepository.existsByUsuario_IdUsuario(99L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .nombres("Juan Pedro")
                .apellidos("García López")
                .documentoIdentidad("1234567890")
                .email("juan.garcia@example.com")
                .telefono("3001234567")
                .direccion("Calle 123 #45-67")
                .idUsuario(99L)  // ← Cambiamos el usuario
                .build();

        // When
        ClientResponseDTO result = clientService.updateClient(1L, updateDTO);

        // Then
        assertThat(result.getIdUsuario()).isEqualTo(99L);
        verify(userRepository, times(1)).findById(99L);
        verify(clientRepository, times(1)).existsByUsuario_IdUsuario(99L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar usuario si el nuevo usuario no existe")
    void testUpdateClient_ChangeUser_UserNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .idUsuario(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario con ID 999 no encontrado"); // ← Exacto

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar usuario si no tiene rol CLIENTE")
    void testUpdateClient_ChangeUser_NotClientRole() {
        // Given - Usuario con rol diferente
        User adminUser = User.builder()
                .idUsuario(88L)
                .username("adminvet")
                .rol(UserRol.ROLE_ADMINISTRADOR)
                .estado(UserState.ACTIVO)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(88L)).thenReturn(Optional.of(adminUser));

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .idUsuario(88L)
                .build();

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("no tiene rol de CLIENTE");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar usuario si ya existe otro cliente asociado")
    void testUpdateClient_ChangeUser_UserAlreadyHasClient() {
        // Given - El usuario ya tiene un cliente asociado
        User usuarioOcupado = User.builder()
                .idUsuario(77L)
                .username("usuarioocupado")
                .rol(UserRol.ROLE_CLIENTE)
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(77L)).thenReturn(Optional.of(usuarioOcupado));
        when(clientRepository.existsByUsuario_IdUsuario(77L)).thenReturn(true);

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .idUsuario(77L)
                .build();

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un cliente asociado al usuario con ID 77");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe mantener el usuario actual cuando se envía el mismo idUsuario (sin revalidar)")
    void testUpdateClient_NoChangeUser_KeepsExisting() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        ClientCreateDTO updateDTO = ClientCreateDTO.builder()
                .nombres("Nombre Actualizado")
                .apellidos("Apellido Actualizado")
                .idUsuario(1L)  // mismo usuario → no debe validar nada
                .build();

        // When
        ClientResponseDTO result = clientService.updateClient(1L, updateDTO);

        // Then
        assertThat(result.getIdUsuario()).isEqualTo(1L);

        // Correcto: no se llama a userRepository ni a existsByUsuario_IdUsuario
        verify(userRepository, never()).findById(anyLong());
        verify(clientRepository, never()).existsByUsuario_IdUsuario(anyLong());
        verify(clientRepository, times(1)).save(any(Client.class));
    }
}

