package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationCreateDTO;
import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationResponseDTO;

import java.util.List;

public interface PetVaccinationService {

    /**
     * Registra una nueva vacunación para una mascota.
     * La fecha/hora se setea con la actual y el usuario se obtiene del contexto de seguridad.
     *
     * @param dto                   Datos mínimos de la vacunación (idMascota, idVacuna)
     * @param authenticatedUsername Username del usuario autenticado (veterinario)
     * @return PetVaccinationResponseDTO con los datos del registro creado
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si no existe la mascota o la vacuna o el usuario
     * @throws IllegalStateException                                              si el usuario autenticado no tiene rol de VETERINARIO
     */
    PetVaccinationResponseDTO create(PetVaccinationCreateDTO dto, String authenticatedUsername);

    /**
     * Elimina un registro de vacunación por su ID.
     *
     * @param idVacunacion ID del registro de vacunación
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si no existe el registro
     */
    void delete(Long idVacunacion);

    /**
     * Obtiene un registro de vacunación por su ID.
     *
     * @param idVacunacion ID del registro de vacunación
     * @return PetVaccinationResponseDTO con los datos del registro
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si no existe el registro
     */
    PetVaccinationResponseDTO getById(Long idVacunacion);

    /**
     * Lista todas las vacunaciones de una mascota.
     *
     * @param idMascota ID de la mascota
     * @return Lista de PetVaccinationResponseDTO
     */
    List<PetVaccinationResponseDTO> getByPet(Long idMascota);

    /**
     * Lista todas las vacunaciones donde se aplicó una vacuna específica.
     *
     * @param idVacuna ID de la vacuna
     * @return Lista de PetVaccinationResponseDTO
     */
    List<PetVaccinationResponseDTO> getByVaccine(Long idVacuna);

    /**
     * Lista todas las vacunaciones aplicadas por un veterinario.
     *
     * @param idUsuarioVeterinario ID del usuario (veterinario)
     * @return Lista de PetVaccinationResponseDTO
     */
    List<PetVaccinationResponseDTO> getByVeterinarian(Long idUsuarioVeterinario);

}
