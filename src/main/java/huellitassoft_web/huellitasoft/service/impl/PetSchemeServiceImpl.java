package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeResponseDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeUpdateStateDTO;
import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.entity.PetScheme;
import huellitassoft_web.huellitasoft.entity.VaccinationScheme;
import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.PetSchemeRepository;
import huellitassoft_web.huellitasoft.repository.VaccinationSchemeRepository;
import huellitassoft_web.huellitasoft.service.PetSchemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PetSchemeServiceImpl implements PetSchemeService {
    private static final String PETSCHEME_NOT_FOUND = "Relación mascota-esquema no encontrada con ID: {}";
    private static final String PET_NOT_FOUND = "Mascota no encontrada con ID: {}";
    private static final String SCHEME_NOT_FOUND = "Esquema de vacunación no encontrado con ID: {}";

    private final PetRepository petRepo;
    private final VaccinationSchemeRepository schemeRepo;
    private final PetSchemeRepository petSchemeRepo;

    @Override
    public PetSchemeResponseDTO assignSchemeToPet(PetSchemeCreateDTO dto) {
        log.info("Asignando esquema {} a mascota {}", dto.getIdEsquema(), dto.getIdMascota());

        Pet pet = petRepo.findById(dto.getIdMascota()).orElseThrow(() -> {
            log.error(PET_NOT_FOUND, dto.getIdMascota());
            return new ResourceNotFoundException("Mascota no encontrada: " + dto.getIdMascota());
        });

        VaccinationScheme scheme = schemeRepo.findById(dto.getIdEsquema()).orElseThrow(() -> {
            log.error(SCHEME_NOT_FOUND, dto.getIdEsquema());
            return new ResourceNotFoundException("Esquema no encontrado: " + dto.getIdEsquema());
        });

        if (petSchemeRepo.existsByPet_IdMascotaAndScheme_IdEsquema(pet.getIdMascota(), scheme.getIdEsquema())) {
            log.warn("Intento de asignación duplicada (mascota={}, esquema={})",
                    pet.getIdMascota(), scheme.getIdEsquema());
            throw new ResourceAlreadyExistsException("La mascota ya tiene asignado este esquema");
        }

        PetScheme ps = PetScheme.builder()
                .pet(pet)
                .scheme(scheme)
                .estado(dto.getEstado() != null ? dto.getEstado() : PetSchemeState.ACTIVO)
                .build();

        PetScheme saved = petSchemeRepo.save(ps);
        log.info("Asignación creada con ID {}", saved.getIdMascotaEsquema());
        return toDTO(saved);
    }

    @Override
    public PetSchemeResponseDTO updateSchemeState(Long idMascotaEsquema, PetSchemeUpdateStateDTO dto) {
        log.info("Actualizando estado de relación mascota-esquema ID {} a {}", idMascotaEsquema, dto.getEstado());

        PetScheme ps = petSchemeRepo.findById(idMascotaEsquema).orElseThrow(() -> {
            log.error(PETSCHEME_NOT_FOUND, idMascotaEsquema);
            return new ResourceNotFoundException("Relación mascota-esquema no encontrada: " + idMascotaEsquema);
        });

        ps.setEstado(dto.getEstado());
        PetScheme updated = petSchemeRepo.save(ps);
        log.info("Estado actualizado correctamente (ID={}, nuevoEstado={})",
                updated.getIdMascotaEsquema(), updated.getEstado());
        return toDTO(updated);
    }

    @Override
    public void unassignScheme(Long idMascotaEsquema) {
        log.info("Eliminando relación mascota-esquema ID {}", idMascotaEsquema);

        if (!petSchemeRepo.existsById(idMascotaEsquema)) {
            log.error(PETSCHEME_NOT_FOUND, idMascotaEsquema);
            throw new ResourceNotFoundException("Relación mascota-esquema no encontrada: " + idMascotaEsquema);
        }

        petSchemeRepo.deleteById(idMascotaEsquema);
        log.info("Relación mascota-esquema eliminada exitosamente (ID={})", idMascotaEsquema);
    }

    @Override
    public PetSchemeResponseDTO getPetSchemeById(Long idMascotaEsquema) {
        log.info("Obteniendo relación mascota-esquema por ID {}", idMascotaEsquema);

        PetScheme ps = petSchemeRepo.findById(idMascotaEsquema).orElseThrow(() -> {
            log.error(PETSCHEME_NOT_FOUND, idMascotaEsquema);
            return new ResourceNotFoundException("Relación mascota-esquema no encontrada: " + idMascotaEsquema);
        });

        return toDTO(ps);
    }

    @Override
    public List<PetSchemeResponseDTO> getPetSchemes(Long idMascota) {
        log.info("Listando todas las relaciones mascota-esquema de la mascota {}", idMascota);
        return petSchemeRepo.findByPet_IdMascota(idMascota).stream().map(this::toDTO).toList();
    }

    @Override
    public List<PetSchemeResponseDTO> getActivePetSchemes(Long idMascota) {
        log.info("Listando relaciones ACTIVO de la mascota {}", idMascota);
        return petSchemeRepo.findByPet_IdMascotaAndEstado(idMascota, PetSchemeState.ACTIVO)
                .stream().map(this::toDTO).toList();
    }

    private PetSchemeResponseDTO toDTO(PetScheme ps) {
        return PetSchemeResponseDTO.builder()
                .idMascotaEsquema(ps.getIdMascotaEsquema())
                .idMascota(ps.getPet().getIdMascota())
                .nombreMascota(ps.getPet().getNombre())
                .idEsquema(ps.getScheme().getIdEsquema())
                .idVacuna(ps.getScheme().getVaccine().getIdVacuna())
                .nombreVacuna(ps.getScheme().getVaccine().getNombre())
                .dosisNumero(ps.getScheme().getDosisNumero())
                .edadSemanas(ps.getScheme().getEdadSemanas())
                .estado(ps.getEstado())
                .build();
    }
}
