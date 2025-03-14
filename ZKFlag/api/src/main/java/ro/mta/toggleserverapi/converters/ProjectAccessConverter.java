package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.ProjectAccessDTO;
import ro.mta.toggleserverapi.entities.UserProject;

import java.util.List;

public class ProjectAccessConverter {
    public static ProjectAccessDTO toDTO(List<UserProject> users){
        ProjectAccessDTO projectAccessDTO = new ProjectAccessDTO();
        projectAccessDTO.setUsers(
                users.stream()
                        .map(ProjectUserConverter::toDTO)
                        .toList()
        );
        return projectAccessDTO;
    }
}
