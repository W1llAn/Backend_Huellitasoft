package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.client.ClientCreateDTO;
import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
import huellitassoft_web.huellitasoft.entity.Client;
import huellitassoft_web.huellitasoft.entity.User;
import huellitassoft_web.huellitasoft.enums.ClientState;
import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;
import huellitassoft_web.huellitasoft.repository.ClientRepository;
import huellitassoft_web.huellitasoft.repository.UserRepository;
import huellitassoft_web.huellitasoft.service.ClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de clientes.
 * 
 * Gestiona la lógica de negocio para operaciones de clientes.
 * Incluye validaciones, transacciones y mapeo de DTOs.
 * 
 * @author Backend Team
 * @version 1.0
 */
@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    private static final String CLIENT_NOT_FOUND = "Cliente con ID %d no encontrado";
    private static final String USUARIO_NOT_FOUND = "Usuario con ID %d no encontrado";
    private static final String USUARIO_NOT_CLIENT = "El usuario con ID %d no tiene rol de CLIENTE";
    private static final String DOCUMENTO_DUPLICADO = "Ya existe un cliente con el documento: %s";
    private static final String EMAIL_DUPLICADO = "Ya existe un cliente con el email: %s";
    private static final String USUARIO_YA_EXISTE_CLIENTE = "Ya existe un cliente asociado al usuario con ID %d";

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAllClients() {
        log.info("Obteniendo todos los clientes");
        return clientRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long idCliente) {
        log.info("Obteniendo cliente con ID: {}", idCliente);
        Client client = clientRepository.findById(idCliente)
                .orElseThrow(() -> {
                    log.error(CLIENT_NOT_FOUND, idCliente);
                    return new ResourceNotFoundException(String.format(CLIENT_NOT_FOUND, idCliente));
                });
        return mapToResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientByDocumento(String documentoIdentidad) {
        log.info("Obteniendo cliente por documento: {}", documentoIdentidad);
        Client client = clientRepository.findByDocumentoIdentidad(documentoIdentidad)
                .orElseThrow(() -> {
                    log.error("Cliente con documento {} no encontrado", documentoIdentidad);
                    return new ResourceNotFoundException("Cliente con documento " + documentoIdentidad + " no encontrado");
                });
        return mapToResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientByEmail(String email) {
        log.info("Obteniendo cliente por email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Cliente con email {} no encontrado", email);
                    return new ResourceNotFoundException("Cliente con email " + email + " no encontrado");
                });
        return mapToResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientByUsuarioId(Integer idUsuario) {
        log.info("Obteniendo cliente por ID de usuario: {}", idUsuario);
        Client client = clientRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> {
                    log.error("Cliente asociado al usuario con ID {} no encontrado", idUsuario);
                    return new ResourceNotFoundException("No existe cliente asociado al usuario con ID " + idUsuario);
                });
        return mapToResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getClientsByEstado(ClientState estado) {
        log.info("Obteniendo clientes con estado: {}", estado);
        return clientRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> searchByApellidos(String apellidos) {
        log.info("Buscando clientes por apellidos: {}", apellidos);
        return clientRepository.findByApellidosContainingIgnoreCase(apellidos)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> searchByNombres(String nombres) {
        log.info("Buscando clientes por nombres: {}", nombres);
        return clientRepository.findByNombresContainingIgnoreCase(nombres)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public ClientResponseDTO createClient(ClientCreateDTO clientCreateDTO) {
        log.info("Creando nuevo cliente con documento: {}", clientCreateDTO.getDocumentoIdentidad());

        // Validar que el usuario exista
        User usuario = userRepository.findById(clientCreateDTO.getIdUsuario())
                .orElseThrow(() -> {
                    log.error(USUARIO_NOT_FOUND, clientCreateDTO.getIdUsuario());
                    return new ResourceNotFoundException(String.format(USUARIO_NOT_FOUND, clientCreateDTO.getIdUsuario()));
                });

        // Validar que el usuario tenga rol CLIENTE
        if (!usuario.getRol().equals(UserRol.ROLE_CLIENTE)) {
            log.warn(USUARIO_NOT_CLIENT, clientCreateDTO.getIdUsuario());
            throw new ResourceAlreadyExistsException(String.format(USUARIO_NOT_CLIENT, clientCreateDTO.getIdUsuario()));
        }

        // Validar que no exista un cliente asociado a este usuario
        if (clientRepository.existsByUsuario_IdUsuario(clientCreateDTO.getIdUsuario())) {
            log.warn(USUARIO_YA_EXISTE_CLIENTE, clientCreateDTO.getIdUsuario());
            throw new ResourceAlreadyExistsException(String.format(USUARIO_YA_EXISTE_CLIENTE, clientCreateDTO.getIdUsuario()));
        }

        // Validar que no exista cliente con el mismo documento
        if (clientRepository.existsByDocumentoIdentidad(clientCreateDTO.getDocumentoIdentidad())) {
            log.warn("Intento de crear cliente con documento duplicado: {}", clientCreateDTO.getDocumentoIdentidad());
            throw new ResourceAlreadyExistsException(String.format(DOCUMENTO_DUPLICADO, clientCreateDTO.getDocumentoIdentidad()));
        }

        // Validar que no exista cliente con el mismo email
        if (clientRepository.existsByEmail(clientCreateDTO.getEmail())) {
            log.warn("Intento de crear cliente con email duplicado: {}", clientCreateDTO.getEmail());
            throw new ResourceAlreadyExistsException(String.format(EMAIL_DUPLICADO, clientCreateDTO.getEmail()));
        }

        // Crear el cliente
        Client client = Client.builder()
                .nombres(clientCreateDTO.getNombres())
                .apellidos(clientCreateDTO.getApellidos())
                .documentoIdentidad(clientCreateDTO.getDocumentoIdentidad())
                .email(clientCreateDTO.getEmail())
                .telefono(clientCreateDTO.getTelefono())
                .direccion(clientCreateDTO.getDireccion())
                .estado(ClientState.ACTIVO)
                .usuario(usuario)
                .build();

        Client savedClient = clientRepository.save(client);
        log.info("Cliente creado exitosamente con ID: {}", savedClient.getIdCliente());

        return mapToResponseDTO(savedClient);
    }

    @Override
    public ClientResponseDTO updateClient(Long idCliente, ClientCreateDTO clientCreateDTO) {
        log.info("Actualizando cliente con ID: {}", idCliente);

        Client client = clientRepository.findById(idCliente)
                .orElseThrow(() -> {
                    log.error(CLIENT_NOT_FOUND, idCliente);
                    return new ResourceNotFoundException(String.format(CLIENT_NOT_FOUND, idCliente));
                });

        // Si el ID del usuario cambió, validar que el nuevo usuario exista y sea CLIENTE
        if (!client.getUsuario().getIdUsuario().equals(clientCreateDTO.getIdUsuario())) {
            User nuevoUsuario = userRepository.findById(clientCreateDTO.getIdUsuario())
                    .orElseThrow(() -> {
                        log.error(USUARIO_NOT_FOUND, clientCreateDTO.getIdUsuario());
                        return new ResourceNotFoundException(String.format(USUARIO_NOT_FOUND, clientCreateDTO.getIdUsuario()));
                    });

            if (!nuevoUsuario.getRol().equals(UserRol.ROLE_CLIENTE)) {
                log.warn(USUARIO_NOT_CLIENT, clientCreateDTO.getIdUsuario());
                throw new ResourceAlreadyExistsException(String.format(USUARIO_NOT_CLIENT, clientCreateDTO.getIdUsuario()));
            }

            if (clientRepository.existsByUsuario_IdUsuario(clientCreateDTO.getIdUsuario())) {
                log.warn(USUARIO_YA_EXISTE_CLIENTE, clientCreateDTO.getIdUsuario());
                throw new ResourceAlreadyExistsException(String.format(USUARIO_YA_EXISTE_CLIENTE, clientCreateDTO.getIdUsuario()));
            }

            client.setUsuario(nuevoUsuario);
        }

        // Validar documento si cambió
        if (!client.getDocumentoIdentidad().equals(clientCreateDTO.getDocumentoIdentidad())) {
            if (clientRepository.existsByDocumentoIdentidad(clientCreateDTO.getDocumentoIdentidad())) {
                log.warn("Intento de actualizar con documento duplicado: {}", clientCreateDTO.getDocumentoIdentidad());
                throw new ResourceAlreadyExistsException(String.format(DOCUMENTO_DUPLICADO, clientCreateDTO.getDocumentoIdentidad()));
            }
            client.setDocumentoIdentidad(clientCreateDTO.getDocumentoIdentidad());
        }

        // Validar email si cambió
        if (!client.getEmail().equals(clientCreateDTO.getEmail())) {
            if (clientRepository.existsByEmail(clientCreateDTO.getEmail())) {
                log.warn("Intento de actualizar con email duplicado: {}", clientCreateDTO.getEmail());
                throw new ResourceAlreadyExistsException(String.format(EMAIL_DUPLICADO, clientCreateDTO.getEmail()));
            }
            client.setEmail(clientCreateDTO.getEmail());
        }

        // Actualizar otros campos
        client.setNombres(clientCreateDTO.getNombres());
        client.setApellidos(clientCreateDTO.getApellidos());
        client.setTelefono(clientCreateDTO.getTelefono());
        client.setDireccion(clientCreateDTO.getDireccion());

        Client updatedClient = clientRepository.save(client);
        log.info("Cliente actualizado exitosamente con ID: {}", updatedClient.getIdCliente());

        return mapToResponseDTO(updatedClient);
    }

    @Override
    public ClientResponseDTO changeClientState(Long idCliente, ClientState nuevoEstado) {
        log.info("Cambiando estado del cliente con ID: {} a: {}", idCliente, nuevoEstado);

        Client client = clientRepository.findById(idCliente)
                .orElseThrow(() -> {
                    log.error(CLIENT_NOT_FOUND, idCliente);
                    return new ResourceNotFoundException(String.format(CLIENT_NOT_FOUND, idCliente));
                });

        client.setEstado(nuevoEstado);
        Client updatedClient = clientRepository.save(client);

        log.info("Estado del cliente {} cambiado a: {}", idCliente, nuevoEstado);
        return mapToResponseDTO(updatedClient);
    }

    @Override
    public void deleteClient(Long idCliente) {
        log.info("Eliminando cliente con ID: {}", idCliente);

        if (!clientRepository.existsById(idCliente)) {
            log.error(CLIENT_NOT_FOUND, idCliente);
            throw new ResourceNotFoundException(String.format(CLIENT_NOT_FOUND, idCliente));
        }

        clientRepository.deleteById(idCliente);
        log.info("Cliente con ID {} eliminado exitosamente", idCliente);
    }

    /**
     * Mapea una entidad Client a ClientResponseDTO.
     *
     * @param client la entidad a mapear
     * @return ClientResponseDTO mapeado
     */
    private ClientResponseDTO mapToResponseDTO(Client client) {
        return ClientResponseDTO.builder()
                .idCliente(client.getIdCliente())
                .nombres(client.getNombres())
                .apellidos(client.getApellidos())
                .documentoIdentidad(client.getDocumentoIdentidad())
                .email(client.getEmail())
                .telefono(client.getTelefono())
                .direccion(client.getDireccion())
                .estado(client.getEstado())
                .idUsuario(client.getUsuario().getIdUsuario())
                .build();
    }
}
