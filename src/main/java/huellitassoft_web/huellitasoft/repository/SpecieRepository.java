package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Specie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecieRepository extends JpaRepository<Specie, Long> {

    /**
     * Busca una especie por su nombre
     *
     * @param nombre El nombre de la especie
     * @return Optional con la especie si existe
     */
    Optional<Specie> findByNombre(String nombre);

    /**
     * Verifica si existe una especie con el nombre especificado
     *
     * @param nombre El nombre de la especie
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
