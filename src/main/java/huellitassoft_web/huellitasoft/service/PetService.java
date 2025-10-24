package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.dto.pet.PetCreateDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetResponseDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetUpdateDTO;
import huellitassoft_web.huellitasoft.enums.Sex;

import java.util.List;

public interface PetService {
    // Obtener Todas las mascotas
    List<PetResponseDTO> getAllPet();

    //Obtener una mascota por su id
    PetResponseDTO getPetById(Long id);

    // Obtener todas las mascotas de un cliente
    List<PetResponseDTO> getPetsByClient(Long idCliente);

    // Crear una nueva mascota
    PetResponseDTO createPet(PetCreateDTO petCreateDTO);

    // Actualizar una mascota
    PetResponseDTO updatePet(Long id, PetUpdateDTO petUpdateDTO);

    // Eliminar una mascota por ID
    void deletePet(Long id);

    // Obtener todas las mascotas de una raza
    List<PetResponseDTO> getPetsByRace(Long idRaza);

    // Obtener mascotas por sexo
    List<PetResponseDTO> getPetsBySex(Sex sexo);

    // Obtener mascotas por estado (activo/inactivo)
    List<PetResponseDTO> getPetsByState(Boolean estado);

    // Contar las mascotas de un cliente
    long countPetsByClient(Long idCliente);


    // Eliminar todas las mascotas de un cliente
    void deletePetsByClient(Long idCliente);
}
