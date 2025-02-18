package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ProjectUsersAddAccessDTO;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.services.UserService;

import java.util.List;

@AllArgsConstructor
@Component
public class ProjectUsersAddAccessConverter {
    private UserService userService;
    public List<User> fromDTO(ProjectUsersAddAccessDTO projectUsersAddAccessDTO){
        return projectUsersAddAccessDTO.getUserIdsDTOList()
                .stream()
                .map(userIdsDTO -> {
                    User user = userService.fetchUserById(userIdsDTO.getId());
                    return user;
                })
                .toList();
    }
}
