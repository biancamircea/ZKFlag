package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRolesResponseDTO {
    @JsonProperty("project-roles")
    private List<ProjectRoleDTO> projectRoleDTOList;
}
