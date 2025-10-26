package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.vaccine.VaccineCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineResponseDTO;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.entity.Vaccine;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SpecieRepository;
import huellitassoft_web.huellitasoft.repository.VaccineRepository;
import huellitassoft_web.huellitasoft.service.VaccineService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class VaccineServiceImpl implements VaccineService {

    private static final String VACCINE_NOT_FOUND = "Vacuna no encontrada con ID: {}";
    private static final String SPECIE_NOT_FOUND = "Especie no encontrada con ID: {}";
    private final VaccineRepository vaccineRepository;
    private final SpecieRepository specieRepository;


    @Override
    @Transactional(readOnly = true)
    public List<VaccineResponseDTO> getAllVaccines() {
        log.info("Obteniendo todas las vacunas disponibles");
        return vaccineRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VaccineResponseDTO getVaccineById(Long idVacuna) {
        log.info("Obteniendo vacunas con ID:{}", idVacuna);
        Vaccine vaccine = vaccineRepository.findById(idVacuna)
                .orElseThrow(() -> {
                    log.error(VACCINE_NOT_FOUND, idVacuna);
                    return new ResourceNotFoundException("Vacuna no encontrada con ID: " + idVacuna);
                });
        return mapToResponseDTO((vaccine));
    }

    @Override
    public VaccineResponseDTO createVaccine(VaccineCreateDTO vaccineCreateDTO) {
        log.info("Creando una nueva vacuna con nombre: {}", vaccineCreateDTO.getNombre());
        //Verificamos si la vacuna ya existe
        if (vaccineRepository.existsByNombre(vaccineCreateDTO.getNombre())) {
            log.warn("intento crear una vacuna duplicada: {}", vaccineCreateDTO.getNombre());
            throw new ResourceAlreadyExistsException("Ya existe una vacuna con ese nombre: " + vaccineCreateDTO.getNombre());
        }
        // Verificar que la especie existe
        Specie specie = specieRepository.findById(vaccineCreateDTO.getIdEspecie())
                .orElseThrow(() -> {
                    log.error(SPECIE_NOT_FOUND, vaccineCreateDTO.getIdEspecie());
                    return new ResourceNotFoundException("Especie no encontrada con ID:" + vaccineCreateDTO.getIdEspecie());
                });

        //Creamos la vacuna
        Vaccine vaccine = Vaccine.builder()
                .nombre(vaccineCreateDTO.getNombre())
                .descripcion(vaccineCreateDTO.getDescripcion())
                .specie(specie)
                .build();

        Vaccine saveVaccine = vaccineRepository.save(vaccine);
        log.info("Vacuna creada exitosamente con ID:{}", saveVaccine.getIdVacuna());
        return mapToResponseDTO(saveVaccine);
    }

    @Override
    public VaccineResponseDTO updateVaccine(Long idVacuna, VaccineCreateDTO vaccineCreateDTO) {
        log.info("Actualizando vacuna con Id:{}", idVacuna);
        Vaccine vaccine = vaccineRepository.findById(idVacuna).orElseThrow(() -> {
            log.error(VACCINE_NOT_FOUND, idVacuna);
            return new ResourceNotFoundException("Vacuna no encontrada con ID: " + idVacuna);
        });
        //Verificamos si el nuevo nombre ya existe
        if (!vaccine.getNombre().equals(vaccineCreateDTO.getNombre()) && vaccineRepository.existsByNombre(vaccineCreateDTO.getNombre())) {
            log.warn("Intento de actualizar vacuna con un nombre duplicado:{}", vaccineCreateDTO.getNombre());
            throw new ResourceAlreadyExistsException("Ya existe una vacuna con el nombre: " + vaccineCreateDTO.getNombre());
        }
        // Verificar que la especie existe si se está cambiando
        if (!vaccine.getSpecie().getIdEspecie().equals(vaccineCreateDTO.getIdEspecie())) {
            Specie specie = specieRepository.findById(vaccineCreateDTO.getIdEspecie())
                    .orElseThrow(() -> {
                        log.error(SPECIE_NOT_FOUND, vaccineCreateDTO.getIdEspecie());
                        return new ResourceNotFoundException("Especie no encontrada con ID: " + vaccineCreateDTO.getIdEspecie());
                    });
            vaccine.setSpecie(specie);
        }

        vaccine.setNombre(vaccineCreateDTO.getNombre());
        vaccine.setDescripcion(vaccineCreateDTO.getDescripcion());

        Vaccine updatedVaccine = vaccineRepository.save(vaccine);
        log.info("Vacuna actualizada exitosamente con ID:{}", updatedVaccine.getIdVacuna());
        return mapToResponseDTO(updatedVaccine);
    }

    @Override
    public void deleteVaccine(Long idVacuna) {
        log.info("Eliminando vacuna con ID:{}", idVacuna);
        if (!vaccineRepository.existsById(idVacuna)) {
            log.error(VACCINE_NOT_FOUND, idVacuna);
            throw new ResourceNotFoundException("Vacuna no encontrada con ID: " + idVacuna);
        }
        vaccineRepository.deleteById(idVacuna);
        log.info("Vacuna eliminada exitosamente con ID:{}", idVacuna);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccineResponseDTO> getVaccinesBySpecie(Long idEspecie) {
        log.info("Obteniendo vacunas para la especie con ID: {}", idEspecie);

        // Verificar que la especie existe
        if (!specieRepository.existsById(idEspecie)) {
            log.error(VACCINE_NOT_FOUND, idEspecie);
            throw new ResourceNotFoundException("Especie no encontrada con ID: " + idEspecie);
        }

        return vaccineRepository.findBySpecie_IdEspecie(idEspecie)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Mapea una entidad Vacuna a VaccineResposeDTO
     *
     * @param vaccine Entidad Vaccine a mapear
     * @return VaccineResposeDTO con los datos mapeados
     */
    private VaccineResponseDTO mapToResponseDTO(Vaccine vaccine) {
        return VaccineResponseDTO.builder()
                .idVacuna(vaccine.getIdVacuna())
                .nombre(vaccine.getNombre())
                .descripcion(vaccine.getDescripcion())
                .idEspecie(vaccine.getSpecie().getIdEspecie())
                .build();
    }
}
