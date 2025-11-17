package huellitassoft_web.huellitasoft.service.impl;

import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
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
import huellitassoft_web.huellitasoft.service.CloudinaryService;
import huellitassoft_web.huellitasoft.service.PetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Text;

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
    private final CloudinaryService cloudinaryService;

    private ClientResponseDTO mapToResponseDTO(Client client) {
        return ClientResponseDTO.builder()
                .idCliente(client.getIdCliente())
                .nombres(client.getNombres())
                .apellidos(client.getApellidos())
                .documentoIdentidad(client.getDocumentoIdentidad())
                .email(client.getEmail())
                .telefono(client.getTelefono())
                .direccion(client.getDireccion())
                .estado(client.getEstado())
                .idUsuario(client.getUsuario().getIdUsuario())
                .build();
    }

    private PetResponseDTO convertToDTO(Pet pet) {
        return PetResponseDTO.builder()
                .idMascota(pet.getIdMascota())
                .nombre(pet.getNombre())
                .fechaNacimiento(pet.getFechaNacimiento())
                .sexo(pet.getSexo())
                .estado(pet.getEstado())
                .idCliente(pet.getCliente().getIdCliente())
                .idRaza(pet.getRaza().getIdRaza())
                .nombreCliente(pet.getCliente().getNombres() + " " + pet.getCliente().getApellidos())
                .nombreRaza(pet.getRaza().getNombre())
                .clientResponseDTO(mapToResponseDTO(pet.getCliente()))
                .idEspecie(pet.getRaza().getSpecie().getIdEspecie())
                .nombreEspecie(pet.getRaza().getSpecie().getNombre())
                .imagen(pet.getImagen())
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

        String imageUrl = null;
        if (petCreateDTO.getImagen() != null && !petCreateDTO.getImagen().isEmpty()) {
            log.info("Subiendo imagen de mascota a Cloudinary...");
            imageUrl = cloudinaryService.uploadImageMascotas(petCreateDTO.getImagen());
        }

        Pet pet = Pet.builder()
                .nombre(petCreateDTO.getNombre())
                .fechaNacimiento(petCreateDTO.getFechaNacimiento())
                .sexo(petCreateDTO.getSexo())
                .estado(petCreateDTO.getEstado())
                .imagen(imageUrl)
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

        //  Actualizar imagen si viene una nueva
        if (petUpdateDTO.getImagen() != null && !petUpdateDTO.getImagen().isEmpty()) {
            log.info("Actualizando imagen de mascota con ID: {}", id);

            // Eliminar imagen anterior si existe
            if (pet.getImagen() != null && !pet.getImagen().isEmpty()) {
                log.info("Eliminando imagen anterior de Cloudinary...");
                cloudinaryService.deleteImageMascotas(pet.getImagen());
            }

            // Subir nueva imagen
            String nuevaImagenUrl = cloudinaryService.uploadImageMascotas(petUpdateDTO.getImagen());
            pet.setImagen(nuevaImagenUrl);
            log.info(" Nueva imagen subida: {}", nuevaImagenUrl);
        }

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
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + id));

        // ️ Eliminar imagen de Cloudinary antes de eliminar la mascota
        if (pet.getImagen() != null && !pet.getImagen().isEmpty()) {
            log.info("🗑️ Eliminando imagen de Cloudinary antes de eliminar mascota...");
            cloudinaryService.deleteImageMascotas(pet.getImagen());
        }

        petRepository.deleteById(id);
        log.info(" Mascota eliminada con id: {}", id);
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
