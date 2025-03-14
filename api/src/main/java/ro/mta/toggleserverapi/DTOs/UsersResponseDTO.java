package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UsersResponseDTO {
    @JsonProperty("users")
    private List<UserDTO> userDTOS;

    @JsonProperty("roles")
    private List<RoleDTO> roleDTOS;
}
