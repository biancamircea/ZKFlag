package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.TagDTO;
import ro.mta.toggleserverapi.DTOs.TagsResponseDTO;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.TagRepository;
import ro.mta.toggleserverapi.services.TagService;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects/{projectId}/tags")
@Slf4j
@AllArgsConstructor
public class TagController {
    private final TagService tagService;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<?> getAllTags(@PathVariable String projectId) {
        log.info("Fetching all tags for project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            TagsResponseDTO tagsResponseDTO = tagService.getAllFromProject(project.getId());
            return ResponseEntity.ok(tagsResponseDTO);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error fetching tags for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{tagId}")
    public ResponseEntity<?> getTag(@PathVariable String projectId, @PathVariable String tagId) {
        log.info("Fetching tag {} for project {}", tagId, projectId);
        try {
            Tag tag = tagRepository.findByHashId(tagId)
                    .orElseThrow(() -> new NoSuchElementException("Tag not found: " + tagId));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            TagDTO tagDTO = tagService.getFromProject(project.getId(), tag.getId());
            return ResponseEntity.ok(tagDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while fetching tag: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching tag {} for project {}: {}", tagId, projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody @Valid TagDTO tagDTO, @PathVariable String projectId) {
        log.info("Creating new tag in project {} with data: {}", projectId, tagDTO);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            Tag tag = TagDTO.fromDTO(tagDTO);
            TagDTO createdTagDTO = tagService.saveToProject(tag, project.getId());
            log.info("Created tag with ID {}", createdTagDTO.getId());
            return ResponseEntity.created(linkTo(methodOn(TagController.class).getTag(projectId, createdTagDTO.getId())).toUri())
                    .body(createdTagDTO);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error creating tag in project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PutMapping(path = "/{tagId}")
    public ResponseEntity<?> updateTag(@RequestBody @Valid TagDTO tagDTO, @PathVariable String projectId, @PathVariable String tagId) {
        log.info("Updating tag {} in project {}", tagId, projectId);
        try {
            Tag tag = tagRepository.findByHashId(tagId)
                    .orElseThrow(() -> new NoSuchElementException("Tag not found: " + tagId));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            Tag newTag = TagDTO.fromDTO(tagDTO);
            TagDTO updatedTag = tagService.updateFromProject(newTag, project.getId(), tag.getId());
            log.info("Updated tag with ID {}", updatedTag.getId());
            return ResponseEntity.created(linkTo(methodOn(TagController.class).getTag(projectId, updatedTag.getId())).toUri())
                    .body(updatedTag);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while updating tag: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating tag {} in project {}: {}", tagId, projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping(path = "/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable String projectId, @PathVariable String tagId) {
        log.info("Deleting tag {} from project {}", tagId, projectId);
        try {
            Tag tag = tagRepository.findByHashId(tagId)
                    .orElseThrow(() -> new NoSuchElementException("Tag not found: " + tagId));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            tagService.deleteFromProject(project.getId(), tag.getId());
            log.info("Tag {} deleted from project {}", tagId, projectId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while deleting tag: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting tag {} from project {}: {}", tagId, projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}
