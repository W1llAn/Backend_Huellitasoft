package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.ClientState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un cliente en el sistema.
 * Los clientes son dueños de mascotas que reciben servicios veterinarios.
 *
 * @author Backend Team
 * @version 1.0
 */
@Entity
@Table(name = "cliente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @Column(name = "id_cliente")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "documento_identidad", nullable = false, unique = true, length = 20)
    private String documentoIdentidad;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientState estado;
}
