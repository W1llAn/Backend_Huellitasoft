package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historialclinica")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia")
    private Long idHistoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet mascota;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private MedicalHistoryState estado;

    @OneToMany(mappedBy = "medicalHistory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Consultation> consultas = new ArrayList<>();

}
