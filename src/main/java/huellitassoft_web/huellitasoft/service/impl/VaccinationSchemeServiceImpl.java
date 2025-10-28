package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeResponseDTO;
import huellitassoft_web.huellitasoft.entity.VaccinationScheme;
import huellitassoft_web.huellitasoft.entity.Vaccine;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.VaccinationSchemeRepository;
import huellitassoft_web.huellitasoft.repository.VaccineRepository;
import huellitassoft_web.huellitasoft.service.VaccinationSchemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VaccinationSchemeServiceImpl implements VaccinationSchemeService {

    private static final String VACCINESCHEME_NOT_FOUND = "Esquema de vacuna no encontrada con ID: {}";
    private static final String VACCINE_NOT_FOUND = "Vacuna no encontrada con ID: {}";

    private final VaccinationSchemeRepository schemeRepo;
    private final VaccineRepository vaccineRepo;

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationSchemeResponseDTO> getAllSchemes() {
        log.info("Obteniendo todas los esquemas de vacunas");
        return schemeRepo.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VaccinationSchemeResponseDTO getSchemeById(Long idEsquema) {
        log.info("Obteniendo esquema de vacuna  con ID: {}", idEsquema);
        VaccinationScheme s = schemeRepo.findById(idEsquema)
                .orElseThrow(() -> new ResourceNotFoundException("Esquema no encontrado: " + idEsquema));
        return toDTO(s);
    }

    @Override
    public VaccinationSchemeResponseDTO createScheme(VaccinationSchemeCreateDTO dto) {
        log.info("Creando nuevo schema de vacunacion");
        Vaccine v = vaccineRepo.findById(dto.getIdVacuna())
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada: " + dto.getIdVacuna()));

        if (schemeRepo.existsByVaccine_IdVacunaAndDosisNumero(v.getIdVacuna(), dto.getDosisNumero())) {
            throw new ResourceAlreadyExistsException("Ya existe un esquema para esa vacuna con la dosis " + dto.getDosisNumero());
        }

        VaccinationScheme s = VaccinationScheme.builder()
                .vaccine(v)
                .dosisNumero(dto.getDosisNumero())
                .edadSemanas(dto.getEdadSemanas())
                .observaciones(dto.getObservaciones())
                .build();
        VaccinationScheme sv = schemeRepo.save(s);
        log.info("Esquema de vacunación  creada exitosamente con ID: {}", sv.getIdEsquema());

        return toDTO(sv);
    }

    @Override
    public VaccinationSchemeResponseDTO updateScheme(Long idEsquema, VaccinationSchemeCreateDTO dto) {
        log.info("Actualizando esquema con ID: {}", idEsquema);

        VaccinationScheme s = schemeRepo.findById(idEsquema)
                .orElseThrow(() -> {
                    log.error(VACCINESCHEME_NOT_FOUND, idEsquema);
                    return new ResourceNotFoundException("Esquema no encontrado: " + idEsquema);
                });
        //Verificamos si la vacuna existe
        Vaccine v = vaccineRepo.findById(dto.getIdVacuna())
                .orElseThrow(() -> {
                    log.error(VACCINE_NOT_FOUND, dto.getIdVacuna());
                    return new ResourceNotFoundException("Vacuna no encontrada: " + dto.getIdVacuna());
                });

        // si cambian vacuna o dosis, revalidar duplicado
        if ((!s.getVaccine().getIdVacuna().equals(v.getIdVacuna()) ||
                !s.getDosisNumero().equals(dto.getDosisNumero())) &&
                schemeRepo.existsByVaccine_IdVacunaAndDosisNumero(v.getIdVacuna(), dto.getDosisNumero())) {
            throw new ResourceAlreadyExistsException("Ya existe un esquema para esa vacuna con la dosis " + dto.getDosisNumero());
        }

        s.setVaccine(v);
        s.setDosisNumero(dto.getDosisNumero());
        s.setEdadSemanas(dto.getEdadSemanas());
        s.setObservaciones(dto.getObservaciones());

        VaccinationScheme sv = schemeRepo.save(s);
        log.info("Esquema de vacuna  actualizada exitosamente con ID: {}", sv.getIdEsquema());
        return toDTO(sv);
    }

    @Override
    public void deleteScheme(Long idEsquema) {
        log.info("Eliminando esquema de vacuna  con ID: {}", idEsquema);
        if (!schemeRepo.existsById(idEsquema)) {
            log.error(VACCINESCHEME_NOT_FOUND, idEsquema);
            throw new ResourceNotFoundException("Esquema no encontrado: " + idEsquema);
        }
        schemeRepo.deleteById(idEsquema);
        log.info("Esquema de vacuna  eliminada exitosamente con ID: {}", idEsquema);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationSchemeResponseDTO> getSchemesByVaccine(Long idVacuna) {
        log.info("Obteniendo esquemas de la vacuna con ID: {}", idVacuna);
        if (!vaccineRepo.existsById(idVacuna)) {
            log.error(VACCINE_NOT_FOUND, idVacuna);
            throw new ResourceNotFoundException("Vacuna no encontrada: " + idVacuna);
        }
        log.info("Esquemas de la vacuna exitosamente con vacuna ID: {}", idVacuna);
        return schemeRepo.findByVaccine_IdVacuna(idVacuna).stream().map(this::toDTO).toList();
    }

    /**
     * Mapea una entidad de esquema de vacuna a  SchemeResposeDTO
     * @param s Entidad para VaccinationScheme
     * @return VaccinationSchemeResponseDTO con los datos ya mapeados*/

    private VaccinationSchemeResponseDTO toDTO(VaccinationScheme s) {
        return VaccinationSchemeResponseDTO.builder()
                .idEsquema(s.getIdEsquema())
                .idVacuna(s.getVaccine().getIdVacuna())
                .nombreVacuna(s.getVaccine().getNombre())
                .dosisNumero(s.getDosisNumero())
                .edadSemanas(s.getEdadSemanas())
                .observaciones(s.getObservaciones())
                .build();
    }

}
