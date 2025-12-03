package huellitassoft_web.huellitasoft.dto.Subsidiary;

import huellitassoft_web.huellitasoft.dto.Schedule.ScheduleResponseDTO;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubsidiaryResponseDTO {
    private Long idSubsidiary;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private SubsidiaryState state;
    private Long idUsuario;
    private String usuarioUsername;
    private String usuarioEmail;
    private List<ScheduleResponseDTO> schedules;
}