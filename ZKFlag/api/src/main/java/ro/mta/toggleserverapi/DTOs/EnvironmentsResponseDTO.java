package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
public class EnvironmentsResponseDTO {
    @JsonProperty("environments")
    private List<EnvironmentDTO> environmentDTOS;
}
