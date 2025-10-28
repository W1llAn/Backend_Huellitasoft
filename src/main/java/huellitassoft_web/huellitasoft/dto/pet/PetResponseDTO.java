package huellitassoft_web.huellitasoft.dto.pet;

import huellitassoft_web.huellitasoft.dto.client.ClientResponseDTO;
import huellitassoft_web.huellitasoft.entity.Client;
import huellitassoft_web.huellitasoft.entity.Race;
import huellitassoft_web.huellitasoft.enums.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que retorna información de la mascota")
public class PetResponseDTO {
    private Long idMascota;
    private String nombre;
    private LocalDate fechaNacimiento;
    private Sex sexo;
    private Boolean estado;
    private Long idCliente;
    private Long idRaza;
    private String nombreCliente;
    private String nombreRaza;
    private Long idEspecie;
    private String nombreEspecie;
    private ClientResponseDTO clientResponseDTO;
}
