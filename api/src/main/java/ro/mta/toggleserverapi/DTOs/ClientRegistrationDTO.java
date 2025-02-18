package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientRegistrationDTO {
    private final String appName;
    private final String instanceId;
    private final LocalDateTime started;
    private final String environment;
}
