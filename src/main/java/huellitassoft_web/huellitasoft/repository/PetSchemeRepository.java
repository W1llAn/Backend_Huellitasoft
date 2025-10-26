package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.PetScheme;
import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface PetSchemeRepository extends JpaRepository<PetScheme, Long> {
    /**
     * Obtiene todos los esquemas asignados a una mascota específica.
     *
     * @param idMascota ID de la mascota
     * @return Lista de relaciones mascota-esquema
     */
    List<PetScheme> findByPet_IdMascota(Long idMascota);

    /**
     * Obtiene todos los esquemas de una mascota que tengan un estado determinado.
     *
     * @param idMascota ID de la mascota
     * @param estado    Estado del esquema (ACTIVO, COMPLETADO o CANCELADO)
     * @return Lista de relaciones mascota-esquema filtradas por estado
     */
    List<PetScheme> findByPet_IdMascotaAndEstado(Long idMascota, PetSchemeState estado);

    /**
     * Verifica si ya existe una asignación entre una mascota y un esquema.
     *
     * @param idMascota ID de la mascota
     * @param idEsquema ID del esquema
     * @return true si la relación ya existe, false si no
     */
    boolean existsByPet_IdMascotaAndScheme_IdEsquema(Long idMascota, Long idEsquema);
}
