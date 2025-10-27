package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationCreateDTO;
import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationResponseDTO;
import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.entity.PetVaccination;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.entity.Vaccine;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.PetVaccinationRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.repository.VaccineRepository;
import huellitassoft_web.huellitasoft.service.PetVaccinationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PetVaccinationServiceImpl implements PetVaccinationService {

    private static final String PET_NOT_FOUND = "Mascota no encontrada con ID: {}";
    private static final String VACCINE_NOT_FOUND = "Vacuna no encontrada con ID: {}";
    private static final String USER_NOT_FOUND = "Usuario no encontrado: {}";
    private static final String USER_NOT_VET = "El usuario autenticado no tiene rol de VETERINARIO";


    private final PetRepository petRepository;
    private final VaccineRepository vaccineRepository;
    private final UserRepository userRepository;
    private final PetVaccinationRepository petVaccinationRepository;


    @Override
    public PetVaccinationResponseDTO create(PetVaccinationCreateDTO dto, String authenticatedUsername) {
        log.info("Registrando vacunación para mascota ID {} con vacuna ID {}", dto.getIdMascota(), dto.getIdVacuna());

        Pet pet = petRepository.findById(dto.getIdMascota()).orElseThrow(() -> {
            log.error(PET_NOT_FOUND, dto.getIdMascota());
            return new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getIdMascota());
        });

        Vaccine vaccine = vaccineRepository.findById(dto.getIdVacuna()).orElseThrow(() -> {
            log.error(VACCINE_NOT_FOUND, dto.getIdVacuna());
            return new ResourceNotFoundException("Vacuna no encontrada con ID: " + dto.getIdVacuna());
        });

        // Extraer el ID numérico del formato "auth0|7"
        int userId = Math.toIntExact(Long.valueOf(extractUserIdFromAuth0Subject(authenticatedUsername)));

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error(USER_NOT_FOUND, authenticatedUsername);
            return new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        });

        // Validar rol de veterinario
        if (user.getRol() == null || !user.getRol().name().contains("VETERINARIO")) {
            log.warn(USER_NOT_VET);
            throw new IllegalStateException(USER_NOT_VET);
        }


        PetVaccination pv = PetVaccination.builder()
                .pet(pet)
                .vaccine(vaccine)
                .user(user)
                .fechaAplicada(LocalDateTime.now()) // backend controla fecha/hora
                .build();

        PetVaccination saved = petVaccinationRepository.save(pv);
        log.info("Vacunación registrada con ID {}", saved.getIdVacunacion());

        return toDTO(saved);
    }

    // Método helper para extraer el ID
    private Long extractUserIdFromAuth0Subject(String auth0Subject) {
        // Formato esperado: "auth0|7"
        if (auth0Subject != null && auth0Subject.startsWith("auth0|")) {
            try {
                return Long.parseLong(auth0Subject.substring(6));
            } catch (NumberFormatException e) {
                log.error("No se pudo extraer ID numérico de: {}", auth0Subject);
                throw new IllegalArgumentException("Formato de subject inválido: " + auth0Subject);
            }
        }
        throw new IllegalArgumentException("Subject no tiene formato auth0|ID: " + auth0Subject);
    }

    @Override
    public void delete(Long idVacunacion) {
        log.info("Eliminando vacunación ID {}", idVacunacion);
        if (!petVaccinationRepository.existsById(idVacunacion)) {
            log.error("Vacunación no encontrada con ID: {}", idVacunacion);
            throw new ResourceNotFoundException("Vacunación no encontrada con ID: " + idVacunacion);
        }
        petVaccinationRepository.deleteById(idVacunacion);
        log.info("Vacunación eliminada ID {}", idVacunacion);
    }

    @Override
    @Transactional(readOnly = true)
    public PetVaccinationResponseDTO getById(Long idVacunacion) {
        PetVaccination pv = petVaccinationRepository.findById(idVacunacion)
                .orElseThrow(() -> new ResourceNotFoundException("Vacunación no encontrada con ID: " + idVacunacion));
        return toDTO(pv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetVaccinationResponseDTO> getByPet(Long idMascota) {
        return petVaccinationRepository.findByPet_IdMascota(idMascota).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetVaccinationResponseDTO> getByVaccine(Long idVacuna) {
        return petVaccinationRepository.findByVaccine_IdVacuna(idVacuna).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetVaccinationResponseDTO> getByVeterinarian(Long idUsuarioVeterinario) {
        return petVaccinationRepository.findByUser_IdUsuario(idUsuarioVeterinario).stream().map(this::toDTO).toList();
    }

    private PetVaccinationResponseDTO toDTO(PetVaccination pv) {
        return PetVaccinationResponseDTO.builder()
                .idVacunacion(pv.getIdVacunacion())
                .idMascota(pv.getPet().getIdMascota())
                .nombreMascota(pv.getPet().getNombre())
                .idVacuna(pv.getVaccine().getIdVacuna())
                .nombreVacuna(pv.getVaccine().getNombre())
                .fechaAplicada(pv.getFechaAplicada())
                .idUsuario(Long.valueOf(pv.getUser().getIdUsuario()))
                .nombreUsuario(pv.getUser().getUsername())
                .build();
    }
}
