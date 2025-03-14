package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectEnvironmentsResponseDTO {
    @JsonProperty("environments")
    private List<ProjectEnvironmentDTO> projectEnvironmentDTOList;
}
