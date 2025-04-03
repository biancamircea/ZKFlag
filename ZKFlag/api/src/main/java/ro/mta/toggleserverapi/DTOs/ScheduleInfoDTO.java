package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleInfoDTO {
    private String taskType;
    private String recurenceType;
    private String taskInstanceId;
    private Instant executionTime;
}
