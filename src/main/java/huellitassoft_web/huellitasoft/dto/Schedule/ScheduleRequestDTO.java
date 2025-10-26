package huellitassoft_web.huellitasoft.dto.Schedule;

import huellitassoft_web.huellitasoft.enums.DayOfWeek;
import huellitassoft_web.huellitasoft.enums.ShiftType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {

    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "El tipo de turno es obligatorio")
    private ShiftType shiftType;

    @NotNull(message = "La hora de entrada es obligatoria")
    private LocalTime startTime;

    private LocalTime lunchStartTime;

    private LocalTime lunchEndTime;

    @NotNull(message = "La hora de salida es obligatoria")
    private LocalTime endTime;

    @NotNull(message = "El estado de apertura es obligatorio")
    private Boolean isOpen;
}