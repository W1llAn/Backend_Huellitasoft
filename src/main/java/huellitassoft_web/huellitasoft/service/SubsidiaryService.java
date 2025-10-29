package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryRequestDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryResponseDTO;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;

import java.util.List;

public interface SubsidiaryService {
    SubsidiaryResponseDTO createSubsidiary(SubsidiaryRequestDTO requestDTO);
    SubsidiaryResponseDTO getSubsidiaryById(Long id);
    List<SubsidiaryResponseDTO> getAllSubsidiaries();
    List<SubsidiaryResponseDTO> getSubsidiariesByState(SubsidiaryState state);
    SubsidiaryResponseDTO updateSubsidiary(Long id, SubsidiaryRequestDTO requestDTO);
    void deleteSubsidiary(Long id);

    /**
     * Obtiene todas las sucursales gestionadas por un usuario específico.
     *
     * @param idUsuario el ID del usuario gestor
     * @return lista de sucursales del usuario
     */
    List<SubsidiaryResponseDTO> getSubsidiariesByManager(Long idUsuario);

    /**
     * Obtiene las sucursales gestionadas por un usuario en un estado específico.
     *
     * @param idUsuario el ID del usuario gestor
     * @param state el estado de la sucursal
     * @return lista de sucursales filtradas
     */
    List<SubsidiaryResponseDTO> getSubsidiariesByManagerAndState(Long idUsuario, SubsidiaryState state);
}