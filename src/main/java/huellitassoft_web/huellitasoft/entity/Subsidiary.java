package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sucursal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subsidiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long idSubsidiary;

    @Column(name = "nombre", nullable = false, length = 100)
    private String name;

    @Column(name = "direccion", nullable = false, length = 255)
    private String address;

    @Column(name = "plan_contratado", nullable = false, length = 100)
    private String contractedPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private SubsidiaryState state;

    @OneToMany(mappedBy = "subsidiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubsidiarySchedule> schedules = new ArrayList<>();
}