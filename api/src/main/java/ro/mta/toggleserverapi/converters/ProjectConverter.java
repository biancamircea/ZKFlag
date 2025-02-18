package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ProjectDTO;
import ro.mta.toggleserverapi.entities.Project;

@AllArgsConstructor
@Component
public class ProjectConverter {
    public static ProjectDTO toDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());
        // `toggleCount`, `memberCount`, și `apiTokenCount` sunt comentate în DTO, deci nu le mapăm.
        return projectDTO;
    }

    public static Project fromDTO(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        return project;
    }
}
