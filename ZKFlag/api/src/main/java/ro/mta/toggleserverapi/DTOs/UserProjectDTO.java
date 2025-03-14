package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProjectDTO {
    //user id
    private String id;

    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String projectName;

    @NotNull
    @NotBlank
    private String projectId;

}
