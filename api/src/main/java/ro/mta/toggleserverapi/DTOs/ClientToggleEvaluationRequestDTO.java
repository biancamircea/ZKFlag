package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class ClientToggleEvaluationRequestDTO {
    @NotNull
    private String toggleName;
    private List<ContextFromClientDTO> contextFields;

    @Data
    public static class ContextFromClientDTO {
        private String name;
        private String value;


    }

    public static Optional<String> getValueByName(List<ContextFromClientDTO> contextFields, String name){
        return contextFields.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(ContextFromClientDTO::getValue);
    }
}
