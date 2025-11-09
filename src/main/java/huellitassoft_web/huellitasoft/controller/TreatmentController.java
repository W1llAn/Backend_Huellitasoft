package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.treatment.TreatmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentUpdateDTO;
import huellitassoft_web.huellitasoft.service.TreatmentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tratamientos")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Treatment", description = "Gestión de tratamientos de mascotas")

public class TreatmentController {

    private final TreatmentService treatmentService;

    // ==================== CREATE ====================

    @PostMapping
    @Operation(summary = "Crear un nuevo tratamiento",
            description = "Registra un nuevo tratamiento médico con medicamento, dosis, frecuencia y duración para una consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tratamiento creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o falta información requerida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Consulta o mascota no encontrada")
    })
    public ResponseEntity<TreatmentResponseDTO> createTreatment(
            @Valid @RequestBody TreatmentCreateDTO treatmentCreateDTO) {
        TreatmentResponseDTO createdTreatment = treatmentService.createTreatment(treatmentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTreatment);
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tratamiento por ID",
            description = "Retorna los detalles completos de un tratamiento específico incluyendo medicamento, dosis, frecuencia y observaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tratamiento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tratamiento no encontrado")
    })
    public ResponseEntity<TreatmentResponseDTO> getTreatmentById(
            @Parameter(description = "ID del tratamiento", example = "1")
            @PathVariable Long id) {
        TreatmentResponseDTO treatment = treatmentService.getTreatmentById(id);
        return ResponseEntity.ok(treatment);
    }

    @GetMapping("/consulta/{idConsulta}")
    @Operation(summary = "Obtener tratamientos por consulta",
            description = "Retorna todos los tratamientos médicos asociados a una consulta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<TreatmentResponseDTO>> getTreatmentsByConsultation(
            @Parameter(description = "ID de la consulta", example = "1")
            @PathVariable Long idConsulta) {
        List<TreatmentResponseDTO> treatments = treatmentService.getTreatmentsByConsultation(idConsulta);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping("/mascota/{idMascota}")
    @Operation(summary = "Obtener tratamientos por mascota",
            description = "Retorna el historial completo de tratamientos médicos de una mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<TreatmentResponseDTO>> getTreatmentsByMascota(
            @Parameter(description = "ID de la mascota", example = "5")
            @PathVariable Long idMascota) {
        List<TreatmentResponseDTO> treatments = treatmentService.getTreatmentsByMascota(idMascota);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping("/mascota/{idMascota}/activos")
    @Operation(summary = "Obtener tratamientos activos de una mascota",
            description = "Retorna únicamente los tratamientos activos (en curso) de una mascota específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos activos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<TreatmentResponseDTO>> getActiveTreatmentsByMascota(
            @Parameter(description = "ID de la mascota", example = "5")
            @PathVariable Long idMascota) {
        List<TreatmentResponseDTO> treatments = treatmentService.getActiveTreatmentsByMascota(idMascota);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los tratamientos",
            description = "Retorna una lista completa de todos los tratamientos médicos registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<TreatmentResponseDTO>> getAllTreatments() {
        List<TreatmentResponseDTO> treatments = treatmentService.getAllTreatments();
        return ResponseEntity.ok(treatments);
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tratamiento",
            description = "Actualiza la información de un tratamiento existente incluyendo descripción, medicamento, dosis, frecuencia, duración y observaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tratamiento actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tratamiento no encontrado")
    })
    public ResponseEntity<TreatmentResponseDTO> updateTreatment(
            @Parameter(description = "ID del tratamiento", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TreatmentUpdateDTO treatmentUpdateDTO) {
        TreatmentResponseDTO updatedTreatment = treatmentService.updateTreatment(id, treatmentUpdateDTO);
        return ResponseEntity.ok(updatedTreatment);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de un tratamiento",
            description = "Cambia el estado de un tratamiento entre activo (true) e inactivo (false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tratamiento no encontrado")
    })
    public ResponseEntity<TreatmentResponseDTO> updateTreatmentStatus(
            @Parameter(description = "ID del tratamiento", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado del tratamiento (true = activo, false = inactivo)", example = "false")
            @RequestParam Boolean nuevoEstado) {
        TreatmentResponseDTO updatedTreatment = treatmentService.updateTreatmentStatus(id, nuevoEstado);
        return ResponseEntity.ok(updatedTreatment);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tratamiento",
            description = "Elimina permanentemente un tratamiento del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tratamiento eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tratamiento no encontrado")
    })
    public ResponseEntity<Void> deleteTreatment(
            @Parameter(description = "ID del tratamiento", example = "1")
            @PathVariable Long id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
