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
    UserResponseDTO getUserById(Integer idUsuario);

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
    UserResponseDTO updateUser(Integer idUsuario, UserCreateDTO updateDTO);

    /**
     * Elimina un usuario.
     *
     * @param idUsuario el ID del usuario a eliminar
     */
    void deleteUser(Integer idUsuario);

    /**
     * Cambia el estado de un usuario.
     *
     * @param idUsuario el ID del usuario
     * @param nuevoEstado el nuevo estado
     * @return el usuario con estado actualizado como DTO
     */
    UserResponseDTO changeUserState(Integer idUsuario, UserState nuevoEstado);
}

