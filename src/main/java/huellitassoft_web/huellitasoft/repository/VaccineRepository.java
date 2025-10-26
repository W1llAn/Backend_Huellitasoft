package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Race;
import huellitassoft_web.huellitasoft.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    /**
     * Busca una vacuna por su nombre
     *
     * @param nombre El nombre de la vacuna
     * @return Optional con la vacuna ya existente
     */
    Optional<Vaccine> findByNombre(String nombre);

    /**
     * Verifica si existe una vacuna con el nombre especificado
     *
     * @param nombre El nombre de la vacuna
     * @return true si existe, false si no encuentra no existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca todas las razas de una especie
     *
     * @param idEspecie El ID de la especie
     * @return Lista de razas que pertenecen a la especie
     */
    List<Vaccine> findBySpecie_IdEspecie(Long idEspecie);
}
