package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.MedicalHistory;
import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    /**
     * Busca un historial clínico por el ID de la mascota
     * @param idMascota ID de la mascota
     * @return Optional con el historial clínico
     */
    Optional<MedicalHistory> findByMascota_IdMascota(Long idMascota);

    /**
     * Busca todos los historiales de una mascota por su ID
     * @param idMascota ID de la mascota
     * @return Lista de historiales clínicos
     */
    List<MedicalHistory> findAllByMascota_IdMascota(Long idMascota);

    /**
     * Busca historiales por estado
     * @param estado Estado del historial
     * @return Lista de historiales clínicos
     */
    List<MedicalHistory> findByEstado(MedicalHistoryState estado);

    /**
     * Busca un historial por número
     * @param numero Número único del historial
     * @return Optional con el historial clínico
     */
    Optional<MedicalHistory> findByNumero(String numero);

    /**
     * Valida si existe un historial para una mascota
     * @param idMascota ID de la mascota
     * @return true si existe, false en caso contrario
     */
    boolean existsByMascota_IdMascota(Long idMascota);

    /**
     * Busca historiales activos de una mascota
     * @param idMascota ID de la mascota
     * @param estado Estado del historial
     * @return Lista de historiales clínicos
     */
    @Query("SELECT mh FROM MedicalHistory mh WHERE mh.mascota.idMascota = :idMascota AND mh.estado = :estado")
    List<MedicalHistory> findByMascotaAndEstado(@Param("idMascota") Long idMascota, @Param("estado") MedicalHistoryState estado);
}
