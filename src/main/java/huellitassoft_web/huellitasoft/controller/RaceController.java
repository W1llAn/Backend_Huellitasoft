package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.race.RaceCreateDTO;
import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;
import huellitassoft_web.huellitasoft.service.RaceService;
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
@RequestMapping("/api/razas")
@AllArgsConstructor
@Tag(
        name = "Razas",
        description = "API de gestión de razas de animales. Permite crear, leer, actualizar y eliminar razas."
)
public class RaceController {

    private final RaceService raceService;

    @GetMapping
    @Operation(
            summary = "Obtener todas las razas",
            description = "Retorna una lista completa de todas las razas registradas en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de razas obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RaceResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<RaceResponseDTO>> getAllRaces() {
        return ResponseEntity.ok(raceService.getAllRaces());
    }

    @GetMapping("/{idRaza}")
    @Operation(
            summary = "Obtener raza por ID",
            description = "Obtiene los detalles de una raza específica usando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Raza encontrada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RaceResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Raza no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<RaceResponseDTO> getRaceById(
            @Parameter(description = "ID único de la raza", example = "1", required = true)
            @PathVariable Long idRaza) {
        return ResponseEntity.ok(raceService.getRaceById(idRaza));
    }

    @GetMapping("/especie/{idEspecie}")
    @Operation(
            summary = "Obtener razas por especie",
            description = "Retorna todas las razas que pertenecen a una especie específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Razas de la especie obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RaceResponseDTO.class)
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
    public ResponseEntity<List<RaceResponseDTO>> getRacesBySpecie(
            @Parameter(description = "ID único de la especie", example = "1", required = true)
            @PathVariable Long idEspecie) {
        return ResponseEntity.ok(raceService.getRacesBySpecie(idEspecie));
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva raza",
            description = "Crea una nueva raza en el sistema. El nombre debe ser único y la especie debe existir."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Raza creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RaceResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud (campos requeridos faltantes o con formato incorrecto)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una raza con ese nombre o la especie no existe"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<RaceResponseDTO> createRace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la raza a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RaceCreateDTO.class)
                    )
            )
            @Valid @RequestBody RaceCreateDTO raceCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(raceCreateDTO));
    }

    @PutMapping("/{idRaza}")
    @Operation(
            summary = "Actualizar raza",
            description = "Actualiza los datos de una raza existente. El nombre debe ser único (si se modifica) y la especie debe existir."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Raza actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RaceResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Raza o especie no encontrada"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otra raza con ese nombre"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<RaceResponseDTO> updateRace(
            @Parameter(description = "ID único de la raza a actualizar", example = "1", required = true)
            @PathVariable Long idRaza,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la raza",
                    required = true
            )
            @Valid @RequestBody RaceCreateDTO raceCreateDTO) {
        return ResponseEntity.ok(raceService.updateRace(idRaza, raceCreateDTO));
    }

    @DeleteMapping("/{idRaza}")
    @Operation(
            summary = "Eliminar raza",
            description = "Elimina una raza del sistema. La operación es permanente e irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Raza eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Raza no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> deleteRace(
            @Parameter(description = "ID único de la raza a eliminar", example = "1", required = true)
            @PathVariable Long idRaza) {
        raceService.deleteRace(idRaza);
        return ResponseEntity.noContent().build();
    }
}
