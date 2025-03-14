package ro.mta.toggleserverapi.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Tag;

@Data
public class TagDTO {
    private String id;

    @NotNull
    @NotBlank
    private String labelName;

    private String description;

    @NotNull
    @NotBlank
    private String color;

    public static TagDTO toDTO(Tag tag){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tag.getHashId());
        tagDTO.setLabelName(tag.getLabelName());
        tagDTO.setDescription(tag.getDescription());
        tagDTO.setColor(tag.getColor());
        return tagDTO;
    }
    public static Tag fromDTO(TagDTO tagDTO){
        Tag tag = new Tag();
        tag.setLabelName(tagDTO.getLabelName());
        tag.setDescription(tagDTO.getDescription());
        tag.setColor(tagDTO.getColor());
        return tag;
    }
}
