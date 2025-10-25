package huellitassoft_web.huellitasoft.controller;

import huellitassoft_web.huellitasoft.dto.client.ClientCreateDTO;
import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
import huellitassoft_web.huellitasoft.enums.ClientState;
import huellitassoft_web.huellitasoft.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar clientes.
 *
 * Proporciona endpoints para realizar operaciones CRUD sobre clientes,
 * búsquedas, filtros por estado y cambio de estado.
 *
 * @author Backend Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/clientes")
@AllArgsConstructor
@Slf4j
@Tag(
        name = "Clientes",
        description = "Endpoints para gestionar clientes. Los clientes son dueños de mascotas que reciben servicios veterinarios."
)
public class ClientController {

    private final ClientService clientService;

    /**
     * Obtiene todos los clientes registrados.
     *
     * @return lista de todos los clientes
     */
    @GetMapping
    @Operation(
            summary = "Obtener todos los clientes",
            description = "Retorna una lista con todos los clientes registrados en el sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de clientes obtenida exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientResponseDTO.class)
            )
    )
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        log.info("GET /api/clientes - Obteniendo todos los clientes");
        List<ClientResponseDTO> clientes = clientService.getAllClients();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtiene un cliente específico por su ID.
     *
     * @param idCliente el ID del cliente
     * @return datos del cliente solicitado
     */
    @GetMapping("/{idCliente}")
    @Operation(
            summary = "Obtener cliente por ID",
            description = "Retorna los datos de un cliente específico usando su ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado"
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autorizado"
    )
    public ResponseEntity<ClientResponseDTO> getClientById(
            @PathVariable
            @Parameter(description = "ID del cliente", example = "1")
            Long idCliente
    ) {
        log.info("GET /api/clientes/{} - Obteniendo cliente por ID", idCliente);
        ClientResponseDTO cliente = clientService.getClientById(idCliente);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Obtiene un cliente por su documento de identidad.
     *
     * @param documento el documento de identidad
     * @return datos del cliente
     */
    @GetMapping("/documento/{documento}")
    @Operation(
            summary = "Obtener cliente por documento de identidad",
            description = "Busca y retorna un cliente usando su documento de identidad"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente con este documento no encontrado"
    )
    public ResponseEntity<ClientResponseDTO> getClientByDocumento(
            @PathVariable
            @Parameter(description = "Documento de identidad", example = "1234567890")
            String documento
    ) {
        log.info("GET /api/clientes/documento/{} - Obteniendo cliente por documento", documento);
        ClientResponseDTO cliente = clientService.getClientByDocumento(documento);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Obtiene un cliente por su correo electrónico.
     *
     * @param email el correo electrónico
     * @return datos del cliente
     */
    @GetMapping("/email/{email}")
    @Operation(
            summary = "Obtener cliente por email",
            description = "Busca y retorna un cliente usando su correo electrónico"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente con este email no encontrado"
    )
    public ResponseEntity<ClientResponseDTO> getClientByEmail(
            @PathVariable
            @Parameter(description = "Correo electrónico", example = "juan.garcia@example.com")
            String email
    ) {
        log.info("GET /api/clientes/email/{} - Obteniendo cliente por email", email);
        ClientResponseDTO cliente = clientService.getClientByEmail(email);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Obtiene un cliente por el ID del usuario asociado.
     *
     * @param idUsuario el ID del usuario
     * @return datos del cliente
     */
    @GetMapping("/usuario/{idUsuario}")
    @Operation(
            summary = "Obtener cliente por ID de usuario",
            description = "Busca y retorna un cliente usando el ID del usuario asociado"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente asociado a este usuario no encontrado"
    )
    public ResponseEntity<ClientResponseDTO> getClientByUsuarioId(
            @PathVariable
            @Parameter(description = "ID del usuario", example = "5")
            Long idUsuario
    ) {
        log.info("GET /api/clientes/usuario/{} - Obteniendo cliente por ID de usuario", idUsuario);
        ClientResponseDTO cliente = clientService.getClientByUsuarioId(idUsuario);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Obtiene todos los clientes con un estado específico.
     *
     * @param estado el estado a filtrar (ACTIVO, INACTIVO)
     * @return lista de clientes con ese estado
     */
    @GetMapping("/estado/{estado}")
    @Operation(
            summary = "Obtener clientes por estado",
            description = "Retorna todos los clientes que tienen el estado especificado (ACTIVO o INACTIVO)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de clientes obtenida exitosamente"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Estado inválido. Debe ser ACTIVO o INACTIVO"
    )
    public ResponseEntity<List<ClientResponseDTO>> getClientsByEstado(
            @PathVariable
            @Parameter(description = "Estado del cliente", example = "ACTIVO")
            ClientState estado
    ) {
        log.info("GET /api/clientes/estado/{} - Obteniendo clientes por estado", estado);
        List<ClientResponseDTO> clientes = clientService.getClientsByEstado(estado);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca clientes por apellido (búsqueda parcial).
     *
     * @param apellidos el apellido a buscar
     * @return lista de clientes que coinciden
     */
    @GetMapping("/buscar/apellidos")
    @Operation(
            summary = "Buscar clientes por apellido",
            description = "Busca clientes cuyo apellido contenga el texto especificado (búsqueda parcial, no sensible a mayúsculas)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Búsqueda realizada exitosamente"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Parámetro de búsqueda inválido"
    )
    public ResponseEntity<List<ClientResponseDTO>> searchByApellidos(
            @RequestParam
            @Parameter(description = "Texto a buscar en apellidos", example = "García")
            String apellidos
    ) {
        log.info("GET /api/clientes/buscar/apellidos?apellidos={} - Buscando por apellidos", apellidos);
        List<ClientResponseDTO> clientes = clientService.searchByApellidos(apellidos);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca clientes por nombre (búsqueda parcial).
     *
     * @param nombres el nombre a buscar
     * @return lista de clientes que coinciden
     */
    @GetMapping("/buscar/nombres")
    @Operation(
            summary = "Buscar clientes por nombre",
            description = "Busca clientes cuyo nombre contenga el texto especificado (búsqueda parcial, no sensible a mayúsculas)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Búsqueda realizada exitosamente"
    )
    public ResponseEntity<List<ClientResponseDTO>> searchByNombres(
            @RequestParam
            @Parameter(description = "Texto a buscar en nombres", example = "Juan")
            String nombres
    ) {
        log.info("GET /api/clientes/buscar/nombres?nombres={} - Buscando por nombres", nombres);
        List<ClientResponseDTO> clientes = clientService.searchByNombres(nombres);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Crea un nuevo cliente.
     *
     * @param clientCreateDTO datos del cliente a crear
     * @return cliente creado
     */
    @PostMapping
    @Operation(
            summary = "Crear nuevo cliente",
            description = "Crea un nuevo cliente en el sistema. El cliente se crea en estado ACTIVO por defecto"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Cliente creado exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o incompletos"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Conflicto: Ya existe cliente con ese documento o email"
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Se requiere token JWT"
    )
    public ResponseEntity<ClientResponseDTO> createClient(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo cliente",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientCreateDTO.class)
                    )
            )
            ClientCreateDTO clientCreateDTO
    ) {
        log.info("POST /api/clientes - Creando nuevo cliente con documento: {}", clientCreateDTO.getDocumentoIdentidad());
        ClientResponseDTO cliente = clientService.createClient(clientCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    /**
     * Actualiza un cliente existente.
     *
     * @param idCliente el ID del cliente a actualizar
     * @param clientCreateDTO los nuevos datos del cliente
     * @return cliente actualizado
     */
    @PutMapping("/{idCliente}")
    @Operation(
            summary = "Actualizar cliente existente",
            description = "Actualiza los datos de un cliente existente. El estado no se puede cambiar con este endpoint, usar /cambiar-estado para eso"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cliente actualizado exitosamente",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Ya existe otro cliente con ese documento o email"
    )
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable
            @Parameter(description = "ID del cliente a actualizar", example = "1")
            Long idCliente,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del cliente",
                    required = true
            )
            ClientCreateDTO clientCreateDTO
    ) {
        log.info("PUT /api/clientes/{} - Actualizando cliente", idCliente);
        ClientResponseDTO cliente = clientService.updateClient(idCliente, clientCreateDTO);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Cambia el estado de un cliente (ACTIVO/INACTIVO).
     *
     * @param idCliente el ID del cliente
     * @param nuevoEstado el nuevo estado
     * @return cliente con estado actualizado
     */
    @PatchMapping("/{idCliente}/cambiar-estado")
    @Operation(
            summary = "Cambiar estado del cliente",
            description = "Cambia el estado de un cliente entre ACTIVO e INACTIVO"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado exitosamente"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Estado inválido"
    )
    public ResponseEntity<ClientResponseDTO> changeClientState(
            @PathVariable
            @Parameter(description = "ID del cliente", example = "1")
            Long idCliente,
            @RequestParam
            @Parameter(description = "Nuevo estado", example = "INACTIVO")
            ClientState nuevoEstado
    ) {
        log.info("PATCH /api/clientes/{}/cambiar-estado - Cambiando estado a: {}", idCliente, nuevoEstado);
        ClientResponseDTO cliente = clientService.changeClientState(idCliente, nuevoEstado);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Elimina un cliente del sistema.
     *
     * @param idCliente el ID del cliente a eliminar
     * @return respuesta vacía con estado 204
     */
    @DeleteMapping("/{idCliente}")
    @Operation(
            summary = "Eliminar cliente",
            description = "Elimina un cliente del sistema. Esta acción es irreversible"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Cliente eliminado exitosamente"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado"
    )
    @ApiResponse(
            responseCode = "401",
            description = "No autorizado"
    )
    public ResponseEntity<Void> deleteClient(
            @PathVariable
            @Parameter(description = "ID del cliente a eliminar", example = "1")
            Long idCliente
    ) {
        log.info("DELETE /api/clientes/{} - Eliminando cliente", idCliente);
        clientService.deleteClient(idCliente);
        return ResponseEntity.noContent().build();
    }
}
