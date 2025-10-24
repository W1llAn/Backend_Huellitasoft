package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.specie.SpecieCreateDTO;
import huellitassoft_web.huellitasoft.dto.specie.SpecieResponseDTO;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SpecieRepository;
import huellitassoft_web.huellitasoft.service.SpecieService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class SpecieServiceImpl implements SpecieService {

    private static final String SPECIE_NOT_FOUND = "Especie no encontrada con ID: {}";

    private final SpecieRepository specieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecieResponseDTO> getAllSpecies() {
        log.info("Obteniendo todas las especies");
        return specieRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecieResponseDTO getSpecieById(Long idEspecie) {
        log.info("Obteniendo especie con ID: {}", idEspecie);
        Specie specie = specieRepository.findById(idEspecie)
                .orElseThrow(() -> {
                    log.error(SPECIE_NOT_FOUND, idEspecie);
                    return new ResourceNotFoundException("Especie no encontrada con ID: " + idEspecie);
                });
        return mapToResponseDTO(specie);
    }

    @Override
    public SpecieResponseDTO createSpecie(SpecieCreateDTO specieCreateDTO) {
        log.info("Creando nueva especie con nombre: {}", specieCreateDTO.getNombre());

        // Verificar si la especie ya existe
        if (specieRepository.existsByNombre(specieCreateDTO.getNombre())) {
            log.warn("Intento de crear especie duplicada: {}", specieCreateDTO.getNombre());
            throw new ResourceAlreadyExistsException("Ya existe una especie con el nombre: " + specieCreateDTO.getNombre());
        }

        // Crear la nueva especie
        Specie specie = Specie.builder()
                .nombre(specieCreateDTO.getNombre())
                .build();

        Specie savedSpecie = specieRepository.save(specie);
        log.info("Especie creada exitosamente con ID: {}", savedSpecie.getIdEspecie());
        return mapToResponseDTO(savedSpecie);
    }

    @Override
    public SpecieResponseDTO updateSpecie(Long idEspecie, SpecieCreateDTO specieCreateDTO) {
        log.info("Actualizando especie con ID: {}", idEspecie);

        Specie specie = specieRepository.findById(idEspecie)
                .orElseThrow(() -> {
                    log.error(SPECIE_NOT_FOUND, idEspecie);
                    return new ResourceNotFoundException("Especie no encontrada con ID: " + idEspecie);
                });

        // Verificar si el nuevo nombre ya existe en otra especie
        if (!specie.getNombre().equals(specieCreateDTO.getNombre()) &&
                specieRepository.existsByNombre(specieCreateDTO.getNombre())) {
            log.warn("Intento de actualizar especie con nombre duplicado: {}", specieCreateDTO.getNombre());
            throw new ResourceAlreadyExistsException("Ya existe una especie con el nombre: " + specieCreateDTO.getNombre());
        }

        specie.setNombre(specieCreateDTO.getNombre());

        Specie updatedSpecie = specieRepository.save(specie);
        log.info("Especie actualizada exitosamente con ID: {}", updatedSpecie.getIdEspecie());
        return mapToResponseDTO(updatedSpecie);
    }

    @Override
    public void deleteSpecie(Long idEspecie) {
        log.info("Eliminando especie con ID: {}", idEspecie);

        if (!specieRepository.existsById(idEspecie)) {
            log.error(SPECIE_NOT_FOUND, idEspecie);
            throw new ResourceNotFoundException("Especie no encontrada con ID: " + idEspecie);
        }

        specieRepository.deleteById(idEspecie);
        log.info("Especie eliminada exitosamente con ID: {}", idEspecie);
    }

    /**
     * Mapea una entidad Specie a SpecieResponseDTO
     *
     * @param specie Entidad Specie a mapear
     * @return SpecieResponseDTO con los datos mapeados
     */
    private SpecieResponseDTO mapToResponseDTO(Specie specie) {
        return huellitassoft_web.huellitasoft.dto.specie.SpecieResponseDTO.builder()
                .idEspecie(specie.getIdEspecie())
                .nombre(specie.getNombre())
                .build();
    }
}
