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
    private final MedicalHistoryRepository medicalHistoryRepository;

    private static final String TREATMENT_NOT_FOUND_LOG = "Treatment not found: {}";
    private static final String TREATMENT_NOT_FOUND_MSG = "Tratamiento no encontrado con ID: ";
    private static final String CONSULTATION_NOT_FOUND_MSG = "Consulta no encontrada con ID: ";
    private static final String PET_NOT_FOUND_MSG = "Mascota no encontrada con ID: ";
    private static final String MEDICAL_HISTORY_NOT_FOUND_MSG = "Historial clínico no encontrado con ID: ";

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
        Pet mascota = petRepository.findById(treatmentCreateDTO.getIdMascota())
                .orElseThrow(() -> {
                    log.error("Pet not found: {}", treatmentCreateDTO.getIdMascota());
                    return new ResourceNotFoundException(PET_NOT_FOUND_MSG + treatmentCreateDTO.getIdMascota());
                });



        // Crear nuevo tratamiento
        Treatment treatment = Treatment.builder()
                .consultation(consultation)
                .mascota(mascota)
                .duracionDias(treatmentCreateDTO.getDuracionDias())
                .observaciones(treatmentCreateDTO.getObservaciones())
                .estado(treatmentCreateDTO.getEstado())
                .build();

        Treatment savedTreatment = treatmentRepository.save(treatment);
        log.info("Treatment created successfully with ID: {}", savedTreatment.getIdTratamiento());

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
        log.info("Fetching treatments for pet: {}", idMascota);
        return treatmentRepository.findByMascota_IdMascota(idMascota).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponseDTO> getActiveTreatmentsByMascota(Long idMascota) {
        log.info("Fetching active treatments for pet: {}", idMascota);
        return treatmentRepository.findActiveByMascota(idMascota).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public TreatmentResponseDTO updateTreatment(Long idTratamiento, TreatmentUpdateDTO treatmentUpdateDTO) {
        log.info("Updating treatment: {}", idTratamiento);

        Treatment treatment = treatmentRepository.findById(idTratamiento)
                .orElseThrow(() -> {
                    log.error(TREATMENT_NOT_FOUND_LOG, idTratamiento);
                    return new ResourceNotFoundException(TREATMENT_NOT_FOUND_MSG + idTratamiento);
                });

        treatment.setDuracionDias(treatmentUpdateDTO.getDuracionDias());
        treatment.setObservaciones(treatmentUpdateDTO.getObservaciones());
        treatment.setEstado(treatmentUpdateDTO.getEstado());

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

        treatment.setEstado(nuevoEstado);
        Treatment updatedTreatment = treatmentRepository.save(treatment);
        log.info("Treatment status updated successfully: {}", idTratamiento);

        return mapToResponseDTO(updatedTreatment);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Mapea una entidad Treatment a TreatmentResponseDTO
     */
    private TreatmentResponseDTO mapToResponseDTO(Treatment treatment) {
        Pet mascota = treatment.getMascota();
        return TreatmentResponseDTO.builder()
                .idTratamiento(treatment.getIdTratamiento())
                .idConsulta(treatment.getConsultation().getIdConsulta())
                .idMascota(mascota.getIdMascota())
                .nombreMascota(mascota.getNombre())
                .duracionDias(treatment.getDuracionDias())
                .observaciones(treatment.getObservaciones())
                .estado(treatment.getEstado())
                .build();
    }
}
