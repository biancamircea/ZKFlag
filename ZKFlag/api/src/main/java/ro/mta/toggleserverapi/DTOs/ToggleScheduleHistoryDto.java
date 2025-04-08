package ro.mta.toggleserverapi.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ToggleScheduleHistoryDto {
    private Long id;
    private Instant activateAt;
    private Instant deactivateAt;
    private String environmentId;
    private String instanceId;
    private String toggleId;
    private String projectId;
    private String environmentName;
    private Integer recurrenceCount;
    private String scheduleType;
}
