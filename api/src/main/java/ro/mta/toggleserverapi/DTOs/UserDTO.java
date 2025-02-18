package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private Long roleId;

    private LocalDateTime createdAt;


}
