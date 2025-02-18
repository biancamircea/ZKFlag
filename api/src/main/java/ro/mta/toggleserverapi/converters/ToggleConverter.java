package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.TagDTO;
import ro.mta.toggleserverapi.DTOs.ToggleDTO;
import ro.mta.toggleserverapi.DTOs.ToggleEnvironmentDTO;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.entities.ToggleTag;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ToggleConverter {
    private final ToggleEnvironmentConverter toggleEnvironmentConverter;

    private void addToggleEnvironmentsToToggleDTO(ToggleDTO toggleDTO, Toggle toggle){
//        get toggle-env links
        List<ToggleEnvironment> toggleEnvironments = toggle.getToggleEnvironmentList();
//        convert in DTOs
        List<ToggleEnvironmentDTO> dtos = toggleEnvironments.stream()
                .sorted(Comparator.comparingLong(te -> te.getEnvironment().getId()))
                .map(toggleEnvironmentConverter::toDTO)
                .collect(Collectors.toList());
//        set list
        toggleDTO.setToggleEnvironmentDTOList(dtos);
    }
    private void addTagsToToggleDTO(ToggleDTO toggleDTO, Toggle toggle){
//        get tags of the toggle
        List<Tag> tags = toggle.getToggleTags().stream()
                .map(ToggleTag::getTag)
                .collect(Collectors.toList());
//        convert in DTOs List
        List<TagDTO> tagDTOS = tags.stream()
                .map(TagDTO::toDTO)
                .collect(Collectors.toList());
//        set List
        toggleDTO.setTagDTOList(tagDTOS);
    }
    public ToggleDTO toDTO(Toggle toggle){
//        it should replace ToggleDTO.toDTO
        ToggleDTO toggleDTO = new ToggleDTO();
        toggleDTO.setId(toggle.getId());
        toggleDTO.setName(toggle.getName());
        toggleDTO.setDescription(toggle.getDescription());
        toggleDTO.setProjectName(toggle.getProject().getName());
        toggleDTO.setCreatedAt(toggle.getCreatedAt());
        addToggleEnvironmentsToToggleDTO(toggleDTO, toggle);
        addTagsToToggleDTO(toggleDTO,toggle);
        return toggleDTO;
    }

    public Toggle fromDTO(ToggleDTO toggleDTO){
        Toggle toggle = new Toggle();
        toggle.setName(toggleDTO.getName());
        toggle.setDescription(toggleDTO.getDescription());
        return toggle;
    }
}
