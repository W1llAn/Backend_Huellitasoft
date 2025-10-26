package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Appointment;
import huellitassoft_web.huellitasoft.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Buscar por cliente
    List<Appointment> findByClienteIdCliente(Long idCliente);

    // Buscar por mascota
    List<Appointment> findByMascotaIdMascota(Long idMascota);

    // Buscar por usuario
    List<Appointment> findByUsuarioIdUsuario(Long idUsuario);

    // Buscar por sucursal
    List<Appointment> findBySucursalIdSubsidiary(Long idSubsidiary);

    // Buscar por estado
    List<Appointment> findByEstado(EstadoCita estado);

    // Buscar por rango de fecha y hora
    List<Appointment> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    // Validar si existe una cita en el mismo horario para la misma mascota
    boolean existsByMascotaIdMascotaAndFechaHora(Long idMascota, LocalDateTime fechaHora);

    //Validar que no exista una misma cita en mismo horario para veterinario
    boolean existsByUsuarioIdUsuarioAndFechaHora(Long idUsuario, LocalDateTime fechaHora);

    //  Contar citas por cliente
    long countByClienteIdCliente(Long idCliente);

    //  Contar citas por estado
    long countByEstado(EstadoCita estado);
}
