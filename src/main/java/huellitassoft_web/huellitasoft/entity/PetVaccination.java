package huellitassoft_web.huellitasoft.entity;


import huellitassoft_web.huellitasoft.enums.PetSchemeState;
import huellitassoft_web.huellitasoft.enums.VaccinationPetState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vacunacionmascota")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetVaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacunacion")
    private Long idVacunacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vacuna", nullable = false)
    private Vaccine vaccine;

    // Se setea desde backend (now), nunca desde el cliente
    @Column(name = "fecha_aplicada", nullable = false)
    private LocalDateTime fechaAplicada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private VaccinationPetState estado = VaccinationPetState.ACTIVA;

    // Usuario (veterinario) que aplicó la vacuna
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;
}
