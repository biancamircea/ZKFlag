package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.RoleDTO;
import ro.mta.toggleserverapi.DTOs.UserDTO;
import ro.mta.toggleserverapi.DTOs.UsersResponseDTO;
import ro.mta.toggleserverapi.entities.Role;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.services.RoleService;

import java.util.List;

@AllArgsConstructor
@Component
public class UserConverter {
    private final RoleService roleService;
    public UserDTO toDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getHashId());
        userDTO.setEmail(user.getEmail());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole().getRoleType().name());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    public User fromDTO(UserDTO userDTO){
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        Role role = roleService.fetchRoleByName(userDTO.getRole());
        user.setRole(role);
        user.setPassword(userDTO.getPassword());
        return user;
    }

    public UsersResponseDTO toListOfDTO(List<User> users){
        UsersResponseDTO usersResponseDTO = new UsersResponseDTO();
        List<UserDTO> userDTOS = users
                .stream()
                .map(this::toDTO)
                .toList();
        List<RoleDTO> roleDTOS = roleService.fetchAll()
                .stream()
                .map(RoleConverter::toDTO)
                .toList();
        usersResponseDTO.setUserDTOS(userDTOS);
        usersResponseDTO.setRoleDTOS(roleDTOS);
        return usersResponseDTO;
    }
}
