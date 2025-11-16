package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.user.UserCreateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserUpdateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserResponseDTO;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.entity.Subsidiary;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.repository.SubsidiaryRepository;
import huellitassoft_web.huellitasoft.service.CloudinaryService;
import huellitassoft_web.huellitasoft.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private static final String SUBSIDIARY_NOT_FOUND = "Sucursal no encontrada con ID: ";

    private final UserRepository userRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService; // ← INYECCIÓN DE CLOUDINARY

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long idUsuario) {
        log.info("Obteniendo usuario con ID: {}", idUsuario);
        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario));
        return convertToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Obteniendo usuario con email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_EMAIL + email));
        return convertToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsuario(String usuario) {
        log.info("Obteniendo usuario con nombre: {}", usuario);
        User user = userRepository.findByUsername(usuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_USUARIO + usuario));
        return convertToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(UserRol rol) {
        log.info("Obteniendo usuarios con rol: {}", rol);
        return userRepository.findByRol(rol)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

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
        String rawPassword = createDTO.getContrasena();
        String rolCliente = String.valueOf(createDTO.getRol());

        //  SUBIR IMAGEN A CLOUDINARY SI VIENE EN EL DTO
        String imageUrl = null;
        if (createDTO.getImagen() != null && !createDTO.getImagen().isEmpty()) {
            log.info(" Subiendo imagen de usuario a Cloudinary...");
            imageUrl = cloudinaryService.uploadImageUsuarios(createDTO.getImagen());
            log.info(" Imagen subida: {}", imageUrl);
        }

        User user = User.builder()
                .email(createDTO.getEmail())
                .username(createDTO.getUsername())
                .contrasena(passwordEncoder.encode(createDTO.getContrasena()))
                .rol(createDTO.getRol())
                .estado(createDTO.getEstado())
                .imagen(imageUrl) // GUARDAR URL DE CLOUDINARY
                .build();

        // Si se proporciona creadoPorId, validar que el usuario exista
        if (createDTO.getCreadoPorId() != null) {
            User creadoPor = userRepository.findById(createDTO.getCreadoPorId())
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + createDTO.getCreadoPorId()));
            user.setCreadoPor(creadoPor);
            log.info("Usuario será creado por el usuario: {}", createDTO.getCreadoPorId());
        }

        // Si se proporciona idSucursal, validar que la sucursal exista
        if (createDTO.getIdSucursal() != null) {
            Subsidiary subsidiary = subsidiaryRepository.findById(createDTO.getIdSucursal())
                    .orElseThrow(() -> new ResourceNotFoundException(SUBSIDIARY_NOT_FOUND + createDTO.getIdSucursal()));
            user.setSucursal(subsidiary);
            log.info("Usuario asociado a la sucursal: {}", createDTO.getIdSucursal());
        }

        User savedUser = userRepository.save(user);
        log.info("Usuario creado exitosamente con ID: {} y email: {}", savedUser.getIdUsuario(), savedUser.getEmail());

        // Enviar email con credenciales si es cliente
        if (rolCliente.equals("ROLE_CLIENTE")) {
            try {
                if (savedUser.getEmail() != null && rawPassword != null) {
                    String asunto = "Creación de cuenta HuellitaSoft";
                    String titulo = "Cuenta creada exitosamente";
                    String mensaje = String.format("""
                            Estimado(a) %s,
                            
                            Nos complace informarle que su cuenta en el sistema de gestión veterinaria HuellitaSoft ha sido creada correctamente.
                            
                            A continuación, se detallan sus credenciales de acceso:
                            
                            • Usuario: %s
                            • Contraseña temporal: %s
                            
                            Por motivos de seguridad, le recomendamos cambiar su contraseña al iniciar sesión por primera vez.
                            
                            Si usted no solicitó esta cuenta, por favor ignore este mensaje.
                            
                            Atentamente,
                            El equipo de HuellitaSoft
                            """, savedUser.getUsername(), savedUser.getUsername(), rawPassword);

                    emailService.sendNotificationEmail(
                            savedUser.getEmail(),
                            titulo,
                            asunto,
                            mensaje
                    );
                }
            } catch (Exception e) {
                log.error("❌ Error al enviar las credenciales al usuario {}: {}", savedUser.getIdUsuario(), e.getMessage());
            }
        }

        return convertToResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long idUsuario, UserUpdateDTO updateDTO) {
        log.info("Actualizando usuario con ID: {}", idUsuario);

        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario));

        // Verificar que el nuevo email no exista en otro usuario
        if (updateDTO.getEmail() != null && !user.getEmail().equals(updateDTO.getEmail())
                && userRepository.existsByEmail(updateDTO.getEmail())) {
            log.warn("Intento de actualizar a email duplicado: {}", updateDTO.getEmail());
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS_EMAIL + updateDTO.getEmail());
        }

        // Verificar que el nuevo usuario no exista en otro usuario
        if (updateDTO.getUsername() != null && !user.getUsername().equals(updateDTO.getUsername())
                && userRepository.existsByUsername(updateDTO.getUsername())) {
            log.warn("Intento de actualizar a usuario duplicado: {}", updateDTO.getUsername());
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS_USUARIO + updateDTO.getUsername());
        }

        // ACTUALIZAR IMAGEN SI VIENE UNA NUEVA
        if (updateDTO.getImagen() != null && !updateDTO.getImagen().isEmpty()) {
            log.info("Actualizando imagen de usuario con ID: {}", idUsuario);

            // Eliminar imagen anterior si existe
            if (user.getImagen() != null && !user.getImagen().isEmpty()) {
                log.info("Eliminando imagen anterior de Cloudinary...");
                cloudinaryService.deleteImageUsuarios(user.getImagen());
            }

            //Subir nueva imagen
            String nuevaImagenUrl = cloudinaryService.uploadImageUsuarios(updateDTO.getImagen());
            user.setImagen(nuevaImagenUrl);
            log.info("Nueva imagen subida: {}", nuevaImagenUrl);
        }

        // Actualizar otros campos
        if (updateDTO.getEmail() != null) user.setEmail(updateDTO.getEmail());
        if (updateDTO.getUsername() != null) user.setUsername(updateDTO.getUsername());

        // Solo actualizar contraseña si se proporciona
        if (updateDTO.getContrasena() != null && !updateDTO.getContrasena().isBlank()) {
            user.setContrasena(passwordEncoder.encode(updateDTO.getContrasena()));
            log.info("Contraseña del usuario actualizada");
        }

        if (updateDTO.getRol() != null) user.setRol(updateDTO.getRol());
        if (updateDTO.getEstado() != null) user.setEstado(updateDTO.getEstado());

        // Actualizar creadoPor si se proporciona
        if (updateDTO.getCreadoPorId() != null) {
            User creadoPor = userRepository.findById(updateDTO.getCreadoPorId())
                    .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + updateDTO.getCreadoPorId()));
            user.setCreadoPor(creadoPor);
        }

        // Actualizar sucursal si se proporciona
        if (updateDTO.getIdSucursal() != null) {
            Subsidiary subsidiary = subsidiaryRepository.findById(updateDTO.getIdSucursal())
                    .orElseThrow(() -> new ResourceNotFoundException(SUBSIDIARY_NOT_FOUND + updateDTO.getIdSucursal()));
            user.setSucursal(subsidiary);
            log.info("Sucursal del usuario actualizada a: {}", updateDTO.getIdSucursal());
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuario actualizado exitosamente con ID: {}", idUsuario);
        return convertToResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long idUsuario) {
        log.info("Eliminando usuario con ID: {}", idUsuario);

        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + idUsuario));

        //  ELIMINAR IMAGEN DE CLOUDINARY ANTES DE ELIMINAR EL USUARIO
        if (user.getImagen() != null && !user.getImagen().isEmpty()) {
            log.info("Eliminando imagen de Cloudinary antes de eliminar usuario...");
            cloudinaryService.deleteImageUsuarios(user.getImagen());
        }

        userRepository.deleteById(idUsuario);
        log.info("Usuario eliminado exitosamente con ID: {}", idUsuario);
    }

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

    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = UserResponseDTO.builder()
                .idUsuario(user.getIdUsuario())
                .email(user.getEmail())
                .username(user.getUsername())
                .rol(user.getRol())
                .estado(user.getEstado())
                .imagen(user.getImagen()) // ← INCLUIR URL DE IMAGEN
                .fechaCreacion(user.getFechaCreacion())
                .build();

        if (user.getCreadoPor() != null) {
            dto.setCreadoPorId(user.getCreadoPor().getIdUsuario());
            dto.setCreadoPorUsername(user.getCreadoPor().getUsername());
        }

        if (user.getSucursal() != null) {
            dto.setIdSucursal(user.getSucursal().getIdSubsidiary());
            dto.setSucursalNombre(user.getSucursal().getName());
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersCreatedBy(Long creadoPorId) {
        log.info("Obteniendo usuarios creados por el usuario: {}", creadoPorId);
        if (!userRepository.existsById(creadoPorId)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + creadoPorId);
        }
        return userRepository.findByCreadoPor_IdUsuario(creadoPorId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersCreatedByWithRole(Long creadoPorId, UserRol rol) {
        log.info("Obteniendo usuarios con rol {} creados por el usuario: {}", rol, creadoPorId);
        if (!userRepository.existsById(creadoPorId)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_BY_ID + creadoPorId);
        }
        return userRepository.findByCreadoPor_IdUsuarioAndRol(creadoPorId, rol)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersBySucursal(Long idSucursal) {
        log.info("Obteniendo usuarios de la sucursal: {}", idSucursal);
        if (!subsidiaryRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException(SUBSIDIARY_NOT_FOUND + idSucursal);
        }
        return userRepository.findBySucursal_IdSubsidiary(idSucursal)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersBySucursalAndRol(Long idSucursal, UserRol rol) {
        log.info("Obteniendo usuarios con rol {} de la sucursal: {}", rol, idSucursal);
        if (!subsidiaryRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException(SUBSIDIARY_NOT_FOUND + idSucursal);
        }
        return userRepository.findBySucursal_IdSubsidiaryAndRol(idSucursal, rol)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }
}