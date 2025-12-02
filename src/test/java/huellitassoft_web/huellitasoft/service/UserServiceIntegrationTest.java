package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.user.UserCreateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserResponseDTO;
import huellitassoft_web.huellitasoft.dto.user.UserUpdateDTO;
import huellitassoft_web.huellitasoft.entity.Subsidiary;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SubsidiaryRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.impl.EmailService;
import huellitassoft_web.huellitasoft.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para UserService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Integration Tests")
class UserServiceIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubsidiaryRepository subsidiaryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateDTO userCreateDTO;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .username("testuser")
                .contrasena("encodedPassword")
                .rol(UserRol.ROLE_CLIENTE)
                .estado(UserState.ACTIVO)
                .fechaCreacion(LocalDateTime.now())
                .planContratado("BASICO")
                .build();

        userCreateDTO = UserCreateDTO.builder()
                .email("newuser@example.com")
                .username("newuser")
                .contrasena("password123")
                .rol(UserRol.ROLE_CLIENTE)
                .build();

        userUpdateDTO = UserUpdateDTO.builder()
                .email("updated@example.com")
                .username("updateduser")
                .contrasena("newpassword123")
                .estado(UserState.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios exitosamente")
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener usuario por ID exitosamente")
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdUsuario()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe obtener usuario por email exitosamente")
    void testGetUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Debe obtener usuario por username exitosamente")
    void testGetUserByUsuario_Success() {
        // Given
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserByUsuario("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Debe obtener usuarios por rol exitosamente")
    void testGetUsersByRole_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRol(UserRol.ROLE_CLIENTE)).thenReturn(users);

        // When
        List<UserResponseDTO> result = userService.getUsersByRole(UserRol.ROLE_CLIENTE);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRol()).isEqualTo(UserRol.ROLE_CLIENTE);
        verify(userRepository, times(1)).findByRol(UserRol.ROLE_CLIENTE);
    }

    @Test
    @DisplayName("Debe crear usuario exitosamente")
    void testCreateUser_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.createUser(userCreateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya existe al crear usuario")
    void testCreateUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un usuario con el email");

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el username ya existe al crear usuario")
    void testCreateUser_UsernameAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userCreateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un usuario con nombre");

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar usuario exitosamente")
    void testUpdateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponseDTO result = userService.updateUser(1L, userUpdateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe al actualizar")
    void testUpdateUser_NotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(999L, userUpdateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar usuario exitosamente")
    void testDeleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay usuarios")
    void testGetAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of());

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Debe crear usuario con imagen en Cloudinary exitosamente")
    void testCreateUser_WithImage_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(cloudinaryService.uploadImageUsuarios(any())).thenReturn("https://cloudinary.com/img123.jpg");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setIdUsuario(1L);
            return u;
        });

        UserCreateDTO dtoWithImage = UserCreateDTO.builder()
                .email("userimg@example.com")
                .username("userimg")
                .contrasena("pass123")
                .rol(UserRol.ROLE_CLIENTE)
                .imagen("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...") // base64 simulado
                .build();

        // When
        UserResponseDTO result = userService.createUser(dtoWithImage);

        // Then
        assertThat(result.getImagen()).isEqualTo("https://cloudinary.com/img123.jpg");
        verify(cloudinaryService, times(1)).uploadImageUsuarios(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe crear usuario asociado a sucursal y creado por otro usuario")
    void testCreateUser_WithSucursalAndCreadoPor_Success() {
        // Given
        User admin = User.builder().idUsuario(99L).username("admin").build();
        Subsidiary sucursal = new Subsidiary();
        sucursal.setIdSubsidiary(5L);
        sucursal.setName("Sucursal Centro");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));
        when(subsidiaryRepository.findById(5L)).thenReturn(Optional.of(sucursal));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserCreateDTO dto = UserCreateDTO.builder()
                .email("vet@example.com")
                .username("vetuser")
                .contrasena("pass123")
                .rol(UserRol.ROLE_VETERINARIO)
                .idSucursal(5L)
                .creadoPorId(99L)
                .build();

        // When
        UserResponseDTO result = userService.createUser(dto);

        // Then
        assertThat(result.getIdSucursal()).isEqualTo(5L);
        assertThat(result.getCreadoPorId()).isEqualTo(99L);
        verify(subsidiaryRepository, times(1)).findById(5L);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear usuario si la sucursal no existe")
    void testCreateUser_SucursalNotFound() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(subsidiaryRepository.findById(999L)).thenReturn(Optional.empty());

        UserCreateDTO dto = UserCreateDTO.builder()
                .email("user@example.com")
                .username("user123")
                .contrasena("pass")
                .idSucursal(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada");
    }

    @Test
    @DisplayName("Debe actualizar usuario con nueva imagen (eliminando la anterior)")
    void testUpdateUser_WithNewImage_Success() {
        // Given
        testUser.setImagen("https://old-image.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cloudinaryService.uploadImageUsuarios(any())).thenReturn("https://new-image.jpg");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .imagen("data:image/png;base64,iVBORw0KGgoAAAANSUhEUg...") // base64 simulado
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getImagen()).isEqualTo("https://new-image.jpg");
        verify(cloudinaryService, times(1)).deleteImageUsuarios("https://old-image.jpg");
        verify(cloudinaryService, times(1)).uploadImageUsuarios(anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar email si ya existe en otro usuario")
    void testUpdateUser_EmailAlreadyExistsInAnotherUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .email("existing@email.com")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un usuario con el email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe cambiar estado del usuario exitosamente")
    void testChangeUserState_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserResponseDTO result = userService.changeUserState(1L, UserState.INACTIVO);

        // Then
        assertThat(result.getEstado()).isEqualTo(UserState.INACTIVO);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar usuario y su imagen de Cloudinary exitosamente")
    void testDeleteUser_WithImage_Success() {
        // Given
        testUser.setImagen("https://cloudinary.com/user123.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(cloudinaryService, times(1)).deleteImageUsuarios("https://cloudinary.com/user123.jpg");
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe obtener usuarios creados por un usuario específico")
    void testGetUsersCreatedBy_Success() {
        // Given
        List<User> createdUsers = Arrays.asList(testUser);
        when(userRepository.existsById(99L)).thenReturn(true);
        when(userRepository.findByCreadoPor_IdUsuario(99L)).thenReturn(createdUsers);

        // When
        List<UserResponseDTO> result = userService.getUsersCreatedBy(99L);

        // Then
        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findByCreadoPor_IdUsuario(99L);
    }

    @Test
    @DisplayName("Debe obtener usuarios por sucursal exitosamente")
    void testGetUsersBySucursal_Success() {
        // Given - Creamos una sucursal ficticia
        Subsidiary sucursal = new Subsidiary();
        sucursal.setIdSubsidiary(5L);
        sucursal.setName("Sucursal Norte");

        // Creamos un usuario que SÍ pertenece a esa sucursal
        User userInSucursal = User.builder()
                .idUsuario(10L)
                .email("vet@clinica.com")
                .username("vetnorte")
                .contrasena("pass")
                .rol(UserRol.ROLE_VETERINARIO)
                .estado(UserState.ACTIVO)
                .sucursal(sucursal)  // ← ¡¡IMPORTANTE!!
                .build();

        List<User> usersInSucursal = Arrays.asList(userInSucursal);

        when(subsidiaryRepository.existsById(5L)).thenReturn(true);
        when(userRepository.findBySucursal_IdSubsidiary(5L)).thenReturn(usersInSucursal);

        // When
        List<UserResponseDTO> result = userService.getUsersBySucursal(5L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdSucursal()).isEqualTo(5L);        // ahora sí pasa
        assertThat(result.get(0).getSucursalNombre()).isEqualTo("Sucursal Norte");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la sucursal no existe al buscar usuarios por sucursal")
    void testGetUsersBySucursal_SucursalNotFound() {
        // Given
        when(subsidiaryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getUsersBySucursal(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada");
    }

    @Test
    @DisplayName("Debe obtener usuarios por sucursal y rol específicos")
    void testGetUsersBySucursalAndRol_Success() {
        // Given - Creamos un usuario con rol VETERINARIO específicamente para este test
        User veterinario = User.builder()
                .idUsuario(2L)
                .email("vet@huellitasoft.com")
                .username("veterinario1")
                .contrasena("encoded")
                .rol(UserRol.ROLE_VETERINARIO)  // ← Rol correcto
                .estado(UserState.ACTIVO)
                .build();

        List<User> vets = Arrays.asList(veterinario);

        when(subsidiaryRepository.existsById(3L)).thenReturn(true);
        when(userRepository.findBySucursal_IdSubsidiaryAndRol(3L, UserRol.ROLE_VETERINARIO))
                .thenReturn(vets);

        // When
        List<UserResponseDTO> result = userService.getUsersBySucursalAndRol(3L, UserRol.ROLE_VETERINARIO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRol()).isEqualTo(UserRol.ROLE_VETERINARIO);
        assertThat(result.get(0).getUsername()).isEqualTo("veterinario1"); // opcional: más seguridad
    }

    @Test
    @DisplayName("Debe actualizar creadoPor si se proporciona y existe")
    void testUpdateUser_ChangeCreadoPor_Success() {
        // Given
        User nuevoCreador = User.builder().idUsuario(50L).username("admin").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(50L)).thenReturn(Optional.of(nuevoCreador));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .creadoPorId(50L)
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getCreadoPorId()).isEqualTo(50L);
        assertThat(result.getCreadoPorUsername()).isEqualTo("admin");
        verify(userRepository, times(1)).findById(50L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar creadoPor si el usuario no existe")
    void testUpdateUser_ChangeCreadoPor_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .creadoPorId(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar sucursal cuando se proporciona idSucursal")
    void testUpdateUser_ChangeSucursal_Success() {
        // Given
        Subsidiary sucursal = new Subsidiary();
        sucursal.setIdSubsidiary(10L);
        sucursal.setName("Sucursal Norte");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(10L)).thenReturn(Optional.of(sucursal));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .idSucursal(10L)
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getIdSucursal()).isEqualTo(10L);
        assertThat(result.getSucursalNombre()).isEqualTo("Sucursal Norte");
        verify(subsidiaryRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar sucursal si la sucursal no existe")
    void testUpdateUser_ChangeSucursal_SubsidiaryNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subsidiaryRepository.findById(999L)).thenReturn(Optional.empty());

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .idSucursal(999L)
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada con ID: 999");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar ambos campos: creadoPor y sucursal al mismo tiempo")
    void testUpdateUser_ChangeBothCreadoPorAndSucursal_Success() {
        // Given
        User creador = User.builder().idUsuario(77L).username("supervisor").build();
        Subsidiary sucursal = new Subsidiary();
        sucursal.setIdSubsidiary(8L);
        sucursal.setName("Sucursal Centro");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(77L)).thenReturn(Optional.of(creador));
        when(subsidiaryRepository.findById(8L)).thenReturn(Optional.of(sucursal));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .creadoPorId(77L)
                .idSucursal(8L)
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getCreadoPorId()).isEqualTo(77L);
        assertThat(result.getCreadoPorUsername()).isEqualTo("supervisor");
        assertThat(result.getIdSucursal()).isEqualTo(8L);
        assertThat(result.getSucursalNombre()).isEqualTo("Sucursal Centro");
    }

    @Test
    @DisplayName("Debe mantener creadoPor y sucursal si no se envían en el DTO")
    void testUpdateUser_NoChangeCreadoPorNorSucursal() {
        // Given - Simulamos que ya tenía valores previos
        User creadorAntiguo = User.builder().idUsuario(5L).username("antiguo").build();
        Subsidiary sucursalAntigua = new Subsidiary();
        sucursalAntigua.setIdSubsidiary(3L);
        sucursalAntigua.setName("Sucursal Vieja");

        testUser.setCreadoPor(creadorAntiguo);
        testUser.setSucursal(sucursalAntigua);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("nuevousername")
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getCreadoPorId()).isEqualTo(5L);
        assertThat(result.getIdSucursal()).isEqualTo(3L);
        verify(userRepository, never()).findById(5L); // no volvió a buscar
        verify(subsidiaryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Debe obtener usuarios creados por un usuario con un rol específico")
    void testGetUsersCreatedByWithRole_Success() {
        // Given
        User veterinario = User.builder()
                .idUsuario(10L)
                .username("vetcreado")
                .email("vet@created.com")
                .rol(UserRol.ROLE_VETERINARIO)
                .creadoPor(testUser)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByCreadoPor_IdUsuarioAndRol(1L, UserRol.ROLE_VETERINARIO))
                .thenReturn(List.of(veterinario));

        // When
        List<UserResponseDTO> result = userService.getUsersCreatedByWithRole(1L, UserRol.ROLE_VETERINARIO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRol()).isEqualTo(UserRol.ROLE_VETERINARIO);
        assertThat(result.get(0).getCreadoPorId()).isEqualTo(1L);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).findByCreadoPor_IdUsuarioAndRol(1L, UserRol.ROLE_VETERINARIO);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar usuarios creados por usuario inexistente con rol")
    void testGetUsersCreatedByWithRole_CreatorNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getUsersCreatedByWithRole(999L, UserRol.ROLE_VETERINARIO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).findByCreadoPor_IdUsuarioAndRol(anyLong(), any(UserRol.class));
    }

    @Test
    @DisplayName("Debe permitir actualizar con el mismo email (no lanza duplicado)")
    void testUpdateUser_SameEmail_NoDuplicateCheck() {
        // Given - Intentamos actualizar con el mismo email
        testUser.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .email("test@example.com")  // mismo email
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar username si ya existe en otro usuario")
    void testUpdateUser_UsernameAlreadyExistsInAnotherUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("existinguser")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un usuario con nombre");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe permitir actualizar con el mismo username (no lanza duplicado)")
    void testUpdateUser_SameUsername_NoDuplicateCheck() {
        // Given
        testUser.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("testuser")  // mismo username
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar imagen: eliminar anterior y subir nueva")
    void testUpdateUser_WithNewImage_DeletesOldAndUploadsNew() {
        // Given
        testUser.setImagen("https://old-image.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cloudinaryService.uploadImageUsuarios(anyString()))
                .thenReturn("https://new-image.jpg");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .imagen("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...")
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getImagen()).isEqualTo("https://new-image.jpg");
        verify(cloudinaryService, times(1)).deleteImageUsuarios("https://old-image.jpg");
        verify(cloudinaryService, times(1)).uploadImageUsuarios(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar contraseña solo si se proporciona")
    void testUpdateUser_Password_OnlyIfProvided() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .contrasena("newpass123")
                .build();

        // When
        userService.updateUser(1L, updateDTO);

        // Then
        verify(passwordEncoder, times(1)).encode("newpass123");
        verify(userRepository, times(1)).save(argThat(user ->
                "encodedNewPass".equals(user.getContrasena())
        ));
    }

    @Test
    @DisplayName("No debe actualizar contraseña si no se envía")
    void testUpdateUser_Password_NotUpdatedIfEmpty() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .email("nuevo@email.com")
                .build();

        // When
        userService.updateUser(1L, updateDTO);

        // Then
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Debe actualizar rol y estado si se proporcionan")
    void testUpdateUser_RolAndEstado_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .rol(UserRol.ROLE_VETERINARIO)
                .estado(UserState.INACTIVO)
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getRol()).isEqualTo(UserRol.ROLE_VETERINARIO);
        assertThat(result.getEstado()).isEqualTo(UserState.INACTIVO);
    }

    @Test
    @DisplayName("Debe mantener creadoPor si no se envía creadoPorId")
    void testUpdateUser_NoChangeCreadoPor_KeepsExisting() {
        // Given
        User creadorActual = User.builder().idUsuario(10L).username("creador").build();
        testUser.setCreadoPor(creadorActual);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("nuevoUsername")
                .build();

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertThat(result.getCreadoPorId()).isEqualTo(10L);
        verify(userRepository, never()).findById(10L); // no volvió a buscar
    }

    @Test
    @DisplayName("Debe crear usuario con creadoPor y sucursal correctamente")
    void testCreateUser_WithCreadoPorAndSucursal_CoversLambda() {
        // Given
        User creador = User.builder().idUsuario(99L).username("admin").build();
        Subsidiary sucursal = new Subsidiary();
        sucursal.setIdSubsidiary(5L);
        sucursal.setName("Sucursal Centro");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.findById(99L)).thenReturn(Optional.of(creador));
        when(subsidiaryRepository.findById(5L)).thenReturn(Optional.of(sucursal));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserCreateDTO dto = UserCreateDTO.builder()
                .email("nuevo@vet.com")
                .username("vetnuevo")
                .contrasena("pass123")
                .rol(UserRol.ROLE_VETERINARIO)
                .creadoPorId(99L)
                .idSucursal(5L)
                .build();

        // When
        UserResponseDTO result = userService.createUser(dto);

        // Then
        assertThat(result.getCreadoPorId()).isEqualTo(99L);
        assertThat(result.getIdSucursal()).isEqualTo(5L);
        assertThat(result.getSucursalNombre()).isEqualTo("Sucursal Centro");
        verify(userRepository, times(1)).findById(99L);
        verify(subsidiaryRepository, times(1)).findById(5L);
    }

    @Test
    @DisplayName("Debe obtener usuarios por sucursal y rol exitosamente")
    void testGetUsersBySucursalAndRol_Success_CoversLambda() {
        // Given
        List<User> vets = Arrays.asList(testUser);
        when(subsidiaryRepository.existsById(5L)).thenReturn(true);
        when(userRepository.findBySucursal_IdSubsidiaryAndRol(5L, UserRol.ROLE_VETERINARIO))
                .thenReturn(vets);

        // When
        List<UserResponseDTO> result = userService.getUsersBySucursalAndRol(5L, UserRol.ROLE_VETERINARIO);

        // Then
        assertThat(result).hasSize(1);
        verify(subsidiaryRepository, times(1)).existsById(5L);
        verify(userRepository, times(1)).findBySucursal_IdSubsidiaryAndRol(5L, UserRol.ROLE_VETERINARIO);
    }

    @Test
    @DisplayName("Debe obtener usuarios creados por un usuario específico")
    void testGetUsersCreatedBy_Success_CoversLambda() {
        // Given
        List<User> created = Arrays.asList(testUser);
        when(userRepository.existsById(99L)).thenReturn(true);
        when(userRepository.findByCreadoPor_IdUsuario(99L)).thenReturn(created);

        // When
        List<UserResponseDTO> result = userService.getUsersCreatedBy(99L);

        // Then
        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, times(1)).findByCreadoPor_IdUsuario(99L);
    }

    @Test
    @DisplayName("Debe cambiar estado del usuario exitosamente")
    void testChangeUserState_Success_CoversLambda() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserResponseDTO result = userService.changeUserState(1L, UserState.INACTIVO);

        // Then
        assertThat(result.getEstado()).isEqualTo(UserState.INACTIVO);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar usuario y su imagen exitosamente")
    void testDeleteUser_WithImage_Success_CoversLambda() {
        // Given
        testUser.setImagen("https://cloudinary.com/old.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(cloudinaryService, times(1)).deleteImageUsuarios("https://cloudinary.com/old.jpg");
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe obtener usuario por username exitosamente")
    void testGetUserByUsuario_Success_CoversLambda() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserByUsuario("testuser");

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Debe obtener usuario por email exitosamente")
    void testGetUserByEmail_Success_CoversLambda() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay usuarios creados por alguien")
    void testGetUsersCreatedBy_EmptyList() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(true);
        when(userRepository.findByCreadoPor_IdUsuario(999L)).thenReturn(List.of());

        // When
        List<UserResponseDTO> result = userService.getUsersCreatedBy(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar usuarios creados por usuario inexistente")
    void testGetUsersCreatedBy_UserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getUsersCreatedBy(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).findByCreadoPor_IdUsuario(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar usuarios por rol creados por usuario inexistente")
    void testGetUsersCreatedByWithRole_UserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getUsersCreatedByWithRole(999L, UserRol.ROLE_VETERINARIO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 999");

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).findByCreadoPor_IdUsuarioAndRol(anyLong(), any(UserRol.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar usuarios de sucursal inexistente")
    void testGetUsersBySucursal_SubsidiaryNotFound() {
        // Given
        when(subsidiaryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getUsersBySucursal(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada con ID: 999");

        verify(subsidiaryRepository, times(1)).existsById(999L);
        verify(userRepository, never()).findBySucursal_IdSubsidiary(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar usuarios por rol de sucursal inexistente")
    void testGetUsersBySucursalAndRol_SubsidiaryNotFound() {
        // Given
        when(subsidiaryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getUsersBySucursalAndRol(999L, UserRol.ROLE_VETERINARIO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada con ID: 999");

        verify(subsidiaryRepository, times(1)).existsById(999L);
        verify(userRepository, never()).findBySucursal_IdSubsidiaryAndRol(anyLong(), any(UserRol.class));
    }

    @Test
    @DisplayName("Debe manejar excepción al enviar email de credenciales")
    void testCreateUser_EmailSendException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setIdUsuario(1L);
            return u;
        });
        
        // Configurar el mock para lanzar excepción al intentar enviar email
        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());

        UserCreateDTO dto = UserCreateDTO.builder()
                .email("test@example.com")
                .username("testuser")
                .contrasena("password123")
                .rol(UserRol.ROLE_CLIENTE) // Cambiar a ROLE_CLIENTE para que se envíe el email
                .estado(UserState.ACTIVO)
                .build();

        // When
        UserResponseDTO result = userService.createUser(dto);

        // Then - El usuario se crea aunque falle el envío del email
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendNotificationEmail(anyString(), anyString(), anyString(), anyString());
    }
}



