package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.DayOfWeek;
import huellitassoft_web.huellitasoft.enums.ShiftType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "horario_sucursal", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_sucursal", "dia_semana"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubsidiarySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Long idSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Subsidiary subsidiary;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_turno", nullable = false)
    private ShiftType shiftType;

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime startTime;

    @Column(name = "hora_salida_almuerzo")
    private LocalTime lunchStartTime;

    @Column(name = "hora_regreso_almuerzo")
    private LocalTime lunchEndTime;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime endTime;

    @Column(name = "esta_abierto", nullable = false)
    private Boolean isOpen = true;
}