package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.Schedule.ScheduleRequestDTO;
import huellitassoft_web.huellitasoft.dto.Schedule.ScheduleResponseDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryRequestDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryResponseDTO;
import huellitassoft_web.huellitasoft.entity.Subsidiary;
import huellitassoft_web.huellitasoft.entity.SubsidiarySchedule;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.SubsidiaryRepository;
import huellitassoft_web.huellitasoft.repository.SubsidiaryScheduleRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.SubsidiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubsidiaryServiceImpl implements SubsidiaryService {

    private final SubsidiaryRepository subsidiaryRepository;
    private final SubsidiaryScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubsidiaryResponseDTO createSubsidiary(SubsidiaryRequestDTO requestDTO) {
        if (subsidiaryRepository.existsByName(requestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Ya existe una sucursal con el nombre: " + requestDTO.getName());
        }

        Subsidiary subsidiary = mapToEntity(requestDTO);
        subsidiary.setSchedules(new ArrayList<>());

        Subsidiary savedSubsidiary = subsidiaryRepository.save(subsidiary);

        if (requestDTO.getSchedules() != null && !requestDTO.getSchedules().isEmpty()) {
            List<SubsidiarySchedule> schedules = requestDTO.getSchedules().stream()
                    .map(scheduleDTO -> mapScheduleToEntity(scheduleDTO, savedSubsidiary))
                    .toList();
            schedules = scheduleRepository.saveAll(schedules);
            savedSubsidiary.setSchedules(schedules);
        }

        return mapToResponseDTO(savedSubsidiary);
    }

    @Override
    @Transactional(readOnly = true)
    public SubsidiaryResponseDTO getSubsidiaryById(Long id) {
        Subsidiary subsidiary = findSubsidiaryById(id);
        return mapToResponseDTO(subsidiary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubsidiaryResponseDTO> getAllSubsidiaries() {
        return subsidiaryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubsidiaryResponseDTO> getSubsidiariesByState(SubsidiaryState state) {
        return subsidiaryRepository.findByState(state).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public SubsidiaryResponseDTO updateSubsidiary(Long id, SubsidiaryRequestDTO requestDTO) {
        Subsidiary subsidiary = findSubsidiaryById(id);

        if (!subsidiary.getName().equals(requestDTO.getName()) &&
                subsidiaryRepository.existsByName(requestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Ya existe una sucursal con el nombre: " + requestDTO.getName());
        }

        updateSubsidiaryFields(subsidiary, requestDTO);

        if (requestDTO.getSchedules() != null) {
            updateSchedules(subsidiary, requestDTO.getSchedules());
        }

        return mapToResponseDTO(subsidiaryRepository.save(subsidiary));
    }

    private void updateSchedules(Subsidiary subsidiary, List<ScheduleRequestDTO> scheduleDTOs) {
        List<SubsidiarySchedule> existingSchedules = subsidiary.getSchedules();

        var existingScheduleMap = existingSchedules.stream()
                .collect(Collectors.toMap(
                        SubsidiarySchedule::getDayOfWeek,
                        schedule -> schedule
                ));

        existingSchedules.clear();

        for (ScheduleRequestDTO scheduleDTO : scheduleDTOs) {
            SubsidiarySchedule schedule = existingScheduleMap.get(scheduleDTO.getDayOfWeek());

            if (schedule != null) {
                updateScheduleFields(schedule, scheduleDTO);
            } else {
                schedule = mapScheduleToEntity(scheduleDTO, subsidiary);
            }

            existingSchedules.add(schedule);
        }
    }

    private void updateScheduleFields(SubsidiarySchedule schedule, ScheduleRequestDTO dto) {
        schedule.setShiftType(dto.getShiftType());
        schedule.setStartTime(dto.getStartTime());
        schedule.setLunchStartTime(dto.getLunchStartTime());
        schedule.setLunchEndTime(dto.getLunchEndTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setIsOpen(dto.getIsOpen());
    }

    @Override
    @Transactional
    public void deleteSubsidiary(Long id) {
        if (!subsidiaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);
        }
        subsidiaryRepository.deleteById(id);
    }

    private Subsidiary findSubsidiaryById(Long id) {
        return subsidiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
    }

    private Subsidiary mapToEntity(SubsidiaryRequestDTO dto) {
        // Validar que el usuario existe
        User manager = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        Subsidiary subsidiary = new Subsidiary();
        subsidiary.setName(dto.getName());
        subsidiary.setAddress(dto.getAddress());
        subsidiary.setState(dto.getState());
        subsidiary.setManager(manager);
        return subsidiary;
    }

    private void updateSubsidiaryFields(Subsidiary subsidiary, SubsidiaryRequestDTO dto) {
        subsidiary.setName(dto.getName());
        subsidiary.setAddress(dto.getAddress());
        subsidiary.setState(dto.getState());

        // Actualizar usuario si se proporciona
        if (dto.getIdUsuario() != null) {
            User manager = userRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            subsidiary.setManager(manager);
        }
    }

    private SubsidiarySchedule mapScheduleToEntity(ScheduleRequestDTO dto, Subsidiary subsidiary) {
        SubsidiarySchedule schedule = new SubsidiarySchedule();
        schedule.setSubsidiary(subsidiary);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setShiftType(dto.getShiftType());
        schedule.setStartTime(dto.getStartTime());
        schedule.setLunchStartTime(dto.getLunchStartTime());
        schedule.setLunchEndTime(dto.getLunchEndTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setIsOpen(dto.getIsOpen());
        return schedule;
    }

    private SubsidiaryResponseDTO mapToResponseDTO(Subsidiary subsidiary) {
        SubsidiaryResponseDTO dto = new SubsidiaryResponseDTO();
        dto.setIdSubsidiary(subsidiary.getIdSubsidiary());
        dto.setName(subsidiary.getName());
        dto.setAddress(subsidiary.getAddress());
        dto.setState(subsidiary.getState());

        // Incluir información del usuario gestor
        if (subsidiary.getManager() != null) {
            dto.setIdUsuario(subsidiary.getManager().getIdUsuario());
            dto.setUsuarioUsername(subsidiary.getManager().getUsername());
            dto.setUsuarioEmail(subsidiary.getManager().getEmail());
        }

        if (subsidiary.getSchedules() != null) {
            List<ScheduleResponseDTO> schedules = subsidiary.getSchedules().stream()
                    .map(this::mapScheduleToResponseDTO)
                    .toList();
            dto.setSchedules(schedules);
        }

        return dto;
    }

    private ScheduleResponseDTO mapScheduleToResponseDTO(SubsidiarySchedule schedule) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setIdSchedule(schedule.getIdSchedule());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setShiftType(schedule.getShiftType());
        dto.setStartTime(schedule.getStartTime());
        dto.setLunchStartTime(schedule.getLunchStartTime());
        dto.setLunchEndTime(schedule.getLunchEndTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setIsOpen(schedule.getIsOpen());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubsidiaryResponseDTO> getSubsidiariesByManager(Long idUsuario) {
        // Validar que el usuario existe
        if (!userRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }

        return subsidiaryRepository.findByManager_IdUsuario(idUsuario).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubsidiaryResponseDTO> getSubsidiariesByManagerAndState(Long idUsuario, SubsidiaryState state) {
        // Validar que el usuario existe
        if (!userRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }

        return subsidiaryRepository.findByManager_IdUsuarioAndState(idUsuario, state).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }
}