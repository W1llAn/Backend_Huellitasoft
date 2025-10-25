package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    /**
     * Busca todos los tratamientos de una consulta
     * @param idConsulta ID de la consulta
     * @return Lista de tratamientos
     */
    List<Treatment> findByConsultation_IdConsulta(Long idConsulta);
    

    /**
     * Busca todos los tratamientos de una mascota
     * @param idMascota ID de la mascota
     * @return Lista de tratamientos
     */
    List<Treatment> findByMascota_IdMascota(Long idMascota);


    /**
     * Busca tratamientos activos de una mascota
     * @param idMascota ID de la mascota
     * @param estado Estado del tratamiento
     * @return Lista de tratamientos
     */
    @Query("SELECT t FROM Treatment t WHERE t.mascota.idMascota = :idMascota AND t.estado = :estado")
    List<Treatment> findByMascotaAndEstado(@Param("idMascota") Long idMascota, @Param("estado") Boolean estado);

    /**
     * Busca la cantidad de tratamientos de una consulta
     * @param idConsulta ID de la consulta
     * @return Cantidad de tratamientos
     */
    long countByConsultation_IdConsulta(Long idConsulta);


    /**
     * Valida si existe un tratamiento
     * @param idTratamiento ID del tratamiento
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long idTratamiento);

    /**
     * Busca tratamientos de una mascota por estado
     * @param idMascota ID de la mascota
     * @return Lista de tratamientos activos
     */
    @Query("SELECT t FROM Treatment t WHERE t.mascota.idMascota = :idMascota AND t.estado = true ORDER BY t.idTratamiento DESC")
    List<Treatment> findActiveByMascota(@Param("idMascota") Long idMascota);
}
