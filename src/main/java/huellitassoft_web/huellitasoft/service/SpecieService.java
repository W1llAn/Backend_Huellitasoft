package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.specie.SpecieCreateDTO;
import huellitassoft_web.huellitasoft.dto.specie.SpecieResponseDTO;

import java.util.List;

public interface SpecieService {

    /**
     * Obtiene todas las especies
     *
     * @return Lista de todas las especies
     */
    List<SpecieResponseDTO> getAllSpecies();

    /**
     * Obtiene una especie por su ID
     *
     * @param idEspecie ID de la especie
     * @return SpecieResponseDTO con los datos de la especie
     * @throws ResourceNotFoundException si la especie no existe
     */
    SpecieResponseDTO getSpecieById(Long idEspecie);

    /**
     * Crea una nueva especie
     *
     * @param specieCreateDTO Datos de la especie a crear
     * @return SpecieResponseDTO con los datos de la especie creada
     * @throws ResourceAlreadyExistsException si ya existe una especie con ese nombre
     */
    SpecieResponseDTO createSpecie(SpecieCreateDTO specieCreateDTO);

    /**
     * Actualiza una especie existente
     *
     * @param idEspecie ID de la especie a actualizar
     * @param specieCreateDTO Nuevos datos de la especie
     * @return SpecieResponseDTO con los datos actualizados
     * @throws ResourceNotFoundException si la especie no existe
     * @throws ResourceAlreadyExistsException si el nuevo nombre ya existe en otra especie
     */
    SpecieResponseDTO updateSpecie(Long idEspecie, SpecieCreateDTO specieCreateDTO);

    /**
     * Elimina una especie
     *
     * @param idEspecie ID de la especie a eliminar
     * @throws ResourceNotFoundException si la especie no existe
     */
    void deleteSpecie(Long idEspecie);
}
