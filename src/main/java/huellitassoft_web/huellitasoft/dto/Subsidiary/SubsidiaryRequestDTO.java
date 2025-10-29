package huellitassoft_web.huellitasoft.dto.Subsidiary;

import huellitassoft_web.huellitasoft.dto.Schedule.ScheduleRequestDTO;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubsidiaryRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
    private String address;

    @NotBlank(message = "El plan contratado es obligatorio")
    @Size(max = 100, message = "El plan contratado no debe exceder 100 caracteres")
    private String contractedPlan;

    @NotNull(message = "El estado es obligatorio")
    private SubsidiaryState state;

    @NotNull(message = "El usuario gestor es obligatorio")
    private Long idUsuario;

    @Valid
    private List<ScheduleRequestDTO> schedules;
}