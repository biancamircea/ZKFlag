package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.enums.UserRoleType;

@Data
public class RoleDTO {
    private Long id;

    @JsonProperty("name")
    @NotNull
    private UserRoleType type;

    private String description;
}
