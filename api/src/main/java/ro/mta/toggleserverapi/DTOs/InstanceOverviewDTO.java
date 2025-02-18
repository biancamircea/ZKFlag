package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InstanceOverviewDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private List<String> environments;

    private Long apiTokenCount;

    private List<ToggleDTO> toggles;
}
