package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.race.RaceResponseDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineCreateDTO;
import huellitassoft_web.huellitasoft.dto.vaccine.VaccineResponseDTO;
import huellitassoft_web.huellitasoft.service.VaccineService;
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
@RequestMapping("/api/vacunas")
@AllArgsConstructor
@Tag(name = "Vacunas",
        description = "Api de gestión de vacunas de animales. Permite leer, crear, actualizar y eliminar vacunas  ")
public class VaccineController {
    private final VaccineService vaccineService;

    @GetMapping
    @Operation(
            summary = "Obtener todas las vacunas ",
            description = "Retorna una lista completa de todas las vacunas registradas dentro del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de vacunas obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VaccineResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<VaccineResponseDTO>> getAllVaccines() {
        return ResponseEntity.ok(vaccineService.getAllVaccines());
    }

    @GetMapping("/{idVacuna}")
    @Operation(
            summary = "Obtener vacuna por ID",
            description = "Obtiene los detalles de una vacuna específica usando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vacuna encontrada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VaccineResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vacuna no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<VaccineResponseDTO> getVaccineById(
            @Parameter(description = "ID único de la vacuna", example = "1", required = true)
            @PathVariable Long idVacuna) {
        return ResponseEntity.ok(vaccineService.getVaccineById(idVacuna));
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva vacuna",
            description = "Crea una nueva vacuna en el sistema. El nombre debe ser único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Vacuna creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VaccineResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud (campos requeridos faltantes o con formato incorrecto)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una vacuna con ese nombre"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<VaccineResponseDTO> createVaccine(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la vacuna a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VaccineCreateDTO.class)
                    )
            )
            @Valid @RequestBody VaccineCreateDTO vaccineCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineService.createVaccine(vaccineCreateDTO));
    }

    @PutMapping("/{idVacuna}")
    @Operation(
            summary = "Actualizar vacuna",
            description = "Actualiza los datos de una vacuna existente. El nombre debe ser único (si se modifica)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vacuna actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VaccineResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vacuna no encontrada"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otra vacuna con ese nombre dentro del bd"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<VaccineResponseDTO> updateVaccine(
            @Parameter(description = "ID único de la vacuna a actualizar", example = "1", required = true)
            @PathVariable Long idVacuna,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la vacuna",
                    required = true
            )
            @Valid @RequestBody VaccineCreateDTO vaccineCreateDTO) {
        return ResponseEntity.ok(vaccineService.updateVaccine(idVacuna, vaccineCreateDTO));
    }

    @DeleteMapping("/{idVacuna}")
    @Operation(
            summary = "Eliminar vacuna",
            description = "Elimina una vacuna del sistema. La operación es permanente e irreversible. " +
                    "Nota: Al eliminar una vacuna se eliminarán también todas las vacunas asociadas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Vacuna eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vacuna no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> deleteVaccine(
            @Parameter(description = "ID único de la vacuna a eliminar", example = "1", required = true)
            @PathVariable Long idVacuna) {
        vaccineService.deleteVaccine(idVacuna);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/especie/{idEspecie}")
    @Operation(
            summary = "Obtener vacunas por especie",
            description = "Retorna todas las vacunas que pertenecen a una especie específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vacunas de la especie obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VaccineResponseDTO.class)
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
    public ResponseEntity<List<VaccineResponseDTO>> getRacesBySpecie(
            @Parameter(description = "ID único de la especie", example = "1", required = true)
            @PathVariable Long idEspecie) {
        return ResponseEntity.ok(vaccineService.getVaccinesBySpecie(idEspecie));
    }

}
