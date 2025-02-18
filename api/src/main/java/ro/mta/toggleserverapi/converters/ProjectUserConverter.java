package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.UserProjectDTO;
import ro.mta.toggleserverapi.entities.UserProject;

public class ProjectUserConverter {
    public static UserProjectDTO toDTO(UserProject userProject){
        UserProjectDTO userProjectDTO = new UserProjectDTO();
        userProjectDTO.setId(userProject.getUser().getId());
        userProjectDTO.setName(userProject.getUser().getName());
        userProjectDTO.setEmail(userProject.getUser().getEmail());
        userProjectDTO.setAddedAt(userProject.getAddedAt());
        userProjectDTO.setRoleId(userProject.getProjectRole().getId());
        return userProjectDTO;
    }
}
