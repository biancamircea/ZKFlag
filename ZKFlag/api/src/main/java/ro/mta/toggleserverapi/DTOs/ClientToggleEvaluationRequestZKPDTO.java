package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

@Data
public class ClientToggleEvaluationRequestZKPDTO {
    String toggleName;
    String proof;
}
