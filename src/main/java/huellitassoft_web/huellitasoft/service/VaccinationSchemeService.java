package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeResponseDTO;

import java.util.List;

public interface VaccinationSchemeService {

    /**
     * Lista todos los esquemas de vacunación.
     *
     * @return lista de esquemas
     */
    List<VaccinationSchemeResponseDTO> getAllSchemes();

    /**
     * Obtiene un esquema por su ID.
     *
     * @param idEsquema identificador del esquema
     * @return esquema encontrado
     * @throws ResourceNotFoundException si no existe
     */
    VaccinationSchemeResponseDTO getSchemeById(Long idEsquema);

    /**
     * Crea un esquema de vacunación.
     *
     * @param dto datos del esquema (vacuna, dosis, edad, observaciones)
     * @return esquema creado
     * @throws ResourceNotFoundException si la vacuna no existe
     * @throws ResourceAlreadyExistsException si ya existe (idVacuna, dosisNumero)
     */
    VaccinationSchemeResponseDTO createScheme(VaccinationSchemeCreateDTO dto);

    /**
     * Actualiza un esquema de vacunación.
     *
     * @param idEsquema id del esquema a actualizar
     * @param dto nuevos datos del esquema
     * @return esquema actualizado
     * @throws ResourceNotFoundException si el esquema o la vacuna no existen
     * @throws ResourceAlreadyExistsException si (idVacuna, dosisNumero) entra en conflicto con otro esquema
     */
    VaccinationSchemeResponseDTO updateScheme(Long idEsquema, VaccinationSchemeCreateDTO dto);

    /**
     * Elimina un esquema de vacunación.
     *
     * @param idEsquema id del esquema
     * @throws ResourceNotFoundException si no existe
     */
    void deleteScheme(Long idEsquema);

    /**
     * Lista esquemas por vacuna.
     *
     * @param idVacuna id de la vacuna
     * @return lista de esquemas asociados a esa vacuna
     * @throws ResourceNotFoundException si la vacuna no existe
     */
    List<VaccinationSchemeResponseDTO> getSchemesByVaccine(Long idVacuna);
}
