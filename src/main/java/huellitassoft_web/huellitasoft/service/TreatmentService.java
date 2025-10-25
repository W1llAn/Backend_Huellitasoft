package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.treatment.TreatmentCreateDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentResponseDTO;
import huellitassoft_web.huellitasoft.dto.treatment.TreatmentUpdateDTO;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Servicio para gestionar tratamientos de mascotas
 */
public interface TreatmentService {

    /**
     * Crea un nuevo tratamiento
     * @param treatmentCreateDTO Datos del tratamiento a crear
     * @return DTO de respuesta con el tratamiento creado
     * @throws ResourceNotFoundException si la consulta, mascota o historial no existe
     */
    TreatmentResponseDTO createTreatment(TreatmentCreateDTO treatmentCreateDTO);

    /**
     * Obtiene un tratamiento por su ID
     * @param idTratamiento ID del tratamiento
     * @return DTO de respuesta del tratamiento
     * @throws ResourceNotFoundException si el tratamiento no existe
     */
    TreatmentResponseDTO getTreatmentById(Long idTratamiento);

    /**
     * Obtiene todos los tratamientos de una consulta
     * @param idConsulta ID de la consulta
     * @return Lista de DTOs de respuesta
     */
    List<TreatmentResponseDTO> getTreatmentsByConsultation(Long idConsulta);


    /**
     * Obtiene todos los tratamientos de una mascota
     * @param idMascota ID de la mascota
     * @return Lista de DTOs de respuesta
     */
    List<TreatmentResponseDTO> getTreatmentsByMascota(Long idMascota);

    /**
     * Obtiene todos los tratamientos activos de una mascota
     * @param idMascota ID de la mascota
     * @return Lista de DTOs de respuesta
     */
    List<TreatmentResponseDTO> getActiveTreatmentsByMascota(Long idMascota);

    /**
     * Actualiza un tratamiento
     * @param idTratamiento ID del tratamiento a actualizar
     * @param treatmentUpdateDTO Datos a actualizar
     * @return DTO de respuesta actualizado
     * @throws ResourceNotFoundException si el tratamiento no existe
     */
    TreatmentResponseDTO updateTreatment(Long idTratamiento, TreatmentUpdateDTO treatmentUpdateDTO);

    /**
     * Elimina un tratamiento
     * @param idTratamiento ID del tratamiento a eliminar
     * @throws ResourceNotFoundException si el tratamiento no existe
     */
    void deleteTreatment(Long idTratamiento);

    /**
     * Obtiene todos los tratamientos
     * @return Lista de DTOs de respuesta
     */
    List<TreatmentResponseDTO> getAllTreatments();

    /**
     * Cambia el estado de un tratamiento
     * @param idTratamiento ID del tratamiento
     * @param nuevoEstado Nuevo estado
     * @return DTO de respuesta actualizado
     * @throws ResourceNotFoundException si el tratamiento no existe
     */
    TreatmentResponseDTO updateTreatmentStatus(Long idTratamiento, Boolean nuevoEstado);
}
