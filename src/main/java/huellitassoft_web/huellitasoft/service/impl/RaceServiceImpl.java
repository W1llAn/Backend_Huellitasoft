package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.race.RaceCreateDTO;
import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;
import huellitassoft_web.huellitasoft.entity.Race;
import huellitassoft_web.huellitasoft.entity.Specie;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.RaceRepository;
import huellitassoft_web.huellitasoft.repository.SpecieRepository;
import huellitassoft_web.huellitasoft.service.RaceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class RaceServiceImpl implements RaceService {

    private static final String RACE_NOT_FOUND = "Raza no encontrada con ID: {}";
    private static final String SPECIE_NOT_FOUND = "Especie no encontrada con ID: {}";

    private final RaceRepository raceRepository;
    private final SpecieRepository specieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RaceResponseDTO> getAllRaces() {
        log.info("Obteniendo todas las razas");
        return raceRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RaceResponseDTO getRaceById(Long idRaza) {
        log.info("Obteniendo raza con ID: {}", idRaza);
        Race race = raceRepository.findById(idRaza)
                .orElseThrow(() -> {
                    log.error(RACE_NOT_FOUND, idRaza);
                    return new ResourceNotFoundException("Raza no encontrada con ID: " + idRaza);
                });
        return mapToResponseDTO(race);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RaceResponseDTO> getRacesBySpecie(Long idEspecie) {
        log.info("Obteniendo razas para la especie con ID: {}", idEspecie);

        // Verificar que la especie existe
        if (!specieRepository.existsById(idEspecie)) {
            log.error(SPECIE_NOT_FOUND, idEspecie);
            throw new ResourceNotFoundException("Especie no encontrada con ID: " + idEspecie);
        }

        return raceRepository.findBySpecie_IdEspecie(idEspecie)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public RaceResponseDTO createRace(RaceCreateDTO raceCreateDTO) {
        log.info("Creando nueva raza con nombre: {}", raceCreateDTO.getNombre());

        // Verificar si la raza ya existe
        if (raceRepository.existsByNombre(raceCreateDTO.getNombre())) {
            log.warn("Intento de crear raza duplicada: {}", raceCreateDTO.getNombre());
            throw new ResourceAlreadyExistsException("Ya existe una raza con el nombre: " + raceCreateDTO.getNombre());
        }

        // Verificar que la especie existe
        Specie specie = specieRepository.findById(raceCreateDTO.getIdEspecie())
                .orElseThrow(() -> {
                    log.error(SPECIE_NOT_FOUND, raceCreateDTO.getIdEspecie());
                    return new ResourceNotFoundException("Especie no encontrada con ID: " + raceCreateDTO.getIdEspecie());
                });

        // Crear la nueva raza
        Race race = Race.builder()
                .nombre(raceCreateDTO.getNombre())
                .specie(specie)
                .build();

        Race savedRace = raceRepository.save(race);
        log.info("Raza creada exitosamente con ID: {}", savedRace.getIdRaza());
        return mapToResponseDTO(savedRace);
    }

    @Override
    public RaceResponseDTO updateRace(Long idRaza, RaceCreateDTO raceCreateDTO) {
        log.info("Actualizando raza con ID: {}", idRaza);

        Race race = raceRepository.findById(idRaza)
                .orElseThrow(() -> {
                    log.error(RACE_NOT_FOUND, idRaza);
                    return new ResourceNotFoundException("Raza no encontrada con ID: " + idRaza);
                });

        // Verificar si el nuevo nombre ya existe en otra raza
        if (!race.getNombre().equals(raceCreateDTO.getNombre()) &&
                raceRepository.existsByNombre(raceCreateDTO.getNombre())) {
            log.warn("Intento de actualizar raza con nombre duplicado: {}", raceCreateDTO.getNombre());
            throw new ResourceAlreadyExistsException("Ya existe una raza con el nombre: " + raceCreateDTO.getNombre());
        }

        // Verificar que la especie existe si se está cambiando
        if (!race.getSpecie().getIdEspecie().equals(raceCreateDTO.getIdEspecie())) {
            Specie specie = specieRepository.findById(raceCreateDTO.getIdEspecie())
                    .orElseThrow(() -> {
                        log.error(SPECIE_NOT_FOUND, raceCreateDTO.getIdEspecie());
                        return new ResourceNotFoundException("Especie no encontrada con ID: " + raceCreateDTO.getIdEspecie());
                    });
            race.setSpecie(specie);
        }

        race.setNombre(raceCreateDTO.getNombre());

        Race updatedRace = raceRepository.save(race);
        log.info("Raza actualizada exitosamente con ID: {}", updatedRace.getIdRaza());
        return mapToResponseDTO(updatedRace);
    }

    @Override
    public void deleteRace(Long idRaza) {
        log.info("Eliminando raza con ID: {}", idRaza);

        if (!raceRepository.existsById(idRaza)) {
            log.error(RACE_NOT_FOUND, idRaza);
            throw new ResourceNotFoundException("Raza no encontrada con ID: " + idRaza);
        }

        raceRepository.deleteById(idRaza);
        log.info("Raza eliminada exitosamente con ID: {}", idRaza);
    }

    /**
     * Mapea una entidad Race a RaceResponseDTO
     *
     * @param race Entidad Race a mapear
     * @return RaceResponseDTO con los datos mapeados
     */
    private RaceResponseDTO mapToResponseDTO(Race race) {
        return RaceResponseDTO.builder()
                .idRaza(race.getIdRaza())
                .nombre(race.getNombre())
                .idEspecie(race.getSpecie().getIdEspecie())
                .nombreEspecie(race.getSpecie().getNombre())
                .build();
    }
}
