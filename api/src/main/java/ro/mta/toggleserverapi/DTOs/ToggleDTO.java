package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Toggle;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ToggleDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private String description;

    @JsonProperty("project")
    private String projectName;

    private Long projectId;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("tags")
    private List<TagDTO> tagDTOList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("environments")
    private List<ToggleEnvironmentDTO> toggleEnvironmentDTOList;

    private LocalDateTime createdAt;

    public static ToggleDTO toDTO(Toggle toggle){
        ToggleDTO toggleDTO = new ToggleDTO();
        toggleDTO.setId(toggle.getId());
        toggleDTO.setName(toggle.getName());
        toggleDTO.setDescription(toggle.getDescription());
        toggleDTO.setProjectName(toggle.getProject().getName());
        toggleDTO.setProjectId(toggle.getProject().getId());
        toggleDTO.setCreatedAt(toggle.getCreatedAt());
        return toggleDTO;
    }

    public static Toggle fromDTO(ToggleDTO toggleDTO){
        Toggle toggle = new Toggle();
        toggle.setName(toggleDTO.getName());
        toggle.setDescription(toggleDTO.getDescription());
        return toggle;
    }
}
