package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationCreateDTO;
import huellitassoft_web.huellitasoft.dto.petVaccination.PetVaccinationResponseDTO;
import huellitassoft_web.huellitasoft.service.PetVaccinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/vacunacion-mascota")
@Tag(name = "Vacunación de Mascotas", description = "Registro y consulta de vacunas aplicadas a mascotas")
@AllArgsConstructor
public class PetVaccinationController {

    private final PetVaccinationService service;

    @PostMapping
    @Operation(summary = "Registrar vacunación (fecha y veterinario se obtienen desde el backend)")
    public ResponseEntity<PetVaccinationResponseDTO> create(@Valid @RequestBody PetVaccinationCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        PetVaccinationResponseDTO resp = service.create(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @DeleteMapping("/{idVacunacion}")
    @Operation(summary = "Eliminar registro de vacunación")
    public ResponseEntity<Void> delete(@PathVariable Long idVacunacion) {
        service.delete(idVacunacion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idVacunacion}")
    @Operation(summary = "Obtener vacunación por ID")
    public ResponseEntity<PetVaccinationResponseDTO> getById(@PathVariable Long idVacunacion) {
        return ResponseEntity.ok(service.getById(idVacunacion));
    }

    @GetMapping("/mascota/{idMascota}")
    @Operation(summary = "Listar vacunaciones por mascota")
    public ResponseEntity<List<PetVaccinationResponseDTO>> getByPet(@PathVariable Long idMascota) {
        return ResponseEntity.ok(service.getByPet(idMascota));
    }

    @GetMapping("/vacuna/{idVacuna}")
    @Operation(summary = "Listar vacunaciones por vacuna")
    public ResponseEntity<List<PetVaccinationResponseDTO>> getByVaccine(@PathVariable Long idVacuna) {
        return ResponseEntity.ok(service.getByVaccine(idVacuna));
    }

    @GetMapping("/veterinario/{idUsuario}")
    @Operation(summary = "Listar vacunaciones aplicadas por un veterinario")
    public ResponseEntity<List<PetVaccinationResponseDTO>> getByVet(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.getByVeterinarian(idUsuario));
    }
}
