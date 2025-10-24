package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.pet.PetCreateDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetResponseDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetUpdateDTO;
import huellitassoft_web.huellitasoft.enums.Sex;
import huellitassoft_web.huellitasoft.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "Endpoints para la gestión de mascotas en el sistema veterinario")

public class PetController {
    private final PetService petService;

    //Crear nueva mascota
    @Operation(summary = "Registrar una nueva mascota", description = "Crea una nueva mascota asociada a un cliente y una raza.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente",
                    content = @Content(schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "404", description = "Cliente o raza no encontrados")
    })
    @PostMapping
    public ResponseEntity<PetResponseDTO> createPet(
            @Valid @RequestBody PetCreateDTO petCreateDTO) {
        PetResponseDTO response = petService.createPet(petCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Obtener todas las mascotas
    @Operation(summary = "Listar todas las mascotas", description = "Retorna una lista con todas las mascotas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPet());
    }

    //Obtener mascota por ID
    @Operation(summary = "Buscar mascota por ID", description = "Obtiene la información detallada de una mascota específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota encontrada"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getPetById(
            @Parameter(description = "ID de la mascota a buscar", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    //Obtener mascotas por cliente
    @Operation(summary = "Listar mascotas por cliente", description = "Obtiene todas las mascotas asociadas a un cliente específico.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PetResponseDTO>> getPetsByClient(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long idCliente) {
        return ResponseEntity.ok(petService.getPetsByClient(idCliente));
    }

    //Actualizar una mascota
    @Operation(summary = "Actualizar datos de una mascota", description = "Permite modificar los datos de una mascota existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = PetResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Mascota, cliente o raza no encontrados")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> updatePet(
            @Parameter(description = "ID de la mascota a actualizar", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PetUpdateDTO petUpdateDTO) {
        return ResponseEntity.ok(petService.updatePet(id, petUpdateDTO));
    }

    //Eliminar mascota por ID
    @Operation(summary = "Eliminar mascota por ID", description = "Elimina una mascota del sistema por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mascota eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "ID de la mascota a eliminar", required = true)
            @PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    //Eliminar todas las mascotas de un cliente
    @Operation(summary = "Eliminar todas las mascotas de un cliente", description = "Elimina todas las mascotas asociadas a un cliente específico.")
    @ApiResponse(responseCode = "204", description = "Mascotas eliminadas correctamente")
    @DeleteMapping("/cliente/{idCliente}")
    public ResponseEntity<Void> deletePetsByClient(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long idCliente) {
        petService.deletePetsByClient(idCliente);
        return ResponseEntity.noContent().build();
    }

    //Buscar por raza
    @Operation(summary = "Buscar mascotas por raza", description = "Obtiene todas las mascotas que pertenecen a una raza específica.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/raza/{idRaza}")
    public ResponseEntity<List<PetResponseDTO>> getPetsByRace(
            @Parameter(description = "ID de la raza", required = true)
            @PathVariable Long idRaza) {
        return ResponseEntity.ok(petService.getPetsByRace(idRaza));
    }

    //Buscar por sexo
    @Operation(summary = "Buscar mascotas por sexo", description = "Obtiene todas las mascotas filtradas por sexo (MACHO o HEMBRA).")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/sexo/{sexo}")
    public ResponseEntity<List<PetResponseDTO>> getPetsBySex(
            @Parameter(description = "Sexo de la mascota (MACHO o HEMBRA)", required = true)
            @PathVariable Sex sexo) {
        return ResponseEntity.ok(petService.getPetsBySex(sexo));
    }

    //Buscar por estado
    @Operation(summary = "Buscar mascotas por estado", description = "Obtiene todas las mascotas activas o inactivas según su estado.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PetResponseDTO>> getPetsByState(
            @Parameter(description = "Estado de la mascota (true = activa, false = inactiva)", required = true)
            @PathVariable Boolean estado) {
        return ResponseEntity.ok(petService.getPetsByState(estado));
    }

    //Contar mascotas por cliente
    @Operation(summary = "Contar mascotas por cliente", description = "Cuenta cuántas mascotas tiene registrado un cliente.")
    @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    @GetMapping("/cliente/{idCliente}/count")
    public ResponseEntity<Long> countPetsByClient(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long idCliente) {
        return ResponseEntity.ok(petService.countPetsByClient(idCliente));
    }
}
