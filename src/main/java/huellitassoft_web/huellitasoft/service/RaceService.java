package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.race.RaceCreateDTO;
import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;

import java.util.List;

public interface RaceService {

    /**
     * Obtiene todas las razas
     *
     * @return Lista de todas las razas
     */
    List<RaceResponseDTO> getAllRaces();

    /**
     * Obtiene una raza por su ID
     *
     * @param idRaza ID de la raza
     * @return RaceResponseDTO con los datos de la raza
     * @throws ResourceNotFoundException si la raza no existe
     */
    RaceResponseDTO getRaceById(Long idRaza);

    /**
     * Obtiene todas las razas de una especie
     *
     * @param idEspecie ID de la especie
     * @return Lista de razas que pertenecen a la especie
     * @throws ResourceNotFoundException si la especie no existe
     */
    List<RaceResponseDTO> getRacesBySpecie(Long idEspecie);

    /**
     * Crea una nueva raza
     *
     * @param raceCreateDTO Datos de la raza a crear
     * @return RaceResponseDTO con los datos de la raza creada
     * @throws ResourceNotFoundException si la especie no existe
     * @throws ResourceAlreadyExistsException si ya existe una raza con ese nombre
     */
    RaceResponseDTO createRace(RaceCreateDTO raceCreateDTO);

    /**
     * Actualiza una raza existente
     *
     * @param idRaza ID de la raza a actualizar
     * @param raceCreateDTO Nuevos datos de la raza
     * @return RaceResponseDTO con los datos actualizados
     * @throws ResourceNotFoundException si la raza no existe
     * @throws ResourceAlreadyExistsException si el nuevo nombre ya existe en otra raza
     */
    RaceResponseDTO updateRace(Long idRaza, RaceCreateDTO raceCreateDTO);

    /**
     * Elimina una raza
     *
     * @param idRaza ID de la raza a eliminar
     * @throws ResourceNotFoundException si la raza no existe
     */
    void deleteRace(Long idRaza);
}
