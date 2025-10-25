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
            description = "Registra un nuevo tratamiento para una consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tratamiento creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Consulta, mascota o historial no encontrado"),
            @ApiResponse(responseCode = "409", description = "El número del tratamiento ya existe")
    })
    public ResponseEntity<TreatmentResponseDTO> createTreatment(
            @Valid @RequestBody TreatmentCreateDTO treatmentCreateDTO) {
        TreatmentResponseDTO createdTreatment = treatmentService.createTreatment(treatmentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTreatment);
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tratamiento por ID",
            description = "Retorna los detalles de un tratamiento específico")
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
            description = "Retorna todos los tratamientos asociados a una consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida",
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
            description = "Retorna todos los tratamientos de una mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida",
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
            description = "Retorna solo los tratamientos activos (estado = true) de una mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida",
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
            description = "Retorna una lista de todos los tratamientos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tratamientos obtenida",
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
            description = "Actualiza los datos de un tratamiento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tratamiento actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tratamiento no encontrado"),
            @ApiResponse(responseCode = "409", description = "El número del tratamiento ya existe")
    })
    public ResponseEntity<TreatmentResponseDTO> updateTreatment(
            @Parameter(description = "ID del tratamiento", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TreatmentUpdateDTO treatmentUpdateDTO) {
        TreatmentResponseDTO updatedTreatment = treatmentService.updateTreatment(id, treatmentUpdateDTO);
        return ResponseEntity.ok(updatedTreatment);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de un tratamiento",
            description = "Cambia el estado (activo/inactivo) de un tratamiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TreatmentResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Tratamiento no encontrado")
    })
    public ResponseEntity<TreatmentResponseDTO> updateTreatmentStatus(
            @Parameter(description = "ID del tratamiento", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "false")
            @RequestParam Boolean nuevoEstado) {
        TreatmentResponseDTO updatedTreatment = treatmentService.updateTreatmentStatus(id, nuevoEstado);
        return ResponseEntity.ok(updatedTreatment);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tratamiento",
            description = "Elimina un tratamiento de la base de datos")
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
