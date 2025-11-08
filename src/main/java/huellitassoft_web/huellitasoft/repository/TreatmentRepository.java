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
     * Encuentra todos los tratamientos asociados a una consulta específica
     * @param idConsulta ID de la consulta
     * @return Lista de tratamientos
     */
    List<Treatment> findByConsultation_IdConsulta(Long idConsulta);

    /**
     * Encuentra todos los tratamientos de una mascota específica
     * @param idMascota ID de la mascota
     * @return Lista de tratamientos
     */
    List<Treatment> findByPet_IdMascota(Long idMascota);

    /**
     * Encuentra todos los tratamientos activos de una mascota específica
     * @param idMascota ID de la mascota
     * @return Lista de tratamientos activos
     */
    @Query("SELECT t FROM Treatment t WHERE t.pet.idMascota = :idMascota AND t.status = true")
    List<Treatment> findActiveByMascota(@Param("idMascota") Long idMascota);

    /**
     * Encuentra todos los tratamientos activos del sistema
     * @return Lista de tratamientos activos
     */
    @Query("SELECT t FROM Treatment t WHERE t.status = true")
    List<Treatment> findAllActive();

    /**
     * Encuentra tratamientos por medicamento
     * @param medication Nombre del medicamento
     * @return Lista de tratamientos
     */
    List<Treatment> findByMedicationContainingIgnoreCase(String medication);
}
