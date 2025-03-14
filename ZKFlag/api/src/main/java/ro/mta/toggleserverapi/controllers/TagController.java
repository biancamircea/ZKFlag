package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.TagDTO;
import ro.mta.toggleserverapi.DTOs.TagsResponseDTO;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.TagRepository;
import ro.mta.toggleserverapi.services.TagService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects/{projectId}/tags")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<?> getAllTags(@PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        TagsResponseDTO tagsResponseDTO = tagService.getAllFromProject(project.getId());
        return ResponseEntity.ok(tagsResponseDTO);
    }
    @GetMapping(path = "/{tagId}")
    public ResponseEntity<?> getTag(@PathVariable String projectId, @PathVariable String tagId){
        Tag tag=tagRepository.findByHashId(tagId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        TagDTO tagDTO = tagService.getFromProject(project.getId(), tag.getId());
        return ResponseEntity.ok(tagDTO);
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody @Valid TagDTO tagDTO, @PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        Tag tag = TagDTO.fromDTO(tagDTO);
        TagDTO createdTagDTO = tagService.saveToProject(tag, project.getId());
        return ResponseEntity.created(linkTo(methodOn(TagController.class).getTag(projectId, createdTagDTO.getId())).toUri())
                .body(createdTagDTO);
    }

    @PutMapping(path = "/{tagId}")
    public ResponseEntity<?> updateTag(@RequestBody @Valid TagDTO tagDTO, @PathVariable String projectId ,@PathVariable String tagId){
        Tag tag=tagRepository.findByHashId(tagId).orElseThrow();

        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        Tag newTag = TagDTO.fromDTO(tagDTO);
        TagDTO updatedTag = tagService.updateFromProject(newTag, project.getId(), tag.getId());
        return ResponseEntity
                .created(linkTo(methodOn(TagController.class).getTag(projectId, updatedTag.getId())).toUri())
                .body(updatedTag);
    }

    @DeleteMapping(path = "/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable String projectId, @PathVariable String tagId){
        Tag tag=tagRepository.findByHashId(tagId).orElseThrow();

        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        tagService.deleteFromProject(project.getId(), tag.getId());
        return ResponseEntity.noContent().build();
    }

}
