package ro.mta.toggleserverapi.DTOs;

import lombok.Data;
import ro.mta.toggleserverapi.enums.ScheduleType;

import java.time.Instant;

@Data
public class ScheduleDTO {
    private Long toggleId;
    private Long instanceId;
    private String environmentName;
    private Long projectId;
    private Instant activateAt;
    private Instant deactivateAt;

    private ScheduleType scheduleType;
    private Integer recurrenceCount;
}
