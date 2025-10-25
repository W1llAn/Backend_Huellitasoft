package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryCreateDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryDetailDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryResponseDTO;
import huellitassoft_web.huellitasoft.entity.MedicalHistory;
import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.MedicalHistoryRepository;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PetRepository petRepository;

    private static final String MEDICAL_HISTORY_NOT_FOUND_LOG = "Medical history not found: {}";
    private static final String MEDICAL_HISTORY_NOT_FOUND_MSG = "Historial clínico no encontrado con ID: ";

    // ==================== CRUD OPERATIONS ====================

    @Override
    public MedicalHistoryResponseDTO createMedicalHistory(MedicalHistoryCreateDTO medicalHistoryCreateDTO) {
        log.info("Creating new medical history for pet: {}", medicalHistoryCreateDTO.getIdMascota());

        // Validar que la mascota existe
        Pet mascota = petRepository.findById(medicalHistoryCreateDTO.getIdMascota())
                .orElseThrow(() -> {
                    log.error("Pet not found: {}", medicalHistoryCreateDTO.getIdMascota());
                    return new ResourceNotFoundException("Mascota no encontrada con ID: " + medicalHistoryCreateDTO.getIdMascota());
                });

        // Validar que no exista un historial activo para la mascota
        if (medicalHistoryRepository.existsByMascota_IdMascota(medicalHistoryCreateDTO.getIdMascota())) {
            log.warn("Medical history already exists for pet: {}", medicalHistoryCreateDTO.getIdMascota());
            throw new IllegalArgumentException("Ya existe un historial clínico para esta mascota");
        }

        // Crear nuevo historial clínico
        MedicalHistory medicalHistory = MedicalHistory.builder()
                .mascota(mascota)
                .numero(medicalHistoryCreateDTO.getNumero())
                .estado(medicalHistoryCreateDTO.getEstado())
                .build();

        MedicalHistory savedMedicalHistory = medicalHistoryRepository.save(medicalHistory);
        log.info("Medical history created successfully with ID: {}", savedMedicalHistory.getIdHistoria());

        return mapToResponseDTO(savedMedicalHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalHistoryDetailDTO getMedicalHistoryDetail(Long idHistoria) {
        log.info("Fetching detailed medical history: {}", idHistoria);

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(idHistoria)
                .orElseThrow(() -> {
                    log.error(MEDICAL_HISTORY_NOT_FOUND_LOG, idHistoria);
                    return new ResourceNotFoundException(MEDICAL_HISTORY_NOT_FOUND_MSG + idHistoria);
                });

        Pet mascota = medicalHistory.getMascota();

        return MedicalHistoryDetailDTO.builder()
                .idHistoria(medicalHistory.getIdHistoria())
                .idMascota(mascota.getIdMascota())
                .nombreMascota(mascota.getNombre())
                .razaMascota(mascota.getRaza().getNombre())
                .numero(medicalHistory.getNumero())
                .estado(medicalHistory.getEstado())
                .totalConsultas(medicalHistory.getConsultas().size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalHistoryResponseDTO getMedicalHistoryByMascota(Long idMascota) {
        log.info("Fetching medical history for pet: {}", idMascota);

        MedicalHistory medicalHistory = medicalHistoryRepository.findByMascota_IdMascota(idMascota)
                .orElseThrow(() -> {
                    log.error("Medical history not found for pet: {}", idMascota);
                    return new ResourceNotFoundException("Historial clínico no encontrado para la mascota: " + idMascota);
                });

        return mapToResponseDTO(medicalHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalHistoryResponseDTO> getAllMedicalHistoriesByMascota(Long idMascota) {
        log.info("Fetching all medical histories for pet: {}", idMascota);
        return medicalHistoryRepository.findAllByMascota_IdMascota(idMascota).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalHistoryResponseDTO> getMedicalHistoriesByEstado(MedicalHistoryState estado) {
        log.info("Fetching medical histories by state: {}", estado);
        return medicalHistoryRepository.findByEstado(estado).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalHistoryResponseDTO getMedicalHistoryById(Long idHistoria) {
        log.info("Fetching medical history: {}", idHistoria);

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(idHistoria)
                .orElseThrow(() -> {
                    log.error(MEDICAL_HISTORY_NOT_FOUND_LOG, idHistoria);
                    return new ResourceNotFoundException(MEDICAL_HISTORY_NOT_FOUND_MSG + idHistoria);
                });

        return mapToResponseDTO(medicalHistory);
    }

    @Override
    public MedicalHistoryResponseDTO updateEstadoMedicalHistory(Long idHistoria, MedicalHistoryState nuevoEstado) {
        log.info("Updating medical history status. ID: {}, New status: {}", idHistoria, nuevoEstado);

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(idHistoria)
                .orElseThrow(() -> {
                    log.error(MEDICAL_HISTORY_NOT_FOUND_LOG, idHistoria);
                    return new ResourceNotFoundException(MEDICAL_HISTORY_NOT_FOUND_MSG + idHistoria);
                });

        medicalHistory.setEstado(nuevoEstado);
        MedicalHistory updatedMedicalHistory = medicalHistoryRepository.save(medicalHistory);
        log.info("Medical history updated successfully: {}", idHistoria);

        return mapToResponseDTO(updatedMedicalHistory);
    }

    @Override
    public void deleteMedicalHistory(Long idHistoria) {
        log.info("Deleting medical history: {}", idHistoria);

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(idHistoria)
                .orElseThrow(() -> {
                    log.error(MEDICAL_HISTORY_NOT_FOUND_LOG, idHistoria);
                    return new ResourceNotFoundException(MEDICAL_HISTORY_NOT_FOUND_MSG + idHistoria);
                });

        // Eliminación lógica
        medicalHistory.setEstado(MedicalHistoryState.ELIMINADO);
        medicalHistoryRepository.save(medicalHistory);
        log.info("Medical history deleted successfully (logical deletion): {}", idHistoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalHistoryResponseDTO> getAllMedicalHistories() {
        log.info("Fetching all medical histories");
        return medicalHistoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Mapea una entidad MedicalHistory a MedicalHistoryResponseDTO
     */
    private MedicalHistoryResponseDTO mapToResponseDTO(MedicalHistory medicalHistory) {
        Pet mascota = medicalHistory.getMascota();
        return MedicalHistoryResponseDTO.builder()
                .idHistoria(medicalHistory.getIdHistoria())
                .idMascota(mascota.getIdMascota())
                .nombreMascota(mascota.getNombre())
                .numero(medicalHistory.getNumero())
                .estado(medicalHistory.getEstado())
                .build();
    }
}
