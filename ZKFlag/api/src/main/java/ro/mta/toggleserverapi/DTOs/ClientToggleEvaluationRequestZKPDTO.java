package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ClientToggleEvaluationRequestZKPDTO {
    String toggleName;
    private JsonNode proof;
}
