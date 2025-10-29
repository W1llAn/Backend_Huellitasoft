package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryRequestDTO;
import huellitassoft_web.huellitasoft.dto.Subsidiary.SubsidiaryResponseDTO;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import huellitassoft_web.huellitasoft.service.SubsidiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subsidiaries")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "Gestión de sucursales y horarios de atención")
public class SubsidiaryController {

    private final SubsidiaryService subsidiaryService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Crear sucursal", description = "Crea una nueva sucursal con sus horarios de atención")
    public ResponseEntity<SubsidiaryResponseDTO> createSubsidiary(@Valid @RequestBody SubsidiaryRequestDTO requestDTO) {
        return new ResponseEntity<>(subsidiaryService.createSubsidiary(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sucursal por ID", description = "Obtiene los detalles de una sucursal incluyendo sus horarios")
    public ResponseEntity<SubsidiaryResponseDTO> getSubsidiaryById(@PathVariable Long id) {
        return ResponseEntity.ok(subsidiaryService.getSubsidiaryById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas las sucursales", description = "Obtiene la lista completa de sucursales")
    public ResponseEntity<List<SubsidiaryResponseDTO>> getAllSubsidiaries() {
        return ResponseEntity.ok(subsidiaryService.getAllSubsidiaries());
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "Buscar sucursales por estado", description = "Filtra sucursales por estado (ACTIVO, INACTIVO, EN_MANTENIMIENTO)")
    public ResponseEntity<List<SubsidiaryResponseDTO>> getSubsidiariesByState(@PathVariable SubsidiaryState state) {
        return ResponseEntity.ok(subsidiaryService.getSubsidiariesByState(state));
    }

    @GetMapping("/manager/{idUsuario}")
    @Operation(summary = "Obtener sucursales por gestor", description = "Obtiene todas las sucursales gestionadas por un usuario específico")
    public ResponseEntity<List<SubsidiaryResponseDTO>> getSubsidiariesByManager(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(subsidiaryService.getSubsidiariesByManager(idUsuario));
    }

    @GetMapping("/manager/{idUsuario}/state/{state}")
    @Operation(summary = "Obtener sucursales por gestor y estado", description = "Obtiene sucursales de un gestor específico filtradas por estado")
    public ResponseEntity<List<SubsidiaryResponseDTO>> getSubsidiariesByManagerAndState(
            @PathVariable Long idUsuario,
            @PathVariable SubsidiaryState state) {
        return ResponseEntity.ok(subsidiaryService.getSubsidiariesByManagerAndState(idUsuario, state));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Actualizar sucursal", description = "Actualiza los datos de una sucursal y sus horarios")
    public ResponseEntity<SubsidiaryResponseDTO> updateSubsidiary(
            @PathVariable Long id,
            @Valid @RequestBody SubsidiaryRequestDTO requestDTO) {
        return ResponseEntity.ok(subsidiaryService.updateSubsidiary(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Eliminar sucursal", description = "Elimina una sucursal y todos sus horarios asociados")
    public ResponseEntity<Void> deleteSubsidiary(@PathVariable Long id) {
        subsidiaryService.deleteSubsidiary(id);
        return ResponseEntity.noContent().build();
    }
}