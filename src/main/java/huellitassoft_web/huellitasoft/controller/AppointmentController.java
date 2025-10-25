package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.appointment.AppointmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentUpdateDTO;
import huellitassoft_web.huellitasoft.enums.EstadoCita;
import huellitassoft_web.huellitasoft.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@Tag(name = "Citas", description = "Operaciones relacionadas con la gestión de citas veterinarias")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Obtener todas las citas", description = "Devuelve una lista de todas las citas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de citas obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @Operation(summary = "Obtener una cita por ID", description = "Busca una cita por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita encontrada"),
            @ApiResponse(responseCode = "404", description = "No se encontró la cita con el ID especificado")
    })
    @GetMapping("/{idCita}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(
            @Parameter(description = "ID de la cita a buscar", example = "1") @PathVariable Long idCita) {
        return appointmentService.findById(idCita)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva cita", description = "Crea una nueva cita veterinaria validando disponibilidad, roles y reglas de negocio.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cita creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o conflicto de horario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentCreateDTO dto) {
        AppointmentResponseDTO response = appointmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar una cita existente", description = "Permite modificar los datos de una cita registrada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PutMapping("/{idCita}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @Parameter(description = "ID de la cita a actualizar", example = "1") @PathVariable Long idCita,
            @Valid @RequestBody AppointmentUpdateDTO dto) {
        AppointmentResponseDTO response = appointmentService.update(idCita, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar una cita", description = "Elimina una cita del sistema por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cita eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @DeleteMapping("/{idCita}")
    public ResponseEntity<Void> deleteAppointment(
            @Parameter(description = "ID de la cita a eliminar", example = "1") @PathVariable Long idCita) {
        appointmentService.delete(idCita);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // ==================== CONSULTAS PERSONALIZADAS ===============
    // ============================================================

    @Operation(summary = "Obtener citas por cliente", description = "Devuelve todas las citas asociadas a un cliente específico.")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByClient(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long idCliente) {
        return ResponseEntity.ok(appointmentService.findByCliente(idCliente));
    }

    @Operation(summary = "Obtener citas por mascota", description = "Devuelve todas las citas asociadas a una mascota específica.")
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPet(
            @Parameter(description = "ID de la mascota", example = "3") @PathVariable Long idMascota) {
        return ResponseEntity.ok(appointmentService.findByMascota(idMascota));
    }

    @Operation(summary = "Obtener citas por usuario", description = "Devuelve todas las citas asignadas a un veterinario o usuario.")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByUser(
            @Parameter(description = "ID del usuario", example = "2") @PathVariable Long idUsuario) {
        return ResponseEntity.ok(appointmentService.findByUsuario(idUsuario));
    }

    @Operation(summary = "Obtener citas por sucursal", description = "Devuelve todas las citas programadas en una sucursal específica.")
    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByBranch(
            @Parameter(description = "ID de la sucursal", example = "4") @PathVariable Long idSucursal) {
        return ResponseEntity.ok(appointmentService.findBySucursal(idSucursal));
    }

    @Operation(summary = "Obtener citas por estado", description = "Devuelve todas las citas con un estado específico.")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByState(
            @Parameter(description = "Estado de la cita", example = "PENDIENTE") @PathVariable EstadoCita estado) {
        return ResponseEntity.ok(appointmentService.findByEstado(estado));
    }

    @Operation(summary = "Buscar citas por rango de fechas", description = "Filtra las citas entre dos fechas determinadas.")
    @GetMapping("/fechas")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsBetweenDates(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", example = "2025-11-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", example = "2025-11-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(appointmentService.findByFechaBetween(fechaInicio, fechaFin));
    }

    @Operation(summary = "Contar citas por cliente", description = "Devuelve la cantidad total de citas asociadas a un cliente.")
    @GetMapping("/cliente/{idCliente}/count")
    public ResponseEntity<Long> countAppointmentsByClient(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long idCliente) {
        return ResponseEntity.ok(appointmentService.countByCliente(idCliente));
    }

    @Operation(summary = "Contar citas por estado", description = "Devuelve la cantidad total de citas según su estado actual.")
    @GetMapping("/estado/{estado}/count")
    public ResponseEntity<Long> countAppointmentsByState(
            @Parameter(description = "Estado de la cita", example = "PENDIENTE") @PathVariable EstadoCita estado) {
        return ResponseEntity.ok(appointmentService.countByEstado(estado));
    }
}
