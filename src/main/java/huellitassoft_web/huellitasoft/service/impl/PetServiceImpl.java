package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.pet.PetCreateDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetResponseDTO;
import huellitassoft_web.huellitasoft.dto.pet.PetUpdateDTO;
import huellitassoft_web.huellitasoft.entity.Client;
import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.entity.Race;
import huellitassoft_web.huellitasoft.enums.Sex;
import huellitassoft_web.huellitasoft.repository.ClientRepository;
import huellitassoft_web.huellitasoft.repository.PetRepository;
import huellitassoft_web.huellitasoft.repository.RaceRepository;
import huellitassoft_web.huellitasoft.service.PetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PetServiceImpl implements PetService {
    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    private final RaceRepository raceRepository;

    private PetResponseDTO convertToDTO(Pet pet) {
        return PetResponseDTO.builder()
                .idMascota(pet.getIdMascota())
                .nombre(pet.getNombre())
                .fechaNacimiento(pet.getFechaNacimiento())
                .sexo(pet.getSexo())
                .estado(pet.getEstado())
                .idCliente(pet.getCliente().getIdCliente())
                .idRaza(pet.getRaza().getIdRaza())
                .cliente(pet.getCliente())
                .raza(pet.getRaza())
                .build();
    }

    @Override
    public List<PetResponseDTO> getAllPet() {
        return petRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PetResponseDTO getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + id));
        return convertToDTO(pet);

    }

    @Override
    public List<PetResponseDTO> getPetsByClient(Long idCliente) {
        return petRepository.findByClienteIdCliente(idCliente)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PetResponseDTO createPet(PetCreateDTO petCreateDTO) {
        Client cliente = clientRepository.findById(petCreateDTO.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + petCreateDTO.getIdCliente()));

        Race raza = raceRepository.findById(petCreateDTO.getIdRaza())
                .orElseThrow(() -> new EntityNotFoundException("Raza no encontrada con id: " + petCreateDTO.getIdRaza()));

        Pet pet = Pet.builder()
                .nombre(petCreateDTO.getNombre())
                .fechaNacimiento(petCreateDTO.getFechaNacimiento())
                .sexo(petCreateDTO.getSexo())
                .estado(petCreateDTO.getEstado())
                .cliente(cliente)
                .raza(raza)
                .build();

        Pet savedPet = petRepository.save(pet);
        log.info("Mascota creada correctamente con ID: {}", savedPet.getIdMascota());
        return convertToDTO(savedPet);
    }

    @Override
    public PetResponseDTO updatePet(Long id, PetUpdateDTO petUpdateDTO) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + id));

        if (petUpdateDTO.getNombre() != null) pet.setNombre(petUpdateDTO.getNombre());
        if (petUpdateDTO.getFechaNacimiento() != null) pet.setFechaNacimiento(petUpdateDTO.getFechaNacimiento());
        if (petUpdateDTO.getSexo() != null) pet.setSexo(petUpdateDTO.getSexo());
        if (petUpdateDTO.getEstado() != null) pet.setEstado(petUpdateDTO.getEstado());

        if (petUpdateDTO.getIdCliente() != null) {
            Client cliente = clientRepository.findById(petUpdateDTO.getIdCliente())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + petUpdateDTO.getIdCliente()));
            pet.setCliente(cliente);
        }

        if (petUpdateDTO.getIdRaza() != null) {
            Race raza = raceRepository.findById(petUpdateDTO.getIdRaza())
                    .orElseThrow(() -> new EntityNotFoundException("Raza no encontrada con id: " + petUpdateDTO.getIdRaza()));
            pet.setRaza(raza);
        }

        Pet updatedPet = petRepository.save(pet);
        log.info("Mascota actualizada correctamente con ID: {}", updatedPet.getIdMascota());
        return convertToDTO(updatedPet);
    }

    @Override
    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new EntityNotFoundException("Mascota no encontrada con id: " + id);
        }
        petRepository.deleteById(id);
        log.info("Mascota eliminada con id: {}", id);
    }

    @Override
    public List<PetResponseDTO> getPetsByRace(Long idRaza) {
        return petRepository.findByRazaIdRaza(idRaza)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetResponseDTO> getPetsBySex(Sex sexo) {
        return petRepository.findBySexo(sexo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetResponseDTO> getPetsByState(Boolean estado) {
        return petRepository.findByEstado(estado)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countPetsByClient(Long idCliente) {
        return petRepository.countByClienteIdCliente(idCliente);
    }

    @Override
    public void deletePetsByClient(Long idCliente) {
        petRepository.deleteByClienteIdCliente(idCliente);
        log.info("Mascotas del cliente {} eliminadas correctamente", idCliente);

    }
}
