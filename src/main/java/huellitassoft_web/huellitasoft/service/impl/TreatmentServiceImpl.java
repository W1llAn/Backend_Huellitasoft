package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.treatment.TreatmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentUpdateDTO;
import huellitassoft_web.huellitasoft.entity.Consultation;
import huellitassoft_web.huellitasoft.entity.MedicalHistory;
import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.entity.Treatment;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.ConsultationRepository;
import huellitassoft_web.huellitasoft.repository.MedicalHistoryRepository;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.TreatmentRepository;
import huellitassoft_web.huellitasoft.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PetRepository petRepository;

    private static final String TREATMENT_NOT_FOUND_LOG = "Treatment not found: {}";
    private static final String TREATMENT_NOT_FOUND_MSG = "Tratamiento no encontrado con ID: ";
    private static final String CONSULTATION_NOT_FOUND_MSG = "Consulta no encontrada con ID: ";
    private static final String PET_NOT_FOUND_MSG = "Mascota no encontrada con ID: ";

    // ==================== CRUD OPERATIONS ====================

    @Override
    public TreatmentResponseDTO createTreatment(TreatmentCreateDTO treatmentCreateDTO) {
        log.info("Creating new treatment for consultation: {}", treatmentCreateDTO.getIdConsulta());

        // Validar que la consulta existe
        Consultation consultation = consultationRepository.findById(treatmentCreateDTO.getIdConsulta())
                .orElseThrow(() -> {
                    log.error("Consultation not found: {}", treatmentCreateDTO.getIdConsulta());
                    return new ResourceNotFoundException(CONSULTATION_NOT_FOUND_MSG + treatmentCreateDTO.getIdConsulta());
                });

        // Validar que la mascota existe
        Pet pet = petRepository.findById(treatmentCreateDTO.getIdMascota())
                .orElseThrow(() -> {
                    log.error("Pet not found: {}", treatmentCreateDTO.getIdMascota());
                    return new ResourceNotFoundException(PET_NOT_FOUND_MSG + treatmentCreateDTO.getIdMascota());
                });

        // Crear nuevo tratamiento con todos los atributos
        Treatment treatment = Treatment.builder()
                .consultation(consultation)
                .pet(pet)
                .description(treatmentCreateDTO.getDescription())
                .medication(treatmentCreateDTO.getMedication())
                .dosage(treatmentCreateDTO.getDosage())
                .frequency(treatmentCreateDTO.getFrequency())
                .durationDays(treatmentCreateDTO.getDurationDays())
                .observations(treatmentCreateDTO.getObservations())
                .status(treatmentCreateDTO.getStatus() != null ? treatmentCreateDTO.getStatus() : true)
                .build();

        Treatment savedTreatment = treatmentRepository.save(treatment);
        log.info("Treatment created successfully with ID: {}", savedTreatment.getId());

        return mapToResponseDTO(savedTreatment);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponseDTO getTreatmentById(Long idTratamiento) {
        log.info("Fetching treatment: {}", idTratamiento);

        Treatment treatment = treatmentRepository.findById(idTratamiento)
                .orElseThrow(() -> {
                    log.error(TREATMENT_NOT_FOUND_LOG, idTratamiento);
                    return new ResourceNotFoundException(TREATMENT_NOT_FOUND_MSG + idTratamiento);
                });

        return mapToResponseDTO(treatment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponseDTO> getTreatmentsByConsultation(Long idConsulta) {
        log.info("Fetching treatments for consultation: {}", idConsulta);
        return treatmentRepository.findByConsultation_IdConsulta(idConsulta).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponseDTO> getTreatmentsByMascota(Long idMascota) {
        log.info("Obteniendo tratamientos de mascota: {}", idMascota);
        return treatmentRepository.findByPet_IdMascota(idMascota).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }
    /*findByPet_IdMascota*/

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponseDTO> getActiveTreatmentsByMascota(Long idMascota) {
        log.info("Obteniendo tratamientos activos de mascota: {}", idMascota);
        return treatmentRepository.findActiveByMascota(idMascota).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public TreatmentResponseDTO updateTreatment(Long idTratamiento, TreatmentUpdateDTO treatmentUpdateDTO) {
        log.info("Actualizando tratamientos: {}", idTratamiento);

        Treatment treatment = treatmentRepository.findById(idTratamiento)
                .orElseThrow(() -> {
                    log.error(TREATMENT_NOT_FOUND_LOG, idTratamiento);
                    return new ResourceNotFoundException(TREATMENT_NOT_FOUND_MSG + idTratamiento);
                });

        // Actualizar todos los campos
        if (treatmentUpdateDTO.getDescription() != null) {
            treatment.setDescription(treatmentUpdateDTO.getDescription());
        }
        if (treatmentUpdateDTO.getMedication() != null) {
            treatment.setMedication(treatmentUpdateDTO.getMedication());
        }
        if (treatmentUpdateDTO.getDosage() != null) {
            treatment.setDosage(treatmentUpdateDTO.getDosage());
        }
        if (treatmentUpdateDTO.getFrequency() != null) {
            treatment.setFrequency(treatmentUpdateDTO.getFrequency());
        }
        if (treatmentUpdateDTO.getDurationDays() != null) {
            treatment.setDurationDays(treatmentUpdateDTO.getDurationDays());
        }
        if (treatmentUpdateDTO.getObservations() != null) {
            treatment.setObservations(treatmentUpdateDTO.getObservations());
        }
        if (treatmentUpdateDTO.getStatus() != null) {
            treatment.setStatus(treatmentUpdateDTO.getStatus());
        }

        Treatment updatedTreatment = treatmentRepository.save(treatment);
        log.info("Treatment updated successfully: {}", idTratamiento);

        return mapToResponseDTO(updatedTreatment);
    }

    @Override
    public void deleteTreatment(Long idTratamiento) {
        log.info("Deleting treatment: {}", idTratamiento);

        Treatment treatment = treatmentRepository.findById(idTratamiento)
                .orElseThrow(() -> {
                    log.error(TREATMENT_NOT_FOUND_LOG, idTratamiento);
                    return new ResourceNotFoundException(TREATMENT_NOT_FOUND_MSG + idTratamiento);
                });

        treatmentRepository.delete(treatment);
        log.info("Treatment deleted successfully: {}", idTratamiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponseDTO> getAllTreatments() {
        log.info("Fetching all treatments");
        return treatmentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public TreatmentResponseDTO updateTreatmentStatus(Long idTratamiento, Boolean nuevoEstado) {
        log.info("Updating treatment status. ID: {}, New status: {}", idTratamiento, nuevoEstado);

        Treatment treatment = treatmentRepository.findById(idTratamiento)
                .orElseThrow(() -> {
                    log.error(TREATMENT_NOT_FOUND_LOG, idTratamiento);
                    return new ResourceNotFoundException(TREATMENT_NOT_FOUND_MSG + idTratamiento);
                });

        treatment.setStatus(nuevoEstado);
        Treatment updatedTreatment = treatmentRepository.save(treatment);
        log.info("Treatment status updated successfully: {}", idTratamiento);

        return mapToResponseDTO(updatedTreatment);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Mapea una entidad Treatment a TreatmentResponseDTO
     */
    private TreatmentResponseDTO mapToResponseDTO(Treatment treatment) {
        Pet pet = treatment.getPet();
        Consultation consultation = treatment.getConsultation();

        return TreatmentResponseDTO.builder()
                .idTratamiento(treatment.getId())
                .idConsulta(consultation.getIdConsulta())
                .idMascota(pet.getIdMascota())
                .nombreMascota(pet.getNombre())
                .description(treatment.getDescription())
                .medication(treatment.getMedication())
                .dosage(treatment.getDosage())
                .frequency(treatment.getFrequency())
                .durationDays(treatment.getDurationDays())
                .observations(treatment.getObservations())
                .status(treatment.getStatus())
                .build();
    }
}
