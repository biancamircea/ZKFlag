package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UsersResponseDTO {
//    for /users ADMIN page to see all existing user and roles of app
//    all users in APP
    @JsonProperty("users")
    private List<UserDTO> userDTOS;

//    all roles in APP
    @JsonProperty("roles")
    private List<RoleDTO> roleDTOS;
}
