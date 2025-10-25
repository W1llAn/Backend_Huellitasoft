package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.user.UserCreateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserResponseDTO;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de usuarios.
 * Proporciona la lógica de negocio para operaciones CRUD de usuarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_BY_ID = "Usuario no encontrado con ID: ";
    private static final String USER_NOT_FOUND_BY_EMAIL = "Usuario no encontrado con email: ";
    private static final String USER_NOT_FOUND_BY_USUARIO = "Usuario no encontrado con nombre: ";
    private static final String USER_ALREADY_EXISTS_EMAIL = "Ya existe un usuario con el email: ";
    private static final String USER_ALREADY_EXISTS_USUARIO = "Ya existe un usuario con nombre: ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios como DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param idUsuario el ID del usuario
     * @return el usuario como DTO
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long idUsuario) {
        log.info("Obteniendo usuario con ID: {}", idUsuario);
        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario));
        return convertToResponseDTO(user);
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email el email del usuario
     * @return el usuario como DTO
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Obteniendo usuario con email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_EMAIL + email));
        return convertToResponseDTO(user);
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param usuario el nombre de usuario
     * @return el usuario como DTO
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsuario(String usuario) {
        log.info("Obteniendo usuario con nombre: {}", usuario);
        User user = userRepository.findByUsername(usuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_USUARIO + usuario));
        return convertToResponseDTO(user);
    }

    /**
     * Obtiene todos los usuarios con un rol específico.
     *
     * @param rol el rol a buscar
     * @return lista de usuarios con ese rol como DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(UserRol rol) {
        log.info("Obteniendo usuarios con rol: {}", rol);
        return userRepository.findByRol(rol)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param createDTO DTO con la información del usuario
     * @return el usuario creado como DTO
     * @throws ResourceAlreadyExistsException si el email o usuario ya existe
     */
    @Override
    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        log.info("Creando nuevo usuario con email: {} y usuario: {}", createDTO.getEmail(), createDTO.getUsername());

        // Verificar que el email no exista
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            log.warn("Intento de crear usuario con email duplicado: {}", createDTO.getEmail());
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS_EMAIL + createDTO.getEmail());
        }

        // Verificar que el usuario no exista
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            log.warn("Intento de crear usuario con nombre duplicado: {}", createDTO.getUsername());
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS_USUARIO + createDTO.getUsername());
        }

        User user = User.builder()
                .email(createDTO.getEmail())
                .username(createDTO.getUsername())
                .contrasena(passwordEncoder.encode(createDTO.getContrasena()))
                .rol(createDTO.getRol())
                .estado(createDTO.getEstado())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuario creado exitosamente con ID: {} y email: {}", savedUser.getIdUsuario(), savedUser.getEmail());
        return convertToResponseDTO(savedUser);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param idUsuario el ID del usuario a actualizar
     * @param updateDTO DTO con los datos a actualizar
     * @return el usuario actualizado como DTO
     * @throws ResourceNotFoundException si el usuario no existe
     * @throws ResourceAlreadyExistsException si el email o usuario ya existe en otro usuario
     */
    @Override
    public UserResponseDTO updateUser(Long idUsuario, UserCreateDTO updateDTO) {
        log.info("Actualizando usuario con ID: {}", idUsuario);

        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario));

        // Verificar que el nuevo email no exista en otro usuario
        if (!user.getEmail().equals(updateDTO.getEmail()) && userRepository.existsByEmail(updateDTO.getEmail())) {
            log.warn("Intento de actualizar a email duplicado: {}", updateDTO.getEmail());
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS_EMAIL + updateDTO.getEmail());
        }

        // Verificar que el nuevo usuario no exista en otro usuario
        if (!user.getUsername().equals(updateDTO.getUsername()) && userRepository.existsByUsername(updateDTO.getUsername())) {
            log.warn("Intento de actualizar a usuario duplicado: {}", updateDTO.getUsername());
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS_USUARIO + updateDTO.getUsername());
        }

        user.setEmail(updateDTO.getEmail());
        user.setUsername(updateDTO.getUsername());
        user.setContrasena(passwordEncoder.encode(updateDTO.getContrasena()));
        user.setRol(updateDTO.getRol());
        user.setEstado(updateDTO.getEstado());

        User updatedUser = userRepository.save(user);
        log.info("Usuario actualizado exitosamente con ID: {}", idUsuario);
        return convertToResponseDTO(updatedUser);
    }

    /**
     * Elimina un usuario.
     *
     * @param idUsuario el ID del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    public void deleteUser(Long idUsuario) {
        log.info("Eliminando usuario con ID: {}", idUsuario);

        if (!userRepository.existsById(idUsuario)) {
            log.warn("Intento de eliminar usuario no existente con ID: {}", idUsuario);
            throw new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario);
        }

        userRepository.deleteById(idUsuario);
        log.info("Usuario eliminado exitosamente con ID: {}", idUsuario);
    }

    /**
     * Cambia el estado de un usuario.
     *
     * @param idUsuario el ID del usuario
     * @param nuevoEstado el nuevo estado
     * @return el usuario con estado actualizado como DTO
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    public UserResponseDTO changeUserState(Long idUsuario, UserState nuevoEstado) {
        log.info("Cambiando estado del usuario {} a {}", idUsuario, nuevoEstado);

        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario));

        user.setEstado(nuevoEstado);
        User updatedUser = userRepository.save(user);
        log.info("Estado del usuario actualizado exitosamente con ID: {}", idUsuario);
        return convertToResponseDTO(updatedUser);
    }

    /**
     * Convierte una entidad User a UserResponseDTO.
     *
     * @param user la entidad User
     * @return el DTO UserResponseDTO
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .idUsuario(user.getIdUsuario())
                .email(user.getEmail())
                .username(user.getUsername())
                .rol(user.getRol())
                .estado(user.getEstado())
                .fechaCreacion(user.getFechaCreacion())
                .build();
    }
}
