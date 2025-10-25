package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.consultation.ConsultationCreateDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationResponseDTO;
import huellitassoft_web.huellitasoft.dto.consultation.ConsultationUpdateDTO;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar consultas veterinarias
 */
public interface ConsultationService {

    /**
     * Crea una nueva consulta veterinaria
     * @param consultationCreateDTO Datos de la consulta a crear
     * @return DTO de respuesta con la consulta creada
     * @throws ResourceNotFoundException si el historial o veterinario no existe
     */
    ConsultationResponseDTO createConsultation(ConsultationCreateDTO consultationCreateDTO);

    /**
     * Obtiene una consulta por su ID
     * @param idConsulta ID de la consulta
     * @return DTO de respuesta de la consulta
     * @throws ResourceNotFoundException si la consulta no existe
     */
    ConsultationResponseDTO getConsultationById(Long idConsulta);

    /**
     * Obtiene todas las consultas de un historial clínico
     * @param idHistoria ID del historial clínico
     * @return Lista de DTOs de respuesta
     */
    List<ConsultationResponseDTO> getConsultationsByHistoria(Long idHistoria);

    /**
     * Obtiene todas las consultas de una mascota
     * @param idMascota ID de la mascota
     * @return Lista de DTOs de respuesta
     */
    List<ConsultationResponseDTO> getConsultationsByMascota(Long idMascota);

    /**
     * Obtiene todas las consultas de un veterinario
     * @param idVeterinario ID del veterinario
     * @return Lista de DTOs de respuesta
     */
    List<ConsultationResponseDTO> getConsultationsByVeterinario(Integer idVeterinario);

    /**
     * Obtiene consultas en un rango de fechas
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @return Lista de DTOs de respuesta
     */
    List<ConsultationResponseDTO> getConsultationsByFechaHora(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Actualiza una consulta veterinaria
     * @param idConsulta ID de la consulta a actualizar
     * @param consultationUpdateDTO Datos a actualizar
     * @return DTO de respuesta actualizado
     * @throws ResourceNotFoundException si la consulta no existe
     */
    ConsultationResponseDTO updateConsultation(Long idConsulta, ConsultationUpdateDTO consultationUpdateDTO);

    /**
     * Elimina una consulta veterinaria
     * @param idConsulta ID de la consulta a eliminar
     * @throws ResourceNotFoundException si la consulta no existe
     */
    void deleteConsultation(Long idConsulta);

    /**
     * Obtiene todas las consultas
     * @return Lista de DTOs de respuesta
     */
    List<ConsultationResponseDTO> getAllConsultations();

    /**
     * Obtiene la consulta más reciente de una mascota
     * @param idMascota ID de la mascota
     * @return DTO de respuesta de la consulta más reciente
     * @throws ResourceNotFoundException si no hay consultas
     */
    ConsultationResponseDTO getLastConsultationByMascota(Long idMascota);
}
