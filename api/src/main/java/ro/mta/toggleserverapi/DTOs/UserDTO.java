package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String id;
    private String email;
    private String name;

    private String role;
    private String password;

    private LocalDateTime createdAt=LocalDateTime.now();


}
