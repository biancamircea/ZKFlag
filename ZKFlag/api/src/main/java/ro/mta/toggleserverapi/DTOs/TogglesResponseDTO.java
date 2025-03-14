package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Toggle;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TogglesResponseDTO {
    @JsonProperty("toggles")
    private List<ToggleDTO> toggleDTOList;

    public static TogglesResponseDTO toDTO(List<Toggle> toggleList){
        TogglesResponseDTO togglesResponseDTO = new TogglesResponseDTO();
        List<ToggleDTO> toggleDTOS = toggleList
                .stream()
                .map(ToggleDTO::toDTO)
                .collect(Collectors.toList());

        togglesResponseDTO.setToggleDTOList(toggleDTOS);
        return togglesResponseDTO;

    }

    public static TogglesResponseDTO toDTOSimple(List<ToggleDTO> toggleDTOS){
        TogglesResponseDTO togglesResponseDTO = new TogglesResponseDTO();
        togglesResponseDTO.setToggleDTOList(toggleDTOS);
        return togglesResponseDTO;
    }
}
