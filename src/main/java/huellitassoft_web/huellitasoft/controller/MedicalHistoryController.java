package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryCreateDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryDetailDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryResponseDTO;
import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import huellitassoft_web.huellitasoft.service.MedicalHistoryService;
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
@RequestMapping("/api/historiales-clinicos")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Medical History", description = "Gestión de historiales clínicos de mascotas")
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    // ==================== CREATE ====================

    @PostMapping
    @Operation(summary = "Crear un nuevo historial clínico", 
            description = "Crea un nuevo historial clínico para una mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Historial clínico creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o mascota no existe"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "409", description = "El historial clínico ya existe para esta mascota")
    })
    public ResponseEntity<MedicalHistoryResponseDTO> createMedicalHistory(
            @Valid @RequestBody MedicalHistoryCreateDTO medicalHistoryCreateDTO) {
        MedicalHistoryResponseDTO createdMedicalHistory = medicalHistoryService.createMedicalHistory(medicalHistoryCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMedicalHistory);
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle completo de un historial clínico", 
            description = "Retorna el historial clínico con todas sus consultas y tratamientos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial clínico encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryDetailDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Historial clínico no encontrado")
    })
    public ResponseEntity<MedicalHistoryDetailDTO> getMedicalHistoryDetail(
            @Parameter(description = "ID del historial clínico", example = "1")
            @PathVariable Long id) {
        MedicalHistoryDetailDTO medicalHistoryDetail = medicalHistoryService.getMedicalHistoryDetail(id);
        return ResponseEntity.ok(medicalHistoryDetail);
    }

    @GetMapping("/simple/{id}")
    @Operation(summary = "Obtener información básica de un historial clínico",
            description = "Retorna la información básica sin consultas y tratamientos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial clínico encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Historial clínico no encontrado")
    })
    public ResponseEntity<MedicalHistoryResponseDTO> getMedicalHistoryById(
            @Parameter(description = "ID del historial clínico", example = "1")
            @PathVariable Long id) {
        MedicalHistoryResponseDTO medicalHistory = medicalHistoryService.getMedicalHistoryById(id);
        return ResponseEntity.ok(medicalHistory);
    }

    @GetMapping("/mascota/{idMascota}")
    @Operation(summary = "Obtener historial clínico por ID de mascota",
            description = "Obtiene el historial clínico de una mascota específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial clínico encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "No existe historial para esta mascota")
    })
    public ResponseEntity<MedicalHistoryResponseDTO> getMedicalHistoryByMascota(
            @Parameter(description = "ID de la mascota", example = "5")
            @PathVariable Long idMascota) {
        MedicalHistoryResponseDTO medicalHistory = medicalHistoryService.getMedicalHistoryByMascota(idMascota);
        return ResponseEntity.ok(medicalHistory);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los historiales clínicos",
            description = "Retorna una lista de todos los historiales clínicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de historiales obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<MedicalHistoryResponseDTO>> getAllMedicalHistories() {
        List<MedicalHistoryResponseDTO> medicalHistories = medicalHistoryService.getAllMedicalHistories();
        return ResponseEntity.ok(medicalHistories);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener historiales clínicos por estado",
            description = "Retorna una lista de historiales filtrados por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<MedicalHistoryResponseDTO>> getMedicalHistoriesByEstado(
            @Parameter(description = "Estado del historial (ACTIVO, INACTIVO, ELIMINADO)", example = "ACTIVO")
            @PathVariable MedicalHistoryState estado) {
        List<MedicalHistoryResponseDTO> medicalHistories = medicalHistoryService.getMedicalHistoriesByEstado(estado);
        return ResponseEntity.ok(medicalHistories);
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de un historial clínico",
            description = "Cambia el estado del historial clínico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicalHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Historial clínico no encontrado")
    })
    public ResponseEntity<MedicalHistoryResponseDTO> updateEstadoMedicalHistory(
            @Parameter(description = "ID del historial clínico", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "INACTIVO")
            @RequestParam MedicalHistoryState nuevoEstado) {
        MedicalHistoryResponseDTO updatedMedicalHistory = medicalHistoryService.updateEstadoMedicalHistory(id, nuevoEstado);
        return ResponseEntity.ok(updatedMedicalHistory);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un historial clínico",
            description = "Realiza una eliminación lógica del historial clínico (cambio de estado a ELIMINADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Historial clínico eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Historial clínico no encontrado")
    })
    public ResponseEntity<Void> deleteMedicalHistory(
            @Parameter(description = "ID del historial clínico", example = "1")
            @PathVariable Long id) {
        medicalHistoryService.deleteMedicalHistory(id);
        return ResponseEntity.noContent().build();
    }
}
