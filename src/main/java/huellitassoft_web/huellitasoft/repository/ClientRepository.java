package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Client;
import huellitassoft_web.huellitasoft.enums.ClientState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder a datos de clientes.
 * 
 * Proporciona operaciones CRUD y consultas personalizadas para la entidad Client.
 * 
 * @author Backend Team
 * @version 1.0
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Busca un cliente por su documento de identidad.
     *
     * @param documentoIdentidad el documento de identidad del cliente
     * @return Optional con el cliente si existe, vacío en caso contrario
     */
    Optional<Client> findByDocumentoIdentidad(String documentoIdentidad);

    /**
     * Busca un cliente por su correo electrónico.
     *
     * @param email el correo electrónico del cliente
     * @return Optional con el cliente si existe, vacío en caso contrario
     */
    Optional<Client> findByEmail(String email);

    /**
     * Busca un cliente por el ID del usuario asociado.
     *
     * @param idUsuario el ID del usuario
     * @return Optional con el cliente si existe, vacío en caso contrario
     */
    Optional<Client> findByUsuario_IdUsuario(Integer idUsuario);

    /**
     * Verifica si existe un cliente con el documento especificado.
     *
     * @param documentoIdentidad el documento de identidad a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    /**
     * Verifica si existe un cliente con el email especificado.
     *
     * @param email el email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un cliente asociado al usuario especificado.
     *
     * @param idUsuario el ID del usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsuario_IdUsuario(Integer idUsuario);

    /**
     * Obtiene todos los clientes con un estado específico.
     *
     * @param estado el estado a filtrar
     * @return lista de clientes con el estado especificado
     */
    List<Client> findByEstado(ClientState estado);

    /**
     * Busca clientes por apellido (búsqueda parcial con LIKE).
     *
     * @param apellidos el apellido a buscar
     * @return lista de clientes que coinciden con el criterio
     */
    List<Client> findByApellidosContainingIgnoreCase(String apellidos);

    /**
     * Busca clientes por nombre (búsqueda parcial con LIKE).
     *
     * @param nombres el nombre a buscar
     * @return lista de clientes que coinciden con el criterio
     */
    List<Client> findByNombresContainingIgnoreCase(String nombres);
}
