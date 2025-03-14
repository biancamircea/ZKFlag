package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.util.ListUtil;

@Data
public class ProjectDTO {
    //aici era long
    private String id;

    @NotNull
    @NotBlank
    private String name;

    private String description;


//    TODO: add attribute "createdAt"
//    TODO: add attribute "favorite"

    public static ProjectDTO toDTO(Project project){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getHashId());
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());
        return projectDTO;
    }

    public static Project fromDTO(ProjectDTO projectDTO){
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        return project;
    }
}
