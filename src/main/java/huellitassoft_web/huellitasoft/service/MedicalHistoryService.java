package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryCreateDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryDetailDTO;
import huellitassoft_web.huellitasoft.dto.medicalhistory.MedicalHistoryResponseDTO;
import huellitassoft_web.huellitasoft.enums.MedicalHistoryState;
import huellitassoft_web.huellitasoft.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Servicio para gestionar historiales clínicos de mascotas
 */
public interface MedicalHistoryService {

    /**
     * Crea un nuevo historial clínico para una mascota
     * @param medicalHistoryCreateDTO Datos del historial a crear
     * @return DTO de respuesta con el historial creado
     * @throws ResourceNotFoundException si la mascota no existe
     */
    MedicalHistoryResponseDTO createMedicalHistory(MedicalHistoryCreateDTO medicalHistoryCreateDTO);

    /**
     * Obtiene el detalle completo de un historial clínico con todas sus consultas y tratamientos
     * @param idHistoria ID del historial clínico
     * @return DTO detallado con consultas y tratamientos
     * @throws ResourceNotFoundException si el historial no existe
     */
    MedicalHistoryDetailDTO getMedicalHistoryDetail(Long idHistoria);

    /**
     * Obtiene el historial clínico de una mascota
     * @param idMascota ID de la mascota
     * @return DTO de respuesta del historial
     * @throws ResourceNotFoundException si la mascota no tiene historial
     */
    MedicalHistoryResponseDTO getMedicalHistoryByMascota(Long idMascota);

    /**
     * Obtiene todos los historiales clínicos de una mascota
     * @param idMascota ID de la mascota
     * @return Lista de DTOs de respuesta
     */
    List<MedicalHistoryResponseDTO> getAllMedicalHistoriesByMascota(Long idMascota);

    /**
     * Obtiene todos los historiales clínicos por estado
     * @param estado Estado del historial
     * @return Lista de DTOs de respuesta
     */
    List<MedicalHistoryResponseDTO> getMedicalHistoriesByEstado(MedicalHistoryState estado);

    /**
     * Obtiene un historial clínico por su ID
     * @param idHistoria ID del historial clínico
     * @return DTO de respuesta
     * @throws ResourceNotFoundException si el historial no existe
     */
    MedicalHistoryResponseDTO getMedicalHistoryById(Long idHistoria);

    /**
     * Actualiza el estado de un historial clínico
     * @param idHistoria ID del historial clínico
     * @param nuevoEstado Nuevo estado del historial
     * @return DTO de respuesta actualizado
     * @throws ResourceNotFoundException si el historial no existe
     */
    MedicalHistoryResponseDTO updateEstadoMedicalHistory(Long idHistoria, MedicalHistoryState nuevoEstado);

    /**
     * Elimina un historial clínico (eliminación lógica)
     * @param idHistoria ID del historial clínico
     * @throws ResourceNotFoundException si el historial no existe
     */
    void deleteMedicalHistory(Long idHistoria);

    /**
     * Obtiene todos los historiales clínicos
     * @return Lista de DTOs de respuesta
     */
    List<MedicalHistoryResponseDTO> getAllMedicalHistories();
}
