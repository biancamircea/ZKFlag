package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToggleScheduleDTO {
    private Long instanceId;
    private Long toggleId;
    private Long environmentId;
    private LocalTime startOn;
    private LocalTime startOff;
    private LocalDate startDate;
    private LocalDate endDate;
}
