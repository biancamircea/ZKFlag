package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.TagDTO;
import ro.mta.toggleserverapi.DTOs.TagsResponseDTO;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.services.TagService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects/{projectId}/tags")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<?> getAllTags(@PathVariable Long projectId){
        TagsResponseDTO tagsResponseDTO = tagService.getAllFromProject(projectId);
        return ResponseEntity.ok(tagsResponseDTO);
    }
    @GetMapping(path = "/{tagId}")
    public ResponseEntity<?> getTag(@PathVariable Long projectId, @PathVariable Long tagId){
        TagDTO tagDTO = tagService.getFromProject(projectId, tagId);
        return ResponseEntity.ok(tagDTO);
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody @Valid TagDTO tagDTO, @PathVariable Long projectId){
        Tag tag = TagDTO.fromDTO(tagDTO);
        TagDTO createdTagDTO = tagService.saveToProject(tag, projectId);
        return ResponseEntity.created(linkTo(methodOn(TagController.class).getTag(projectId, createdTagDTO.getId())).toUri())
                .body(createdTagDTO);
    }

    @PutMapping(path = "/{tagId}")
    public ResponseEntity<?> updateTag(@RequestBody @Valid TagDTO tagDTO, @PathVariable Long projectId ,@PathVariable Long tagId){
        Tag newTag = TagDTO.fromDTO(tagDTO);
        TagDTO updatedTag = tagService.updateFromProject(newTag, projectId, tagId);
        return ResponseEntity
                .created(linkTo(methodOn(TagController.class).getTag(projectId, updatedTag.getId())).toUri())
                .body(updatedTag);
    }

    @DeleteMapping(path = "/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long projectId, @PathVariable Long tagId){
        tagService.deleteFromProject(projectId, tagId);
        return ResponseEntity.noContent().build();
    }

}
