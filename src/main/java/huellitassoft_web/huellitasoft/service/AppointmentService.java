package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.appointment.AppointmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.appointment.AppointmentUpdateDTO;
import huellitassoft_web.huellitasoft.enums.EstadoCita;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    // Crear una nueva cita
    AppointmentResponseDTO create(AppointmentCreateDTO dto);

    // Actualizar una cita existente
    AppointmentResponseDTO update(Long idCita, AppointmentUpdateDTO dto);

    // Eliminar una cita por ID
    void delete(Long idCita);

    // Obtener una cita por ID
    Optional<AppointmentResponseDTO> findById(Long idCita);

    // Listar todas las citas
    List<AppointmentResponseDTO> findAll();

    // Buscar citas por cliente
    List<AppointmentResponseDTO> findByCliente(Long idCliente);

    // Buscar citas por mascota
    List<AppointmentResponseDTO> findByMascota(Long idMascota);

    // Buscar citas por usuario (veterinario)
    List<AppointmentResponseDTO> findByUsuario(Long idUsuario);

    // Buscar citas por sucursal
    List<AppointmentResponseDTO> findBySucursal(Long idSucursal);

    // Buscar citas por estado (pendiente, confirmada, etc.)
    List<AppointmentResponseDTO> findByEstado(EstadoCita estado);

    // Buscar citas en un rango de fechas
    List<AppointmentResponseDTO> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Consultar si ya existe una cita en la misma fecha/hora para una mascota
    boolean existsByMascotaAndFechaHora(Long idMascota, LocalDate fecha, int hora, int minuto);

    //Contar cuántas citas tiene un cliente
    long countByCliente(Long idCliente);

    // Contar cuántas citas están en determinado estado
    long countByEstado(EstadoCita estado);
}
