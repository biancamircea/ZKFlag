package ro.mta.toggleserverapi.DTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectsResponseDTO {
    @JsonProperty("projects")
    private List<ProjectDTO> projectDTOList;
}
