package huellitassoft_web.huellitasoft.controller;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccinationScheme.VaccinationSchemeResponseDTO;
import huellitassoft_web.huellitasoft.service.VaccinationSchemeService;
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
@RequestMapping("/api/esquemas-vacunacion")
@Tag(name = "Esquemas de Vacunación",
        description = "API de gestión de esquemas de vacunación por vacuna: permite listar, crear, actualizar y eliminar")
@AllArgsConstructor
public class VaccinationSchemeController {
    private final VaccinationSchemeService vaccinationSchemeService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los esquemas de vacunación",
            description = "Retorna una lista completa de todos los esquemas registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<VaccinationSchemeResponseDTO>> getAll() {
        return ResponseEntity.ok(vaccinationSchemeService.getAllSchemes());
    }

    @GetMapping("/{idEsquema}")
    @Operation(
            summary = "Obtener esquema por ID",
            description = "Obtiene los detalles de un esquema de vacunación por su identificador"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Esquema encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Esquema no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VaccinationSchemeResponseDTO> getById(
            @Parameter(description = "ID único del esquema", example = "1", required = true)
            @PathVariable Long idEsquema) {
        return ResponseEntity.ok(vaccinationSchemeService.getSchemeById(idEsquema));
    }

    @GetMapping("/vacuna/{idVacuna}")
    @Operation(
            summary = "Obtener esquemas por vacuna",
            description = "Lista todos los esquemas de vacunación asociados a una vacuna específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Esquemas obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vacuna no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<VaccinationSchemeResponseDTO>> getByVaccine(
            @Parameter(description = "ID único de la vacuna", example = "2", required = true)
            @PathVariable Long idVacuna) {
        return ResponseEntity.ok(vaccinationSchemeService.getSchemesByVaccine(idVacuna));
    }

    @PostMapping
    @Operation(
            summary = "Crear nuevo esquema de vacunación",
            description = "Registra un nuevo esquema. (vacuna, dosis) no debe estar repetido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Esquema creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Vacuna no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe un esquema con la misma vacuna y dosis"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VaccinationSchemeResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del esquema a crear",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeCreateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody VaccinationSchemeCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccinationSchemeService.createScheme(dto));
    }

    @PutMapping("/{idEsquema}")
    @Operation(
            summary = "Actualizar esquema de vacunación",
            description = "Actualiza un esquema existente. Se valida que no duplique (vacuna, dosis)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Esquema actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Esquema o vacuna no encontrados"),
            @ApiResponse(responseCode = "409", description = "Conflicto por duplicidad vacuna+dosis"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VaccinationSchemeResponseDTO> update(
            @Parameter(description = "ID único del esquema", example = "1", required = true)
            @PathVariable Long idEsquema,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del esquema",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VaccinationSchemeCreateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody VaccinationSchemeCreateDTO dto) {
        return ResponseEntity.ok(vaccinationSchemeService.updateScheme(idEsquema, dto));
    }

    @DeleteMapping("/{idEsquema}")
    @Operation(
            summary = "Eliminar esquema de vacunación",
            description = "Elimina un esquema del sistema. Operación permanente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Esquema eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Esquema no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único del esquema", example = "1", required = true)
            @PathVariable Long idEsquema) {
        vaccinationSchemeService.deleteScheme(idEsquema);
        return ResponseEntity.noContent().build();
    }
}
