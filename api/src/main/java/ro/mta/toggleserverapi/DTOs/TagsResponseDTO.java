package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TagsResponseDTO {
    @JsonProperty("tags")
    private List<TagDTO> tagDTOs;
}
