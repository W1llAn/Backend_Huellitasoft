package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    /**
     * Busca una raza por su nombre
     *
     * @param nombre El nombre de la raza
     * @return Optional con la raza si existe
     */
    Optional<Race> findByNombre(String nombre);

    /**
     * Busca todas las razas de una especie
     *
     * @param idEspecie El ID de la especie
     * @return Lista de razas que pertenecen a la especie
     */
    List<Race> findBySpecie_IdEspecie(Long idEspecie);

    /**
     * Verifica si existe una raza con el nombre especificado
     *
     * @param nombre El nombre de la raza
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
