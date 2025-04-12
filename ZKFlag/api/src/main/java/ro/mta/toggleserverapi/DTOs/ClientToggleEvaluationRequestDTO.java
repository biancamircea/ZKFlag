package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class ClientToggleEvaluationRequestDTO {
    @NotNull
    private String toggleName;
    private List<ContextFromClientDTO> contextFields;
    private List<ProofFromClientDTO> proofs;

    @Data
    public static class ContextFromClientDTO {
        private String name;
        private String value;
    }

    @Data
    public static class ProofFromClientDTO {
        private String name;
        private JsonNode proof;
        private String type; //normal, location
    }

    public static Optional<String> getValueByName(List<ContextFromClientDTO> contextFields, String name){
        return contextFields.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(ContextFromClientDTO::getValue);
    }
}
