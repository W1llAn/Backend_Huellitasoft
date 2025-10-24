package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.client.ClientCreateDTO;
import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
import huellitassoft_web.huellitasoft.enums.ClientState;

import java.util.List;

/**
 * Servicio para gestionar operaciones relacionadas con clientes.
 * 
 * Define el contrato para todas las operaciones de negocio sobre clientes.
 * 
 * @author Backend Team
 * @version 1.0
 */
public interface ClientService {

    /**
     * Obtiene todos los clientes.
     *
     * @return lista de todos los clientes
     */
    List<ClientResponseDTO> getAllClients();

    /**
     * Obtiene un cliente por su ID.
     *
     * @param idCliente el ID del cliente
     * @return DTO con la información del cliente
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si el cliente no existe
     */
    ClientResponseDTO getClientById(Long idCliente);

    /**
     * Obtiene un cliente por su documento de identidad.
     *
     * @param documentoIdentidad el documento de identidad del cliente
     * @return DTO con la información del cliente
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si el cliente no existe
     */
    ClientResponseDTO getClientByDocumento(String documentoIdentidad);

    /**
     * Obtiene un cliente por su correo electrónico.
     *
     * @param email el correo electrónico del cliente
     * @return DTO con la información del cliente
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si el cliente no existe
     */
    ClientResponseDTO getClientByEmail(String email);

    /**
     * Obtiene todos los clientes con un estado específico.
     *
     * @param estado el estado a filtrar
     * @return lista de clientes con el estado especificado
     */
    List<ClientResponseDTO> getClientsByEstado(ClientState estado);

    /**
     * Busca clientes por apellido.
     *
     * @param apellidos el apellido a buscar
     * @return lista de clientes que coinciden
     */
    List<ClientResponseDTO> searchByApellidos(String apellidos);

    /**
     * Busca clientes por nombre.
     *
     * @param nombres el nombre a buscar
     * @return lista de clientes que coinciden
     */
    List<ClientResponseDTO> searchByNombres(String nombres);

    /**
     * Crea un nuevo cliente.
     *
     * @param clientCreateDTO los datos del cliente a crear
     * @return DTO con la información del cliente creado
     * @throws huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException si el documento o email ya existe
     */
    ClientResponseDTO createClient(ClientCreateDTO clientCreateDTO);

    /**
     * Actualiza un cliente existente.
     *
     * @param idCliente el ID del cliente a actualizar
     * @param clientCreateDTO los nuevos datos del cliente
     * @return DTO con la información actualizada del cliente
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si el cliente no existe
     * @throws huellitassoft_web.huellitasoft.exception.ResourceAlreadyExistsException si el documento o email ya existe
     */
    ClientResponseDTO updateClient(Long idCliente, ClientCreateDTO clientCreateDTO);

    /**
     * Cambia el estado de un cliente.
     *
     * @param idCliente el ID del cliente
     * @param nuevoEstado el nuevo estado
     * @return DTO con la información del cliente actualizado
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si el cliente no existe
     */
    ClientResponseDTO changeClientState(Long idCliente, ClientState nuevoEstado);

    /**
     * Elimina un cliente.
     *
     * @param idCliente el ID del cliente a eliminar
     * @throws huellitassoft_web.huellitasoft.exception.ResourceNotFoundException si el cliente no existe
     */
    void deleteClient(Long idCliente);
}
