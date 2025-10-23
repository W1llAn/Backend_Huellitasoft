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
public interface UserRepository extends JpaRepository<User, Integer> {

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
}
