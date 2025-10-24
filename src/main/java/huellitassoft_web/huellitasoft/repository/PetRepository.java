package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Pet;
import huellitassoft_web.huellitasoft.enums.Sex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // Metodo para buscar todas las mascotas de un cliente
    List<Pet> findByClienteIdCliente(Long idCliente);

    // Buscar todas las mascotas de una raza
    List<Pet> findByRazaIdRaza(Long idRaza);

    // Buscar mascotas por sexo
    List<Pet> findBySexo(Sex sexo);

    // Buscar mascotas por estado
    List<Pet> findByEstado(Boolean estado);

    // Contar el número de mascotas de un cliente
    long countByClienteIdCliente(Long idCliente);

    // Eliminar mascotas de un cliente específico
    void deleteByClienteIdCliente(Long idCliente);
}
