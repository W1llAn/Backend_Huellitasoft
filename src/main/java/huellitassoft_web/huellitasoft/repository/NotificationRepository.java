package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByClienteIdClienteOrderByFechaCreacionDesc(Long idCliente);
    List<Notification> findByVeterinarioIdUsuarioOrderByFechaCreacionDesc(Long idVeterinario);
    List<Notification> findByLeidaFalseAndClienteIdCliente(Long idCliente);
    List<Notification> findByLeidaFalseAndVeterinarioIdUsuario(Long idVeterinario);
    List<Notification> findByEnviadaEmailFalse();
    List<Notification> findByFechaCreacionBetween(LocalDateTime start, LocalDateTime end);
}