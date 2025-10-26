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
}