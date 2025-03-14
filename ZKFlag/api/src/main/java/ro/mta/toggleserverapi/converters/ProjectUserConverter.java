package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.UserProjectDTO;
import ro.mta.toggleserverapi.entities.UserProject;

public class ProjectUserConverter {
    public static UserProjectDTO toDTO(UserProject userProject){
        UserProjectDTO userProjectDTO = new UserProjectDTO();
        userProjectDTO.setId(userProject.getUser().getHashId());
        userProjectDTO.setUserName(userProject.getUser().getEmail());
        userProjectDTO.setProjectName(userProject.getProject().getName());
        userProjectDTO.setProjectId(userProject.getProject().getHashId());
        return userProjectDTO;
    }
}
