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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consultation consultation;

    @Column(name = "descripcion", length = 500)
    private String description;

    @Column(name = "medicamento", length = 200)
    private String medication;

    @Column(name = "dosis", length = 100)
    private String dosage;

    @Column(name = "frecuencia", length = 100)
    private String frequency;

    @Column(name = "duracion_dias")
    private Integer durationDays;

    @Column(name = "observaciones", length = 500)
    private String observations;

    @Column(name = "estado")
    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota")
    private Pet pet;
}
