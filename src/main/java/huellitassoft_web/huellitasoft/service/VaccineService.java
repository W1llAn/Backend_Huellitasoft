package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineResponseDTO;

import java.util.List;

public interface VaccineService {
    /**
     * Obtener todas las vacunas
     *
     * @return lista de todas las vacunas
     */
    List<VaccineResponseDTO> getAllVaccines();

    /**
     * Obtener una vacuna por ID
     *
     * @param idVacuna de la vacuna
     * @return VaccineResposeDTO con los datos de la vacuna
     * @throws ResourceNotFoundException si la vacuna no existe con ese ID
     */
    VaccineResponseDTO getVaccineById(Long idVacuna);

    /**
     * Crear una nueva vacuna
     *
     * @throws ResourceAlreadyExistException si ya existe una vacuna con ese nombre
     * @Param VaccineCreateDTO  datos de la vacuba
     * @Return VaccineResposeDTO con los datos de la vacuna creada
     */
    VaccineResponseDTO createVaccine(VaccineCreateDTO vaccineCreateDTO);

    /**
     * Actualizar una vacuna existente
     *
     * @param idVacuna         Id de la vacuana a actualizar
     * @param vaccineCreateDTO nuevos datos de la vacuna
     * @return VaccineResposeDTO con los datos actualizados
     * @throws ResourceNotFoundException      si la vacuna no existe
     * @throws ResourceAlreadyExistsException si el nuevo nombre ya existe en otra vacuna
     */
    VaccineResponseDTO updateVaccine(Long idVacuna, VaccineCreateDTO vaccineCreateDTO);

    /**
     * Elimina una vacuna
     *
     * @param idVacuna ID de la vacuna a eliminar
     * @throws ResourceNotFoundException si la vacuna no existe
     */
    void deleteVaccine(Long idVacuna);

    /**
     * Obtiene todas las vacunas de una especie
     *
     * @param idEspecie ID de la especie
     * @return Lista de vacunas que pertenecen a la especie
     * @throws ResourceNotFoundException si la especie no existe
     */
    List<VaccineResponseDTO> getVaccinesBySpecie(Long idEspecie);
}
