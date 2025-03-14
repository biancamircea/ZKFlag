package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class InstanceUsersAddAccessDTO {
    @JsonProperty("users")
    private List<InstanceUsersAddAccessDTO.UserIdsDTO> userIdsDTOList;

    @Data
    public static class UserIdsDTO {
        @JsonProperty("id")
        private String id;
    }
}
