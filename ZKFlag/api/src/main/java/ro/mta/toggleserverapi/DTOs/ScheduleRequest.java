package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleRequest {
    private String projectId;
    private String toggleId;
    private String instanceId;
    private String environmentName;
    private Instant activateAt;
    private Instant deactivateAt;
    private String recurrence;
}
