package ro.mta.toggleserverapi.DTOs;

import lombok.Data;
import ro.mta.toggleserverapi.enums.ActionType;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private String project;
    private String environment;
    private String toggle;
    private ActionType action;
    private LocalDateTime createdAt;
}
