package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContextFieldsResponseDTO {
    @JsonProperty("context-fields")
    private List<ContextFieldDTO> contextFieldDTOS;
}
