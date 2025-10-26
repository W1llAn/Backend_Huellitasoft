package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.consultation.ConsultationCreateDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationResponseDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationUpdateDTO;
import huellitassoft_web.huellitasoft.entity.Consultation;
import huellitassoft_web.huellitasoft.entity.MedicalHistory;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.ConsultationRepository;
import huellitassoft_web.huellitasoft.repository.MedicalHistoryRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final UserRepository userRepository;

    private static final String CONSULTATION_NOT_FOUND_LOG = "Consultation not found: {}";
    private static final String CONSULTATION_NOT_FOUND_MSG = "Consulta no encontrada con ID: ";
    private static final String MEDICAL_HISTORY_NOT_FOUND_MSG = "Historial clínico no encontrado con ID: ";
    private static final String VETERINARIAN_NOT_FOUND_MSG = "Veterinario no encontrado con ID: ";

    // ==================== CRUD OPERATIONS ====================

    @Override
    public ConsultationResponseDTO createConsultation(ConsultationCreateDTO consultationCreateDTO) {
        log.info("Creating new consultation for medical history: {}", consultationCreateDTO.getIdHistoria());

        // Validar que el historial clínico existe
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(consultationCreateDTO.getIdHistoria())
                .orElseThrow(() -> {
                    log.error("Medical history not found: {}", consultationCreateDTO.getIdHistoria());
                    return new ResourceNotFoundException(MEDICAL_HISTORY_NOT_FOUND_MSG + consultationCreateDTO.getIdHistoria());
                });

        // Validar que el veterinario existe
        User veterinarian = userRepository.findById(consultationCreateDTO.getIdVeterinario().longValue())
                .orElseThrow(() -> {
                    log.error("Veterinarian not found: {}", consultationCreateDTO.getIdVeterinario());
                    return new ResourceNotFoundException(VETERINARIAN_NOT_FOUND_MSG + consultationCreateDTO.getIdVeterinario());
                });

        // Crear nueva consulta
        Consultation consultation = Consultation.builder()
                .medicalHistory(medicalHistory)
                .fechaHora(consultationCreateDTO.getFechaHora())
                .motivo(consultationCreateDTO.getMotivo())
                .veterinarian(veterinarian)
                .diagnostico(consultationCreateDTO.getDiagnostico())
                .indicaciones(consultationCreateDTO.getIndicaciones())
                .build();

        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Consultation created successfully with ID: {}", savedConsultation.getIdConsulta());

        return mapToResponseDTO(savedConsultation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultationResponseDTO getConsultationById(Long idConsulta) {
        log.info("Fetching consultation: {}", idConsulta);

        Consultation consultation = consultationRepository.findById(idConsulta)
                .orElseThrow(() -> {
                    log.error(CONSULTATION_NOT_FOUND_LOG, idConsulta);
                    return new ResourceNotFoundException(CONSULTATION_NOT_FOUND_MSG + idConsulta);
                });

        return mapToResponseDTO(consultation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByHistoria(Long idHistoria) {
        log.info("Fetching consultations for medical history: {}", idHistoria);
        return consultationRepository.findByMedicalHistory_IdHistoria(idHistoria).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByMascota(Long idMascota) {
        log.info("Fetching consultations for pet: {}", idMascota);
        return consultationRepository.findByMascota_IdMascota(idMascota).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByVeterinario(Integer idVeterinario) {
        log.info("Fetching consultations for veterinarian: {}", idVeterinario);
        return consultationRepository.findByVeterinarian_IdUsuario(idVeterinario).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByFechaHora(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Fetching consultations between {} and {}", inicio, fin);
        return consultationRepository.findByFechaHoraBetween(inicio, fin).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public ConsultationResponseDTO updateConsultation(Long idConsulta, ConsultationUpdateDTO consultationUpdateDTO) {
        log.info("Updating consultation: {}", idConsulta);

        Consultation consultation = consultationRepository.findById(idConsulta)
                .orElseThrow(() -> {
                    log.error(CONSULTATION_NOT_FOUND_LOG, idConsulta);
                    return new ResourceNotFoundException(CONSULTATION_NOT_FOUND_MSG + idConsulta);
                });

        consultation.setMotivo(consultationUpdateDTO.getMotivo());
        consultation.setDiagnostico(consultationUpdateDTO.getDiagnostico());
        consultation.setIndicaciones(consultationUpdateDTO.getIndicaciones());

        Consultation updatedConsultation = consultationRepository.save(consultation);
        log.info("Consultation updated successfully: {}", idConsulta);

        return mapToResponseDTO(updatedConsultation);
    }

    @Override
    public void deleteConsultation(Long idConsulta) {
        log.info("Deleting consultation: {}", idConsulta);

        Consultation consultation = consultationRepository.findById(idConsulta)
                .orElseThrow(() -> {
                    log.error(CONSULTATION_NOT_FOUND_LOG, idConsulta);
                    return new ResourceNotFoundException(CONSULTATION_NOT_FOUND_MSG + idConsulta);
                });

        consultationRepository.delete(consultation);
        log.info("Consultation deleted successfully: {}", idConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getAllConsultations() {
        log.info("Fetching all consultations");
        return consultationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultationResponseDTO getLastConsultationByMascota(Long idMascota) {
        log.info("Fetching last consultation for pet: {}", idMascota);

        Consultation consultation = consultationRepository.findLastConsultationByMascota(idMascota)
                .orElseThrow(() -> {
                    log.error("No consultations found for pet: {}", idMascota);
                    return new ResourceNotFoundException("No hay consultas registradas para la mascota: " + idMascota);
                });

        return mapToResponseDTO(consultation);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Mapea una entidad Consultation a ConsultationResponseDTO
     */
    private ConsultationResponseDTO mapToResponseDTO(Consultation consultation) {
        long totalTratamientos = consultation.getTratamientos() != null ? consultation.getTratamientos().size() : 0;

        return ConsultationResponseDTO.builder()
                .idConsulta(consultation.getIdConsulta())
                .idHistoria(consultation.getMedicalHistory().getIdHistoria())
                .fechaHora(consultation.getFechaHora())
                .motivo(consultation.getMotivo())
                .idVeterinario(consultation.getVeterinarian().getIdUsuario().intValue())
                .nombreVeterinario(consultation.getVeterinarian().getUsername())
                .diagnostico(consultation.getDiagnostico())
                .indicaciones(consultation.getIndicaciones())
                .totalTratamientos((int) totalTratamientos)
                .build();
    }
}
