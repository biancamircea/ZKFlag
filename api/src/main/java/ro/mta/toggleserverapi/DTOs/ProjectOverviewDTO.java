package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.util.ListUtil;

import java.util.List;

@Data
public class ProjectOverviewDTO {
    //aici era long
    private String  id;

    @NotNull
    @NotBlank
    private String name;

    private String description;

    private List<ToggleDTO> toggles;

    private Long members;

    public static ProjectOverviewDTO toDTO(Project project) {
        ProjectOverviewDTO projectOverviewDTO = new ProjectOverviewDTO();
        projectOverviewDTO.setId(project.getHashId());
        projectOverviewDTO.setName(project.getName());
        projectOverviewDTO.setDescription(project.getDescription());
        projectOverviewDTO.setMembers(ListUtil.listSize(project.getUserProjectRole()));
        return projectOverviewDTO;
    }
}
