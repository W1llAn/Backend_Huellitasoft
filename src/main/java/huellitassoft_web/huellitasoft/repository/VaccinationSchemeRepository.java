package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.VaccinationScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VaccinationSchemeRepository extends JpaRepository<VaccinationScheme, Long> {


    /**
     * Obtiene todos los esquemas asociados a una vacuna específica.
     *
     * @param idVacuna ID de la vacuna
     * @return Lista de esquemas relacionados con la vacuna
     */
    List<VaccinationScheme> findByVaccine_IdVacuna(Long idVacuna);

    /**
     * Busca un esquema específico según la vacuna y el número de dosis.
     *
     * @param idVacuna     ID de la vacuna
     * @param dosisNumero   Número de dosis dentro del esquema
     * @return Optional con el esquema si existe
     */
    Optional<VaccinationScheme> findByVaccine_IdVacunaAndDosisNumero(Long idVacuna, Integer dosisNumero);

    /**
     * Verifica si ya existe un esquema para una vacuna con un número de dosis determinado.
     *
     * @param idVacuna     ID de la vacuna
     * @param dosisNumero  Número de dosis
     * @return true si existe, false si no
     */
    boolean existsByVaccine_IdVacunaAndDosisNumero(Long idVacuna, Integer dosisNumero);
}
