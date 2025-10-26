package huellitassoft_web.huellitasoft.controller;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeResponseDTO;
import huellitassoft_web.huellitasoft.dto.petScheme.PetSchemeUpdateStateDTO;
import huellitassoft_web.huellitasoft.service.PetSchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mascota-esquemas")
@AllArgsConstructor
@Tag(name = "Seguimiento de Esquemas por Mascota",
        description = "API para asignar esquemas a mascotas y gestionar su estado (ACTIVO, COMPLETADO, CANCELADO)")
public class PetSchemeController {

    private final PetSchemeService petSchemeService;

    @PostMapping
    @Operation(
            summary = "Asignar esquema a mascota",
            description = "Crea la relación mascota ↔ esquema. Por defecto el estado inicial es ACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignación creada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Mascota o esquema no encontrados"),
            @ApiResponse(responseCode = "409", description = "La mascota ya tiene asignado ese esquema"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PetSchemeResponseDTO> assign(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de asignación (idMascota, idEsquema, estado opcional)",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeCreateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody PetSchemeCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petSchemeService.assignSchemeToPet(dto));
    }

    @PatchMapping("/{idMascotaEsquema}/estado")
    @Operation(
            summary = "Actualizar estado del seguimiento",
            description = "Cambia el estado de la relación a ACTIVO, COMPLETADO o CANCELADO"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Relación mascota-esquema no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PetSchemeResponseDTO> updateState(
            @Parameter(description = "ID de la relación mascota_esquema", example = "15", required = true)
            @PathVariable Long idMascotaEsquema,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo estado del seguimiento",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeUpdateStateDTO.class)))
            @org.springframework.web.bind.annotation.RequestBody PetSchemeUpdateStateDTO dto) {
        return ResponseEntity.ok(petSchemeService.updateSchemeState(idMascotaEsquema, dto));
    }

    @GetMapping("/{idMascotaEsquema}")
    @Operation(
            summary = "Obtener una asignación por ID",
            description = "Retorna el detalle de la relación mascota ↔ esquema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relación encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Relación mascota-esquema no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PetSchemeResponseDTO> getById(
            @Parameter(description = "ID de la relación", example = "15", required = true)
            @PathVariable Long idMascotaEsquema) {
        return ResponseEntity.ok(petSchemeService.getPetSchemeById(idMascotaEsquema));
    }

    @GetMapping("/mascota/{idMascota}")
    @Operation(
            summary = "Listar todas las asignaciones de una mascota",
            description = "Incluye asignaciones en cualquier estado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PetSchemeResponseDTO>> getByPet(
            @Parameter(description = "ID de la mascota", example = "10", required = true)
            @PathVariable Long idMascota) {
        return ResponseEntity.ok(petSchemeService.getPetSchemes(idMascota));
    }

    @GetMapping("/mascota/{idMascota}/activos")
    @Operation(
            summary = "Listar asignaciones ACTIVAS de una mascota",
            description = "Retorna solo las relaciones cuyo estado es ACTIVO"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PetSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PetSchemeResponseDTO>> getActiveByPet(
            @Parameter(description = "ID de la mascota", example = "10", required = true)
            @PathVariable Long idMascota) {
        return ResponseEntity.ok(petSchemeService.getActivePetSchemes(idMascota));
    }

    @DeleteMapping("/{idMascotaEsquema}")
    @Operation(
            summary = "Eliminar una asignación (desasignar)",
            description = "Elimina la relación mascota ↔ esquema del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asignación eliminada"),
            @ApiResponse(responseCode = "404", description = "Relación mascota-esquema no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> remove(
            @Parameter(description = "ID de la relación", example = "15", required = true)
            @PathVariable Long idMascotaEsquema) {
        petSchemeService.unassignScheme(idMascotaEsquema);
        return ResponseEntity.noContent().build();
    }
}
