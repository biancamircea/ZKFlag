package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.InstanceUsersAddAccessDTO;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.repositories.UserRepository;
import ro.mta.toggleserverapi.services.UserService;

import java.util.List;

@AllArgsConstructor
@Component
public class InstanceUsersAddAccessConverter {
    private final UserRepository userRepository;
    private UserService userService;
    public List<User> fromDTO(InstanceUsersAddAccessDTO instanceUsersAddAccessDTO){
        return instanceUsersAddAccessDTO.getUserIdsDTOList()
                .stream()
                .map(userIdsDTO -> {
                    User user = userRepository.findByHashId(userIdsDTO.getId()).orElseThrow();
                    return user;
                })
                .toList();
    }
}
