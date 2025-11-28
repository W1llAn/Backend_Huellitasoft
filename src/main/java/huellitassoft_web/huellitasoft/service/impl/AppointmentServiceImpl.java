package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationRequestDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentUpdateDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.enums.EstadoCita;
import huellitassoft_web.huellitasoft.enums.NotificationTitle;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.*;
import huellitassoft_web.huellitasoft.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final NotificationService notificationService;

    @Override
    public AppointmentResponseDTO create(AppointmentCreateDTO dto) {
        // Validar existencia de cliente
        Client cliente = clientRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));

        // Validar existencia de mascota
        Pet mascota = petRepository.findById(dto.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getIdMascota()));

        // Validar existencia de usuario (solo veterinario o administrador veterinaria)
        User usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
        if (!mascota.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            throw new IllegalArgumentException(
                    String.format("La mascota con ID %d no pertenece al cliente con ID %d",
                            dto.getIdMascota(), dto.getIdCliente()));
        }
        if (!(usuario.getRol() == UserRol.ROLE_VETERINARIO ||
                usuario.getRol() == UserRol.ROLE_ADMINISTRADOR_VETERINARIA)) {
            throw new IllegalArgumentException("Solo los usuarios con rol VETERINARIO o ADMINISTRADOR_VETERINARIA pueden tener citas asignadas.");
        }

        // Validar existencia de sucursal
        Subsidiary sucursal = subsidiaryRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + dto.getIdSucursal()));

        // 5️⃣ Validar que no exista una cita en el mismo horario con el mismo veterinario
        boolean existsSameVetTime = appointmentRepository
                .existsByUsuarioIdUsuarioAndFechaHora(dto.getIdUsuario(), dto.getFechaHora());

        if (existsSameVetTime) {
            throw new IllegalArgumentException("El veterinario ya tiene una cita programada en esa fecha y hora.");
        }

        // 6️⃣ Validar que la mascota no tenga otra cita en el mismo horario
        boolean existsSamePetTime = appointmentRepository
                .existsByMascotaIdMascotaAndFechaHora(dto.getIdMascota(), dto.getFechaHora());

        if (existsSamePetTime) {
            throw new IllegalArgumentException("La mascota ya tiene una cita en esa fecha y hora.");
        }

        // 7️⃣ Crear y guardar la cita
        Appointment appointment = Appointment.builder()
                .cliente(cliente)
                .mascota(mascota)
                .usuario(usuario)
                .sucursal(sucursal)
                .fechaHora(dto.getFechaHora())
                .estado(dto.getEstado())
                .motivo(dto.getMotivo())
                .build();

        appointmentRepository.save(appointment);

        sendAppointmentCreatedNotification(appointment);

        return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponseDTO update(Long idCita, AppointmentUpdateDTO dto) {
        // 🔹 1. Buscar la cita
        Appointment cita = appointmentRepository.findById(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + idCita));

        // 🔹 2. Validar estado actual
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new IllegalArgumentException("No se puede modificar una cita que ya está " + cita.getEstado());
        }

        // 🔹 3. Validar si se intenta cambiar usuario (veterinario)
        if (dto.getIdUsuario() != null && !dto.getIdUsuario().equals(cita.getUsuario().getIdUsuario())) {
            User nuevoUsuario = userRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));

            // Validar disponibilidad del nuevo veterinario
            if (dto.getFechaHora() != null) {
                boolean existeCita = appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(nuevoUsuario.getIdUsuario(), dto.getFechaHora());
                if (existeCita) {
                    throw new IllegalArgumentException("El veterinario ya tiene una cita en la nueva fecha y hora indicada.");
                }
            }

            cita.setUsuario(nuevoUsuario);
        }

        // 🔹 4. Validar cambio de fecha/hora
        if (dto.getFechaHora() != null && !dto.getFechaHora().equals(cita.getFechaHora())) {
            if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La fecha y hora deben ser futuras.");
            }

            boolean existeCita = appointmentRepository.existsByUsuarioIdUsuarioAndFechaHora(
                    cita.getUsuario().getIdUsuario(), dto.getFechaHora());
            if (existeCita) {
                throw new IllegalArgumentException("El veterinario ya tiene una cita en la fecha y hora indicada.");
            }

            cita.setFechaHora(dto.getFechaHora());
        }

        // 🔹 5. Actualizar estado si se envía uno nuevo
        if (dto.getEstado() != null) {
            cita.setEstado(dto.getEstado());
        }

        // 🔹 6. Actualizar motivo si se envía uno nuevo
        if (dto.getMotivo() != null && !dto.getMotivo().isBlank()) {
            cita.setMotivo(dto.getMotivo());
        }

        // 🔹 7. Guardar cambios
        Appointment updated = appointmentRepository.save(cita);

        sendAppointmentUpdatedNotification(updated);

        // 🔹 8. Retornar DTO
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long idCita) {
        if (!appointmentRepository.existsById(idCita)) {
            throw new ResourceNotFoundException("Cita no encontrada con ID: " + idCita);
        }
        appointmentRepository.deleteById(idCita);
    }

    @Override
    public Optional<AppointmentResponseDTO> findById(Long idCita) {
        return appointmentRepository.findById(idCita)
                .map(this::mapToResponse);
    }

    @Override
    public List<AppointmentResponseDTO> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> findByCliente(Long idCliente) {
        return appointmentRepository.findByClienteIdCliente(idCliente).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> findByMascota(Long idMascota) {
        return appointmentRepository.findByMascotaIdMascota(idMascota).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> findByUsuario(Long idUsuario) {
        return appointmentRepository.findByUsuarioIdUsuario(idUsuario).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> findBySucursal(Long idSucursal) {
        return appointmentRepository.findBySucursalIdSubsidiary(idSucursal).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> findByEstado(EstadoCita estado) {
        return appointmentRepository.findByEstado(estado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();
        return appointmentRepository.findByFechaHoraBetween(inicio, fin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByMascotaAndFechaHora(Long idMascota, LocalDate fecha, int hora, int minuto) {
        LocalDateTime fechaHora = fecha.atTime(hora, minuto);
        return appointmentRepository.existsByMascotaIdMascotaAndFechaHora(idMascota, fechaHora);
    }

    @Override
    public long countByCliente(Long idCliente) {
        return appointmentRepository.countByClienteIdCliente(idCliente);
    }

    @Override
    public long countByEstado(EstadoCita estado) {
        return appointmentRepository.countByEstado(estado);
    }

    private AppointmentResponseDTO mapToResponse(Appointment cita) {
        return AppointmentResponseDTO.builder()
                .idCita(cita.getIdCita())
                .fechaHora(cita.getFechaHora())
                .estado(cita.getEstado())
                .motivo(cita.getMotivo())
                .idCliente(cita.getCliente().getIdCliente())
                .nombreCliente(cita.getCliente().getNombres()+" "+cita.getCliente().getApellidos())
                .idMascota(cita.getMascota().getIdMascota())
                .nombreMascota(cita.getMascota().getNombre())
                .idUsuario(cita.getUsuario().getIdUsuario())
                .nombreUsuario(cita.getUsuario().getUsername())
                .idSucursal(cita.getSucursal().getIdSubsidiary())
                .nombreSucursal(cita.getSucursal().getName())
                .build();
    }

    // ===== MÉTODOS HELPER DE NOTIFICACIONES =====

    private void sendAppointmentCreatedNotification(Appointment appointment) {
        NotificationRequestDTO notification = new NotificationRequestDTO();
        notification.setTitulo(NotificationTitle.CITA_CREADA);
        notification.setAsunto("Nueva cita programada");
        notification.setMensaje(String.format(
                "Se ha programado una cita para %s el %s. Motivo: %s",
                appointment.getMascota().getNombre(),
                appointment.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                appointment.getMotivo()
        ));
        notification.setTipo("CITA");
        notification.setIdCliente(appointment.getCliente().getIdCliente());
        notification.setIdVeterinario(appointment.getUsuario().getIdUsuario()); // ✅ Corregido
        notification.setIdCita(appointment.getIdCita());

        notificationService.createNotification(notification);
    }

    private void sendAppointmentUpdatedNotification(Appointment appointment) {
        NotificationRequestDTO notification = new NotificationRequestDTO();
        notification.setTitulo(NotificationTitle.CITA_CONFIRMADA);
        notification.setAsunto("Cita modificada");
        notification.setMensaje(String.format(
                "Su cita para %s ha sido modificada. Nueva fecha: %s",
                appointment.getMascota().getNombre(),
                appointment.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ));
        notification.setTipo("CITA");
        notification.setIdCliente(appointment.getCliente().getIdCliente());
        notification.setIdVeterinario(appointment.getUsuario().getIdUsuario()); // ✅ Corregido
        notification.setIdCita(appointment.getIdCita());

        notificationService.createNotification(notification);
    }

    private void sendAppointmentCancelledNotification(Appointment appointment) {
        NotificationRequestDTO notification = new NotificationRequestDTO();
        notification.setTitulo(NotificationTitle.CITA_CANCELADA);
        notification.setAsunto("Cita cancelada");
        notification.setMensaje(String.format(
                "La cita para %s programada el %s ha sido cancelada",
                appointment.getMascota().getNombre(),
                appointment.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ));
        notification.setTipo("CITA");
        notification.setIdCliente(appointment.getCliente().getIdCliente());
        notification.setIdVeterinario(appointment.getUsuario().getIdUsuario()); // ✅ Corregido
        notification.setIdCita(appointment.getIdCita());

        notificationService.createNotification(notification);
    }
}
