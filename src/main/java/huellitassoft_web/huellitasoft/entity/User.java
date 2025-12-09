package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.UserRol;
import huellitassoft_web.huellitasoft.enums.UserState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "usuario")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRol rol;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "plan_contratado")
    private String planContratado;

    @Column(name = "imagen", columnDefinition = "TEXT")
    private String imagen;

    // Información Personal
    @Column
    private String nombres;

    @Column
    private String apellidos;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // Información Profesional
    @Column(length = 100)
    private String especialidad;

    @Column(name = "numero_licencia", length = 50)
    private String numeroLicencia;

    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "creado_por", nullable = true)
    private User creadoPor;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_sucursal", nullable = true)
    private Subsidiary sucursal;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
