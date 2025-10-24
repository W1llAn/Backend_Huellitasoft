package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.specie.SpecieCreateDTO;
import huellitassoft_web.huellitasoft.dto.specie.SpecieResponseDTO;
import huellitassoft_web.huellitasoft.service.SpecieService;
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
@RequestMapping("/api/especies")
@AllArgsConstructor
@Tag(
        name = "Especies",
        description = "API de gestión de especies de animales. Permite crear, leer, actualizar y eliminar especies."
)
public class SpecieController {

    private final SpecieService specieService;

    @GetMapping
    @Operation(
            summary = "Obtener todas las especies",
            description = "Retorna una lista completa de todas las especies registradas en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de especies obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecieResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<SpecieResponseDTO>> getAllSpecies() {
        return ResponseEntity.ok(specieService.getAllSpecies());
    }

    @GetMapping("/{idEspecie}")
    @Operation(
            summary = "Obtener especie por ID",
            description = "Obtiene los detalles de una especie específica usando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Especie encontrada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecieResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Especie no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<SpecieResponseDTO> getSpecieById(
            @Parameter(description = "ID único de la especie", example = "1", required = true)
            @PathVariable Long idEspecie) {
        return ResponseEntity.ok(specieService.getSpecieById(idEspecie));
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva especie",
            description = "Crea una nueva especie en el sistema. El nombre debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Especie creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecieResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud (campos requeridos faltantes o con formato incorrecto)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una especie con ese nombre"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<SpecieResponseDTO> createSpecie(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la especie a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecieCreateDTO.class)
                    )
            )
            @Valid @RequestBody SpecieCreateDTO specieCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specieService.createSpecie(specieCreateDTO));
    }

    @PutMapping("/{idEspecie}")
    @Operation(
            summary = "Actualizar especie",
            description = "Actualiza los datos de una especie existente. El nombre debe ser único (si se modifica)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Especie actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpecieResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Especie no encontrada"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otra especie con ese nombre"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<SpecieResponseDTO> updateSpecie(
            @Parameter(description = "ID único de la especie a actualizar", example = "1", required = true)
            @PathVariable Long idEspecie,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la especie",
                    required = true
            )
            @Valid @RequestBody SpecieCreateDTO specieCreateDTO) {
        return ResponseEntity.ok(specieService.updateSpecie(idEspecie, specieCreateDTO));
    }

    @DeleteMapping("/{idEspecie}")
    @Operation(
            summary = "Eliminar especie",
            description = "Elimina una especie del sistema. La operación es permanente e irreversible. " +
                    "Nota: Al eliminar una especie se eliminarán también todas las razas asociadas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Especie eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Especie no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> deleteSpecie(
            @Parameter(description = "ID único de la especie a eliminar", example = "1", required = true)
            @PathVariable Long idEspecie) {
        specieService.deleteSpecie(idEspecie);
        return ResponseEntity.noContent().build();
    }
}
