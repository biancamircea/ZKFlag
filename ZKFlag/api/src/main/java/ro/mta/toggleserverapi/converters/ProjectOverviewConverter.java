package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.InstanceOverviewDTO;
import ro.mta.toggleserverapi.DTOs.ProjectOverviewDTO;
import ro.mta.toggleserverapi.DTOs.ToggleDTO;
import ro.mta.toggleserverapi.DTOs.UserDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.services.ProjectService;
import ro.mta.toggleserverapi.util.ListUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProjectOverviewConverter {

    private final ToggleConverter toggleConverter;
    private final UserConverter userConverter;

    public ProjectOverviewDTO toDTO(Project project) {
        ProjectOverviewDTO projectOverviewDTO = new ProjectOverviewDTO();
        projectOverviewDTO.setId(project.getHashId());
        projectOverviewDTO.setName(project.getName());
        projectOverviewDTO.setDescription(project.getDescription());

        List<UserProject> userProjects = project.getUserProjectRole();
        List<UserDTO> users=userProjects.stream()
                .filter(userProject -> userProject.getUser().getRole().getRoleType().name().equals("ProjectAdmin"))
                .map(UserProject::getUser)
                .map(userConverter::toDTO)
                .collect(Collectors.toList());

        Long size=users.stream().count();
        projectOverviewDTO.setMembers( size);

        List<ToggleDTO> toggleDTOList = project.getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();
        projectOverviewDTO.setToggles(toggleDTOList);

        return projectOverviewDTO;
    }

    private void addTogglesToProjectOverviewDTO(ProjectOverviewDTO projectOverviewDTO, Project project) {
        List<ToggleDTO> toggleDTOList = project.getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();

        projectOverviewDTO.setToggles(toggleDTOList);
    }
}

