package huellitassoft_web.huellitasoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tratamiento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Long idTratamiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "estado", nullable = false)
    private Boolean estado;
}
