package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectUsersAddAccessDTO {
    @JsonProperty("users")
    private List<UserIdsDTO> userIdsDTOList;

    @Data
    public static class UserIdsDTO {
        @JsonProperty("id")
        private Long id;
    }
}
