package huellitassoft_web.huellitasoft.repository;


import huellitassoft_web.huellitasoft.entity.PetVaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetVaccinationRepository extends JpaRepository<PetVaccination, Long> {

    /**
     * Busca todas las vacunaciones registradas para una mascota.
     *
     * @param idMascota ID de la mascota
     * @return Lista de vacunaciones asociadas a la mascota
     */
    List<PetVaccination> findByPet_IdMascota(Long idMascota);

    /**
     * Busca todas las vacunaciones aplicadas por un usuario (veterinario).
     *
     * @param idUsuario ID del usuario que aplicó la vacuna
     * @return Lista de vacunaciones aplicadas por el usuario
     */
    List<PetVaccination> findByUser_IdUsuario(Long idUsuario);

    /**
     * Busca todas las vacunaciones registradas de una vacuna específica.
     *
     * @param idVacuna ID de la vacuna
     * @return Lista de vacunaciones donde se aplicó esa vacuna
     */
    List<PetVaccination> findByVaccine_IdVacuna(Long idVacuna);
}
