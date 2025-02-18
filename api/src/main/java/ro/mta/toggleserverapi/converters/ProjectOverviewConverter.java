package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.InstanceOverviewDTO;
import ro.mta.toggleserverapi.DTOs.ProjectOverviewDTO;
import ro.mta.toggleserverapi.DTOs.ToggleDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.util.ListUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProjectOverviewConverter {

    private final ToggleConverter toggleConverter;

    public ProjectOverviewDTO toDTO(Project project) {
        ProjectOverviewDTO projectOverviewDTO = new ProjectOverviewDTO();
        projectOverviewDTO.setId(project.getId());
        projectOverviewDTO.setName(project.getName());
        projectOverviewDTO.setDescription(project.getDescription());
        projectOverviewDTO.setMembers(ListUtil.listSize(project.getUserProjectRole()));

        // Adăugăm lista de toggles din proiect
        List<ToggleDTO> toggleDTOList = project.getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();
        projectOverviewDTO.setToggles(toggleDTOList);

        return projectOverviewDTO;
    }

    private void addTogglesToProjectOverviewDTO(ProjectOverviewDTO projectOverviewDTO, Project project) {
        // Obține lista de toggles din proiectul asociat instanței
        List<ToggleDTO> toggleDTOList = project.getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();

        // Setează lista de toggles în DTO
        projectOverviewDTO.setToggles(toggleDTOList);
    }


//    private void addEnabledEnvToProjectOverviewDTO(ProjectOverviewDTO projectOverviewDTO, Project project) {
////        get list of enabled envs in project
//        List<Environment> environments = project.getProjectEnvironmentList()
//                .stream()
//                .filter(ProjectEnvironment::getActive)
//                .map(ProjectEnvironment::getEnvironment)
//                .toList();
////        create List of string with env names
//        List<String> environmentsName = environments.stream()
//                .sorted(Comparator.comparingLong(Environment::getId))
//                .map(Environment::getName)
//                .collect(Collectors.toList());
//
////        set the environments name
//        projectOverviewDTO.setEnvironments(environmentsName);
//    }




//    private void addTogglesToProjectOverviewDTO(ProjectOverviewDTO projectOverviewDTO, Project project) {
////        get list of toggles in current project
//        List<Toggle> toggleList = project.getToggleList();
////        convert in DTOs objects
//        List<ToggleDTO> toggleDTOList = toggleList.stream()
//                .map(toggleConverter::toDTO)
//                .toList();
//
////        set list of toggles
//        projectOverviewDTO.setToggles(toggleDTOList);
//    }
//
//        public ProjectOverviewDTO toDTO(Project project){
//        ProjectOverviewDTO projectOverviewDTO = new ProjectOverviewDTO();
//        projectOverviewDTO.setId(project.getId());
//        projectOverviewDTO.setName(project.getName());
//        projectOverviewDTO.setDescription(project.getDescription());
//        projectOverviewDTO.setMembers(ListUtil.listSize(project.getUserProjectRole()));
//        projectOverviewDTO.setApiTokenCount(ListUtil.listSize(project.getApiTokens()));
//        addEnabledEnvToProjectOverviewDTO(projectOverviewDTO, project);
//        addTogglesToProjectOverviewDTO(projectOverviewDTO, project);
//        return projectOverviewDTO;
//    }
}

