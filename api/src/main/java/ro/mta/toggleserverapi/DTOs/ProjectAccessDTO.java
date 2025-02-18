package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ProjectAccessDTO {
//    members assigned to this project
    private List<UserProjectDTO> users;
//    roles existent in project
    private List<ProjectRoleDTO> roles;
}
