package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

@Data
public class StrategiesRequest {
    private String toggleId;
    private String instanceId;
    private String environmentName;
}
