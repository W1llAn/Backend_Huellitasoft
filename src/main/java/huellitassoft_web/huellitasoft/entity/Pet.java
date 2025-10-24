package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.Sex;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "mascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long idMascota;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha_nacimiento" )
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private Sex sexo;

    @Column (name = "estado")
    private Boolean estado;

    // Relación con la entidad Cliente
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Client cliente;

    // Relación con la entidad Raza
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_raza", nullable = false)
    private Race raza;
}
