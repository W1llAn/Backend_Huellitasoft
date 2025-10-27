package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.Notification.NotificationRequestDTO;
import huellitassoft_web.huellitasoft.dto.Notification.NotificationResponseDTO;
import huellitassoft_web.huellitasoft.service.impl.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints para gestión de notificaciones")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Crear notificación",
            description = "Crea una nueva notificación y la envía por email y WebSocket")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas las notificaciones")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener notificación por ID")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar notificaciones de un cliente")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(notificationService.getNotificationsByClient(clientId));
    }

    @GetMapping("/veterinarian/{veterinarianId}")
    @Operation(summary = "Listar notificaciones de un veterinario")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByVeterinarian(
            @PathVariable Long veterinarianId) {
        return ResponseEntity.ok(notificationService.getNotificationsByVeterinarian(veterinarianId));
    }

    @GetMapping("/client/{clientId}/unread")
    @Operation(summary = "Listar notificaciones no leídas de un cliente")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotificationsByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByClient(clientId));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificación")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}