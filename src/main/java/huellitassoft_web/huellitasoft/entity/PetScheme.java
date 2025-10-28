package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mascota_esquema",
        uniqueConstraints = {
                // Evita asignar el mismo esquema dos veces a la misma mascota
                @UniqueConstraint(name = "uk_mascota_esquema", columnNames = {"id_mascota", "id_esquema"})
        })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota_esquema")
    private Long idMascotaEsquema;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_esquema", nullable = false)
    private VaccinationScheme scheme;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private PetSchemeState estado;
}
