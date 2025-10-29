package huellitassoft_web.huellitasoft.entity;

import huellitassoft_web.huellitasoft.enums.NotificationTitle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notificacion")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "titulo", nullable = false, length = 50)
    private NotificationTitle titulo;

    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo; // CITA, VACUNA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Client cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario")
    private User veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita")
    private Appointment cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vacuna")
    private Vaccine vacuna;

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "enviada_email", nullable = false)
    private Boolean enviadaEmail = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_envio_email")
    private LocalDateTime fechaEnvioEmail;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}