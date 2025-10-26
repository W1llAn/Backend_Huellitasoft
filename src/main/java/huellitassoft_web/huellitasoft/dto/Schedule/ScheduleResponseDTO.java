package huellitassoft_web.huellitasoft.dto.Schedule;

import huellitassoft_web.huellitasoft.enums.DayOfWeek;
import huellitassoft_web.huellitasoft.enums.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {
    private Long idSchedule;
    private DayOfWeek dayOfWeek;
    private ShiftType shiftType;
    private LocalTime startTime;
    private LocalTime lunchStartTime;
    private LocalTime lunchEndTime;
    private LocalTime endTime;
    private Boolean isOpen;
}
