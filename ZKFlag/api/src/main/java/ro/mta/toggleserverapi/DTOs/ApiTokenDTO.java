package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiTokenDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private String secret;

    @NotNull
    private String environmentId;

    private String environmentName;

    private String projectId;
    private String instanceId;
    private Long type;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
