package huellitassoft_web.huellitasoft.entity;

import jakarta.persistence.*;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "esquemavacunacion",
        uniqueConstraints = {
                // Evita duplicar el mismo número de dosis para una misma vacuna
                @UniqueConstraint(name = "uk_esquema_vacuna_dosis", columnNames = {"id_vacuna", "dosis_numero"})
        })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_esquema")
    private Long idEsquema;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vaccine vaccine;

    @Column(name = "dosis_numero", nullable = false)
    private Integer dosisNumero; // 1..MAX

    @Column(name = "edad_semanas", nullable = false)
    private Integer edadSemanas; // edad recomendada (en semanas)

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
