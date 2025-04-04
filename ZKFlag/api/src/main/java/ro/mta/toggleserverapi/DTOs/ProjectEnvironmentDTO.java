package ro.mta.toggleserverapi.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;
import ro.mta.toggleserverapi.enums.EnvironmentType;

@Data
public class ProjectEnvironmentDTO {
    private Long id;

    private String name;

    private EnvironmentType type;

    private Boolean enabled;

    private Integer enabledProjectToggleCount;

    public static ProjectEnvironmentDTO toDTO(ProjectEnvironment projectEnvironment){
        ProjectEnvironmentDTO projectEnvironmentDTO = new ProjectEnvironmentDTO();
        projectEnvironmentDTO.setId(projectEnvironment.getEnvironment().getId());
        projectEnvironmentDTO.setName(projectEnvironment.getEnvironment().getName());
        projectEnvironmentDTO.setType(projectEnvironment.getEnvironment().getType());
        projectEnvironmentDTO.setEnabled(projectEnvironment.getActive());

        return projectEnvironmentDTO;
    }
}
