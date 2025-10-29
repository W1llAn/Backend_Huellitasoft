package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationRequestDTO;
import huellitassoft_web.huellitasoft.dto.Notification.NotificationResponseDTO;
import huellitassoft_web.huellitasoft.entity.*;
import huellitassoft_web.huellitasoft.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final VaccineRepository vaccineRepository;
    private final EmailService emailService;
    private final WebSocketService webSocketService;

    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        Notification notification = Notification.builder()
                .titulo(request.getTitulo())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .tipo(request.getTipo())
                .leida(false)
                .enviadaEmail(false)
                .build();

        if (request.getIdCliente() != null) {
            Client client = clientRepository.findById(request.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            notification.setCliente(client);
        }

        if (request.getIdVeterinario() != null) {
            User veterinarian = userRepository.findById(request.getIdVeterinario())
                    .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
            notification.setVeterinario(veterinarian);
        }

        if (request.getIdCita() != null) {
            Appointment appointment = appointmentRepository.findById(request.getIdCita())
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            notification.setCita(appointment);
        }

        if (request.getIdVacuna() != null) {
            Vaccine vaccine = vaccineRepository.findById(request.getIdVacuna())
                    .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
            notification.setVacuna(vaccine);
        }

        Notification savedNotification = notificationRepository.save(notification);

        // Enviar emails
        sendEmails(savedNotification);

        // Enviar por WebSocket
        NotificationResponseDTO response = mapToResponse(savedNotification);
        sendWebSocketNotifications(savedNotification, response);

        return response;
    }

    private void sendEmails(Notification notification) {
        try {
            String titulo = notification.getTitulo().getDisplayName();

            if (notification.getCliente() != null && notification.getCliente().getEmail() != null) {
                emailService.sendNotificationEmail(
                        notification.getCliente().getEmail(),
                        titulo,
                        notification.getAsunto(),
                        notification.getMensaje()
                );
            }

            if (notification.getVeterinario() != null && notification.getVeterinario().getEmail() != null) {
                emailService.sendNotificationEmail(
                        notification.getVeterinario().getEmail(),
                        titulo,
                        notification.getAsunto(),
                        notification.getMensaje()
                );
            }

            notification.setEnviadaEmail(true);
            notification.setFechaEnvioEmail(LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("Error al enviar emails para notificación {}: {}",
                    notification.getIdNotificacion(), e.getMessage());
        }
    }

    private void sendWebSocketNotifications(Notification notification, NotificationResponseDTO response) {
        if (notification.getCliente() != null) {
            webSocketService.sendNotificationToClient(
                    notification.getCliente().getIdCliente(),
                    response
            );
        }

        if (notification.getVeterinario() != null) {
            webSocketService.sendNotificationToVeterinarian(
                    notification.getVeterinario().getIdUsuario(),
                    response
            );
        }
    }

    public List<NotificationResponseDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NotificationResponseDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        return mapToResponse(notification);
    }

    public List<NotificationResponseDTO> getNotificationsByClient(Long clientId) {
        return notificationRepository.findByClienteIdClienteOrderByFechaCreacionDesc(clientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getNotificationsByVeterinarian(Long veterinarianId) {
        return notificationRepository.findByVeterinarioIdUsuarioOrderByFechaCreacionDesc(veterinarianId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getUnreadNotificationsByClient(Long clientId) {
        return notificationRepository.findByLeidaFalseAndClienteIdCliente(clientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponseDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notification.setLeida(true);
        return mapToResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada");
        }
        notificationRepository.deleteById(id);
    }

    private NotificationResponseDTO mapToResponse(Notification notification) {
        return NotificationResponseDTO.builder()
                .idNotificacion(notification.getIdNotificacion())
                .titulo(notification.getTitulo())
                .asunto(notification.getAsunto())
                .mensaje(notification.getMensaje())
                .tipo(notification.getTipo())
                .nombreCliente(notification.getCliente() != null ?
                        notification.getCliente().getNombres() + " " + notification.getCliente().getApellidos() : null)
                .emailCliente(notification.getCliente() != null ? notification.getCliente().getEmail() : null)
                .nombreVeterinario(notification.getVeterinario() != null ?
                        notification.getVeterinario().getUsername() + " " + notification.getVeterinario().getEmail() : null)
                .emailVeterinario(notification.getVeterinario() != null ? notification.getVeterinario().getEmail() : null)
                .leida(notification.getLeida())
                .enviadaEmail(notification.getEnviadaEmail())
                .fechaCreacion(notification.getFechaCreacion())
                .fechaEnvioEmail(notification.getFechaEnvioEmail())
                .build();
    }
}