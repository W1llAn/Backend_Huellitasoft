package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.user.UserCreateDTO;
import huellitassoft_web.huellitasoft.dto.user.UserResponseDTO;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de usuarios.
 * Define el contrato de operaciones CRUD y consultas relacionadas con usuarios.
 */
public interface UserService {

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios como DTOs
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Obtiene un usuario por su ID.
     *
     * @param idUsuario el ID del usuario
     * @return el usuario como DTO
     */
    UserResponseDTO getUserById(Long idUsuario);

    /**
     * Obtiene un usuario por su email.
     *
     * @param email el email del usuario
     * @return el usuario como DTO
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param usuario el nombre de usuario
     * @return el usuario como DTO
     */
    UserResponseDTO getUserByUsuario(String usuario);

    /**
     * Obtiene todos los usuarios con un rol específico.
     *
     * @param rol el rol a buscar
     * @return lista de usuarios con ese rol como DTOs
     */
    List<UserResponseDTO> getUsersByRole(UserRol rol);

    /**
     * Crea un nuevo usuario.
     *
     * @param createDTO DTO con la información del usuario
     * @return el usuario creado como DTO
     */
    UserResponseDTO createUser(UserCreateDTO createDTO);

    /**
     * Actualiza un usuario existente.
     *
     * @param idUsuario el ID del usuario a actualizar
     * @param updateDTO DTO con los datos a actualizar
     * @return el usuario actualizado como DTO
     */
    UserResponseDTO updateUser(Long idUsuario, UserCreateDTO updateDTO);

    /**
     * Elimina un usuario.
     *
     * @param idUsuario el ID del usuario a eliminar
     */
    void deleteUser(Long idUsuario);

    /**
     * Cambia el estado de un usuario.
     *
     * @param idUsuario el ID del usuario
     * @param nuevoEstado el nuevo estado
     * @return el usuario con estado actualizado como DTO
     */
    UserResponseDTO changeUserState(Long idUsuario, UserState nuevoEstado);

    /**
     * Obtiene todos los usuarios creados por un usuario específico.
     *
     * @param creadoPorId el ID del usuario que creó otros usuarios
     * @return lista de usuarios creados por el usuario especificado
     */
    List<UserResponseDTO> getUsersCreatedBy(Long creadoPorId);

    /**
     * Obtiene todos los usuarios con un rol específico creados por un usuario específico.
     *
     * @param creadoPorId el ID del usuario que creó otros usuarios
     * @param rol el rol a buscar
     * @return lista de usuarios con el rol especificado creados por el usuario
     */
    List<UserResponseDTO> getUsersCreatedByWithRole(Long creadoPorId, UserRol rol);

    /**
     * Obtiene todos los usuarios veterinarios de una sucursal específica.
     *
     * @param idSucursal el ID de la sucursal
     * @return lista de usuarios veterinarios de la sucursal
     */
    List<UserResponseDTO> getUsersBySucursal(Long idSucursal);

    /**
     * Obtiene todos los usuarios veterinarios de una sucursal con un rol específico.
     *
     * @param idSucursal el ID de la sucursal
     * @param rol el rol a buscar (VETERINARIO o ADMIN_VETERINARIA)
     * @return lista de usuarios veterinarios de la sucursal con el rol especificado
     */
    List<UserResponseDTO> getUsersBySucursalAndRol(Long idSucursal, UserRol rol);
}

