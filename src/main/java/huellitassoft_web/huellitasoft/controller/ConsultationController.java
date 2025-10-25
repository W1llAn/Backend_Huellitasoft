package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.consultation.ConsultationCreateDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationResponseDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationUpdateDTO;
import huellitassoft_web.huellitasoft.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Consultation", description = "Gestión de consultas veterinarias")
public class ConsultationController {

    private final ConsultationService consultationService;

    // ==================== CREATE ====================

    @PostMapping
    @Operation(summary = "Crear una nueva consulta veterinaria",
            description = "Registra una nueva consulta veterinaria para un historial clínico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Historial clínico o veterinario no encontrado")
    })
    public ResponseEntity<ConsultationResponseDTO> createConsultation(
            @Valid @RequestBody ConsultationCreateDTO consultationCreateDTO) {
        ConsultationResponseDTO createdConsultation = consultationService.createConsultation(consultationCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConsultation);
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una consulta por ID",
            description = "Retorna los detalles de una consulta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Consulta no encontrada")
    })
    public ResponseEntity<ConsultationResponseDTO> getConsultationById(
            @Parameter(description = "ID de la consulta", example = "1")
            @PathVariable Long id) {
        ConsultationResponseDTO consultation = consultationService.getConsultationById(id);
        return ResponseEntity.ok(consultation);
    }

    @GetMapping("/historial/{idHistoria}")
    @Operation(summary = "Obtener consultas por historial clínico",
            description = "Retorna todas las consultas de un historial clínico específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas obtenida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByHistoria(
            @Parameter(description = "ID del historial clínico", example = "1")
            @PathVariable Long idHistoria) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByHistoria(idHistoria);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/mascota/{idMascota}")
    @Operation(summary = "Obtener consultas por mascota",
            description = "Retorna todas las consultas de una mascota específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas obtenida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByMascota(
            @Parameter(description = "ID de la mascota", example = "5")
            @PathVariable Long idMascota) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByMascota(idMascota);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/veterinario/{idVeterinario}")
    @Operation(summary = "Obtener consultas por veterinario",
            description = "Retorna todas las consultas realizadas por un veterinario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas obtenida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByVeterinario(
            @Parameter(description = "ID del veterinario", example = "3")
            @PathVariable Integer idVeterinario) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByVeterinario(idVeterinario);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/rango-fechas")
    @Operation(summary = "Obtener consultas en un rango de fechas",
            description = "Retorna las consultas realizadas dentro de un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas obtenida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByFechaHora(
            @Parameter(description = "Fecha inicial (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fecha final (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByFechaHora(inicio, fin);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/mascota/{idMascota}/ultima")
    @Operation(summary = "Obtener última consulta de una mascota",
            description = "Retorna la consulta más reciente de una mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "No hay consultas para esta mascota")
    })
    public ResponseEntity<ConsultationResponseDTO> getLastConsultationByMascota(
            @Parameter(description = "ID de la mascota", example = "5")
            @PathVariable Long idMascota) {
        ConsultationResponseDTO consultation = consultationService.getLastConsultationByMascota(idMascota);
        return ResponseEntity.ok(consultation);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las consultas",
            description = "Retorna una lista de todas las consultas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas obtenida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<ConsultationResponseDTO>> getAllConsultations() {
        List<ConsultationResponseDTO> consultations = consultationService.getAllConsultations();
        return ResponseEntity.ok(consultations);
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una consulta veterinaria",
            description = "Actualiza los datos de una consulta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Consulta no encontrada")
    })
    public ResponseEntity<ConsultationResponseDTO> updateConsultation(
            @Parameter(description = "ID de la consulta", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ConsultationUpdateDTO consultationUpdateDTO) {
        ConsultationResponseDTO updatedConsultation = consultationService.updateConsultation(id, consultationUpdateDTO);
        return ResponseEntity.ok(updatedConsultation);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una consulta veterinaria",
            description = "Elimina una consulta y sus tratamientos asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consulta eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Consulta no encontrada")
    })
    public ResponseEntity<Void> deleteConsultation(
            @Parameter(description = "ID de la consulta", example = "1")
            @PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }
}
