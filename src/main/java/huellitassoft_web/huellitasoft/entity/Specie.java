package huellitassoft_web.huellitasoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@Table(name = "especie")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Specie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especie")
    private Long idEspecie;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @OneToMany(mappedBy = "specie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Race> razas;
}
