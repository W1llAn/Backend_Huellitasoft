package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.UserRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Proporciona métodos CRUD y búsquedas personalizadas.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email.
     *
     * @param email el email del usuario
     * @return Un Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param usuario el nombre de usuario
     * @return Un Optional con el usuario si existe
     */
    Optional<User> findByUsername(String usuario);

    /**
     * Obtiene todos los usuarios con un rol específico.
     *
     * @param rol el rol a buscar
     * @return Una lista de usuarios con el rol especificado
     */
    List<User> findByRol(UserRol rol);

    /**
     * Verifica si existe un usuario con un email específico.
     *
     * @param email el email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con un nombre de usuario específico.
     *
     * @param usuario el nombre de usuario a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String usuario);

    /**
     * Obtiene todos los usuarios creados por un usuario específico.
     *
     * @param creadoPor el usuario que creó otros usuarios
     * @return Una lista de usuarios creados por el usuario especificado
     */
    List<User> findByCreadoPor(User creadoPor);

    /**
     * Obtiene todos los usuarios creados por un usuario específico usando su ID.
     *
     * @param creadoPorId el ID del usuario que creó otros usuarios
     * @return Una lista de usuarios creados por el usuario especificado
     */
    List<User> findByCreadoPor_IdUsuario(Long creadoPorId);

    /**
     * Obtiene todos los usuarios con un rol específico creados por un usuario específico.
     *
     * @param creadoPorId el ID del usuario que creó otros usuarios
     * @param rol el rol a buscar
     * @return Una lista de usuarios con el rol especificado creados por el usuario
     */
    List<User> findByCreadoPor_IdUsuarioAndRol(Long creadoPorId, UserRol rol);

    /**
     * Obtiene todos los usuarios asociados a una sucursal específica.
     *
     * @param idSucursal el ID de la sucursal
     * @return Una lista de usuarios de la sucursal especificada
     */
    List<User> findBySucursal_IdSubsidiary(Long idSucursal);

    /**
     * Obtiene todos los usuarios veterinarios (VETERINARIO o ADMIN_VETERINARIA) de una sucursal.
     *
     * @param idSucursal el ID de la sucursal
     * @param rol el rol a buscar (VETERINARIO o ADMIN_VETERINARIA)
     * @return Una lista de usuarios veterinarios de la sucursal
     */
    List<User> findBySucursal_IdSubsidiaryAndRol(Long idSucursal, UserRol rol);

    /**
     * Verifica si existe un usuario con una sucursal específica.
     *
     * @param idUsuario el ID del usuario
     * @param idSucursal el ID de la sucursal
     * @return true si existe, false si no
     */
    boolean existsByIdUsuarioAndSucursal_IdSubsidiary(Long idUsuario, Long idSucursal);
}
