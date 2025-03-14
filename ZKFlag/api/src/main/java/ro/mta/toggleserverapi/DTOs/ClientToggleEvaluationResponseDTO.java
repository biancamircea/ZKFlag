package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

@Data
public class ClientToggleEvaluationResponseDTO {
    private Boolean enabled;
    private String payload;
}
