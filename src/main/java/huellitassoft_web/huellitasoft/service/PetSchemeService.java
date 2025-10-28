package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeResponseDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeUpdateStateDTO;

import java.util.List;

public interface PetSchemeService {
    /**
     * Asigna un esquema a una mascota. Estado por defecto: ACTIVO.
     *
     * @param dto {idMascota, idEsquema, estado?}
     * @return relación creada
     * @throws ResourceNotFoundException      si la mascota o el esquema no existen
     * @throws ResourceAlreadyExistsException si ya existe la relación (idMascota, idEsquema)
     */
    PetSchemeResponseDTO assignSchemeToPet(PetSchemeCreateDTO dto);

    /**
     * Actualiza el estado de la relación (ACTIVO, COMPLETADO, CANCELADO).
     *
     * @param idMascotaEsquema id de la relación
     * @param dto              nuevo estado
     * @return relación actualizada
     * @throws ResourceNotFoundException si no existe la relación
     */
    PetSchemeResponseDTO updateSchemeState(Long idMascotaEsquema, PetSchemeUpdateStateDTO dto);

    /**
     * Desasigna (elimina) la relación mascota ↔ esquema.
     *
     * @param idMascotaEsquema id de la relación
     * @throws ResourceNotFoundException si no existe
     */
    void unassignScheme(Long idMascotaEsquema);

    /**
     * Obtiene una relación por su ID.
     *
     * @param idMascotaEsquema id de la relación
     * @return relación encontrada
     * @throws ResourceNotFoundException si no existe
     */
    PetSchemeResponseDTO getPetSchemeById(Long idMascotaEsquema);

    /**
     * Lista todas las relaciones de una mascota.
     *
     * @param idMascota id de la mascota
     * @return lista de relaciones (cualquier estado)
     */
    List<PetSchemeResponseDTO> getPetSchemes(Long idMascota);

    /**
     * Lista las relaciones ACTIVO de una mascota.
     *
     * @param idMascota id de la mascota
     * @return lista de relaciones con estado ACTIVO
     */
    List<PetSchemeResponseDTO> getActivePetSchemes(Long idMascota);
}
