package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ro.mta.toggleserverapi.entities.ContextField;

@Data
public class ContextFieldDTO {
    private String id;

    @NotNull
    @NotBlank
    private String name;

    private String description;

    public static ContextFieldDTO toDTO(ContextField contextField){
        ContextFieldDTO contextFieldDTO = new ContextFieldDTO();
        contextFieldDTO.setId(contextField.getHashId());
        contextFieldDTO.setName(contextField.getName());
        contextFieldDTO.setDescription(contextField.getDescription());
        return contextFieldDTO;
    }
    public static ContextField fromDTO(ContextFieldDTO contextFieldDTO){
        ContextField contextField = new ContextField();
        contextField.setHashId(contextFieldDTO.getId());
        contextField.setName(contextFieldDTO.getName());
        contextField.setDescription(contextFieldDTO.getDescription());
        return contextField;
    }

}
