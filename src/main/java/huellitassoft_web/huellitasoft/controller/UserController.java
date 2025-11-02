package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.user.UserCreateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserUpdateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserResponseDTO;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import huellitassoft_web.huellitasoft.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de usuarios.
 * Proporciona endpoints para CRUD completo de usuarios.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints para gestión de usuarios")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene todos los usuarios.
     * Solo accesible para administradores (ADMINISTRADOR o ADMINISTRADOR_VETERINARIA).
     *
     * @return lista de todos los usuarios
     */
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene la lista completa de usuarios. Solo disponible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere rol ADMINISTRADOR o ADMINISTRADOR_VETERINARIA"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/users - Obteniendo todos los usuarios");
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param idUsuario el ID del usuario
     * @return el usuario solicitado
     */
    @GetMapping("/{idUsuario}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long idUsuario) {
        log.info("GET /api/users/{} - Obteniendo usuario por ID", idUsuario);
        UserResponseDTO user = userService.getUserById(idUsuario);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email el email del usuario
     * @return el usuario con el email especificado
     */
    @GetMapping("/email/{email}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuario por email", description = "Obtiene los detalles de un usuario por su email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{} - Obteniendo usuario por email", email);
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param usuario el nombre de usuario
     * @return el usuario con el nombre especificado
     */
    @GetMapping("/usuario/{usuario}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuario por nombre", description = "Obtiene los detalles de un usuario por su nombre de usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserResponseDTO> getUserByUsuario(@PathVariable String usuario) {
        log.info("GET /api/users/usuario/{} - Obteniendo usuario por nombre", usuario);
        UserResponseDTO user = userService.getUserByUsuario(usuario);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene todos los usuarios con un rol específico.
     * Solo accesible para administradores (ADMINISTRADOR o ADMINISTRADOR_VETERINARIA).
     *
     * @param rol el rol a filtrar
     * @return lista de usuarios con el rol especificado
     */
    @GetMapping("/role/{rol}")
    @Operation(summary = "Obtener usuarios por rol", description = "Obtiene todos los usuarios con un rol específico. Solo disponible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable UserRol rol) {
        log.info("GET /api/users/role/{} - Obteniendo usuarios por rol", rol);
        List<UserResponseDTO> users = userService.getUsersByRole(rol);
        return ResponseEntity.ok(users);
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param createDTO DTO con la información del usuario
     * @return el usuario creado con su ID
     */
    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El email o usuario ya existe")
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        log.info("POST /api/users - Creando nuevo usuario con email: {} y usuario: {}", createDTO.getEmail(), createDTO.getUsername());
        UserResponseDTO user = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param idUsuario el ID del usuario a actualizar
     * @param updateDTO DTO con los datos a actualizar
     * @return el usuario actualizado
     */
    @PutMapping("/{idUsuario}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente. La contraseña es opcional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El email o usuario ya existe"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long idUsuario, @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("PUT /api/users/{} - Actualizando usuario", idUsuario);
        UserResponseDTO user = userService.updateUser(idUsuario, updateDTO);
        return ResponseEntity.ok(user);
    }

    /**
     * Cambia el estado de un usuario.
     * Solo accesible para administradores (ADMINISTRADOR o ADMINISTRADOR_VETERINARIA).
     *
     * @param idUsuario el ID del usuario
     * @param nuevoEstado el nuevo estado del usuario
     * @return el usuario con estado actualizado
     */
    @PatchMapping("/{idUsuario}/state")
    @Operation(summary = "Cambiar estado del usuario", description = "Cambia el estado (activo, inactivo, etc.) de un usuario. Solo disponible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserResponseDTO> changeUserState(
            @PathVariable Long idUsuario,
            @RequestParam UserState nuevoEstado) {
        log.info("PATCH /api/users/{}/state - Cambiando estado del usuario a {}", idUsuario, nuevoEstado);
        UserResponseDTO user = userService.changeUserState(idUsuario, nuevoEstado);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene todos los usuarios creados por un usuario específico.
     * Útil para administrador_veterinaria ver los veterinarios que ha creado.
     *
     * @param creadoPorId el ID del usuario que creó otros usuarios
     * @return lista de usuarios creados por el usuario especificado
     */
    @GetMapping("/creados-por/{creadoPorId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuarios creados por un administrador", description = "Obtiene todos los usuarios que fueron creados por un usuario específico (típicamente un ADMINISTRADOR_VETERINARIA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario creador no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersCreatedBy(@PathVariable Long creadoPorId) {
        log.info("GET /api/users/creados-por/{} - Obteniendo usuarios creados por", creadoPorId);
        List<UserResponseDTO> users = userService.getUsersCreatedBy(creadoPorId);
        return ResponseEntity.ok(users);
    }

    /**
     * Obtiene todos los usuarios con un rol específico creados por un usuario específico.
     * Útil para administrador_veterinaria ver solo los veterinarios que ha creado.
     *
     * @param creadoPorId el ID del usuario que creó otros usuarios
     * @param rol el rol a filtrar
     * @return lista de usuarios con el rol especificado creados por el usuario
     */
    @GetMapping("/creados-por/{creadoPorId}/role/{rol}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuarios creados por un administrador con rol específico", description = "Obtiene todos los usuarios con un rol específico que fueron creados por un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario creador no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersCreatedByWithRole(
            @PathVariable Long creadoPorId,
            @PathVariable UserRol rol) {
        log.info("GET /api/users/creados-por/{}/role/{} - Obteniendo usuarios con rol {} creados por", creadoPorId, rol, rol);
        List<UserResponseDTO> users = userService.getUsersCreatedByWithRole(creadoPorId, rol);
        return ResponseEntity.ok(users);
    }

    /**
     * Elimina un usuario.
     * Solo accesible para administradores (ADMINISTRADOR o ADMINISTRADOR_VETERINARIA).
     *
     * @param idUsuario el ID del usuario a eliminar
     * @return respuesta sin contenido
     */
    @DeleteMapping("/{idUsuario}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema. Solo disponible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long idUsuario) {
        log.info("DELETE /api/users/{} - Eliminando usuario", idUsuario);
        userService.deleteUser(idUsuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los usuarios de una sucursal específica.
     * Solo accesible para administradores y usuarios de esa sucursal.
     *
     * @param idSucursal el ID de la sucursal
     * @return lista de usuarios de la sucursal
     */
    @GetMapping("/sucursal/{idSucursal}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuarios por sucursal", description = "Obtiene todos los usuarios veterinarios asociados a una sucursal específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersBySucursal(@PathVariable Long idSucursal) {
        log.info("GET /api/users/sucursal/{} - Obteniendo usuarios de la sucursal", idSucursal);
        List<UserResponseDTO> users = userService.getUsersBySucursal(idSucursal);
        return ResponseEntity.ok(users);
    }

    /**
     * Obtiene todos los usuarios de una sucursal con un rol específico.
     * Solo accesible para administradores.
     *
     * @param idSucursal el ID de la sucursal
     * @param rol el rol a filtrar
     * @return lista de usuarios de la sucursal con el rol especificado
     */
    @GetMapping("/sucursal/{idSucursal}/rol/{rol}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener usuarios por sucursal y rol", description = "Obtiene todos los usuarios de una sucursal con un rol específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersBySucursalAndRol(
            @PathVariable Long idSucursal,
            @PathVariable UserRol rol) {
        log.info("GET /api/users/sucursal/{}/rol/{} - Obteniendo usuarios de la sucursal con rol", idSucursal, rol);
        List<UserResponseDTO> users = userService.getUsersBySucursalAndRol(idSucursal, rol);
        return ResponseEntity.ok(users);
    }
}
