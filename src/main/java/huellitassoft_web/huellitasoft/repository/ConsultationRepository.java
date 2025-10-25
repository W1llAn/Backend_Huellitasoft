package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    /**
     * Busca todas las consultas de un historial clínico
     * @param idHistoria ID del historial clínico
     * @return Lista de consultas
     */
    List<Consultation> findByMedicalHistory_IdHistoria(Long idHistoria);

    /**
     * Busca todas las consultas de una mascota
     * @param idMascota ID de la mascota
     * @return Lista de consultas
     */
    @Query("SELECT c FROM Consultation c WHERE c.medicalHistory.mascota.idMascota = :idMascota ORDER BY c.fechaHora DESC")
    List<Consultation> findByMascota_IdMascota(@Param("idMascota") Long idMascota);

    /**
     * Busca una consulta por su ID
     * @param idConsulta ID de la consulta
     * @return Optional con la consulta
     */
    Optional<Consultation> findById(Long idConsulta);

    /**
     * Busca consultas de un veterinario
     * @param idVeterinario ID del veterinario
     * @return Lista de consultas
     */
    List<Consultation> findByVeterinarian_IdUsuario(Integer idVeterinario);

    /**
     * Busca consultas en un rango de fechas
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @return Lista de consultas
     */
    @Query("SELECT c FROM Consultation c WHERE c.fechaHora BETWEEN :inicio AND :fin ORDER BY c.fechaHora DESC")
    List<Consultation> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    /**
     * Busca el número de consultas de un historial
     * @param idHistoria ID del historial clínico
     * @return Cantidad de consultas
     */
    long countByMedicalHistory_IdHistoria(Long idHistoria);

    /**
     * Busca la consulta más reciente de una mascota
     * @param idMascota ID de la mascota
     * @return Optional con la consulta más reciente
     */
    @Query(value = "SELECT c FROM Consultation c WHERE c.medicalHistory.mascota.idMascota = :idMascota ORDER BY c.fechaHora DESC LIMIT 1", nativeQuery = false)
    Optional<Consultation> findLastConsultationByMascota(@Param("idMascota") Long idMascota);
}
