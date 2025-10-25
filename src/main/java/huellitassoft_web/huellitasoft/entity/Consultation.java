package huellitassoft_web.huellitasoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consulta")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idConsulta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_historia", nullable = false)
    private MedicalHistory medicalHistory;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User veterinarian;

    @Column(name = "diagnostico", length = 1000)
    private String diagnostico;

    @Column(name = "indicaciones", length = 1000)
    private String indicaciones;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Treatment> tratamientos = new ArrayList<>();
}
