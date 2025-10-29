package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Subsidiary;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubsidiaryRepository extends JpaRepository<Subsidiary, Long> {
    List<Subsidiary> findByState(SubsidiaryState state);
    Optional<Subsidiary> findByName(String name);
    boolean existsByName(String name);

    /**
     * Obtiene todas las sucursales gestionadas por un usuario específico.
     *
     * @param idUsuario el ID del usuario gestor
     * @return lista de sucursales gestionadas por el usuario
     */
    List<Subsidiary> findByManager_IdUsuario(Long idUsuario);

    /**
     * Obtiene las sucursales gestionadas por un usuario en un estado específico.
     *
     * @param idUsuario el ID del usuario gestor
     * @param state el estado de la sucursal
     * @return lista de sucursales filtradas
     */
    List<Subsidiary> findByManager_IdUsuarioAndState(Long idUsuario, SubsidiaryState state);

    /**
     * Verifica si una sucursal existe para un usuario específico.
     *
     * @param idSubsidiary el ID de la sucursal
     * @param idUsuario el ID del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByIdSubsidiaryAndManager_IdUsuario(Long idSubsidiary, Long idUsuario);
}
