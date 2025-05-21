package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ContextFieldDTO;
import ro.mta.toggleserverapi.DTOs.ContextFieldsResponseDTO;
import ro.mta.toggleserverapi.entities.ContextField;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.repositories.ContextFieldRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.services.ContextFieldService;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping(path = "/projects/{projectId}/context-fields")
@AllArgsConstructor
public class ContextFieldController {
    private final ContextFieldService contextFieldService;
    private final ProjectRepository projectRepository;
    private final ContextFieldRepository contextFieldRepository;

    @GetMapping
    public ResponseEntity<?> getAllContextFields(@PathVariable String projectId) {
        log.info("Fetching all context fields for project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));
            ContextFieldsResponseDTO response = contextFieldService.getAllFromProject(project.getId());
            System.out.println("response = " + response);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Unexpected error while fetching context fields for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @GetMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> getContextField(@PathVariable String projectId, @PathVariable String contextFieldId) {
        log.info("Fetching context field {} for project {}", contextFieldId, projectId);
        try {
            ContextField contextField = contextFieldRepository.findByHashId(contextFieldId)
                    .orElseThrow(() -> new NoSuchElementException("Context field not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));
            ContextFieldDTO dto = contextFieldService.getFromProject(project.getId(), contextField.getId());
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping
    public ResponseEntity<?> createContextField(@RequestBody @Valid ContextFieldDTO contextFieldDTO, @PathVariable String projectId) {
        log.info("Creating new context field '{}' in project {}", contextFieldDTO.getName(), projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));
            ContextField contextField = ContextFieldDTO.fromDTO(contextFieldDTO);
            ContextFieldDTO created = contextFieldService.saveToProject(contextField, project.getId());
            log.info("Created context field with ID {}", created.getId());
            return ResponseEntity.created(
                            linkTo(methodOn(ContextFieldController.class).getContextField(projectId, created.getId())).toUri())
                    .body(created);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error while creating context field: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PutMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> updateContextField(@RequestBody @Valid ContextFieldDTO contextFieldDTO,
                                                @PathVariable String projectId,
                                                @PathVariable String contextFieldId) {
        log.info("Updating context field {} in project {}", contextFieldId, projectId);
        try {
            ContextField existing = contextFieldRepository.findByHashId(contextFieldId)
                    .orElseThrow(() -> new NoSuchElementException("Context field not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));
            ContextField updatedEntity = ContextFieldDTO.fromDTO(contextFieldDTO);
            ContextFieldDTO updated = contextFieldService.updateFromProject(updatedEntity, project.getId(), existing.getId());
            log.info("Updated context field {}", updated.getId());
            return ResponseEntity.created(
                            linkTo(methodOn(ContextFieldController.class).getContextField(projectId, updated.getId())).toUri())
                    .body(updated);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating context field: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> deleteContextField(@PathVariable String projectId, @PathVariable String contextFieldId) {
        log.info("Deleting context field {} from project {}", contextFieldId, projectId);
        try {
            ContextField contextField = contextFieldRepository.findByHashId(contextFieldId)
                    .orElseThrow(() -> new NoSuchElementException("Context field not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));
            contextFieldService.deleteFromProject(project.getId(), contextField.getId());
            log.info("Deleted context field {}", contextFieldId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting context field: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}
