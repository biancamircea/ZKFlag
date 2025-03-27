package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ContextFieldDTO;
import ro.mta.toggleserverapi.DTOs.ContextFieldsResponseDTO;
import ro.mta.toggleserverapi.entities.ContextField;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.repositories.ContextFieldRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.services.ContextFieldService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects/{projectId}/context-fields")
@AllArgsConstructor
public class ContextFieldController {
    private final ContextFieldService contextFieldService;
    private final ProjectRepository projectRepository;
    private final ContextFieldRepository contextFieldRepository;

    @GetMapping
    public ResponseEntity<?> getAllContextFields(@PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ContextFieldsResponseDTO contextFieldsResponseDTO = contextFieldService.getAllFromProject(project.getId());
        return ResponseEntity.ok(contextFieldsResponseDTO);
    }

    @GetMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> getContextField(@PathVariable String projectId, @PathVariable String contextFieldId){
        ContextField contextField=contextFieldRepository.findByHashId(contextFieldId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ContextFieldDTO contextFieldDTO = contextFieldService.getFromProject(project.getId(), contextField.getId());
        return ResponseEntity.ok(contextFieldDTO);
    }
    @PostMapping
    public ResponseEntity<?> createContextField(@RequestBody @Valid ContextFieldDTO contextFieldDTO, @PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ContextField contextField = ContextFieldDTO.fromDTO(contextFieldDTO);
        ContextFieldDTO createdContextFieldDTO = contextFieldService.saveToProject(contextField, project.getId());
        return ResponseEntity.created(linkTo(methodOn(ContextFieldController.class).getContextField(projectId, createdContextFieldDTO.getId())).toUri())
                .body(createdContextFieldDTO);

    }

    @PutMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> updateContextField(@RequestBody @Valid ContextFieldDTO contextFieldDTO,
                                                @PathVariable String projectId,
                                                @PathVariable String contextFieldId){
        ContextField contextField2=contextFieldRepository.findByHashId(contextFieldId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();

        ContextField contextField = ContextFieldDTO.fromDTO(contextFieldDTO);
        ContextFieldDTO updatedContextField = contextFieldService.updateFromProject(contextField, project.getId(), contextField2.getId());
        return ResponseEntity
                .created(linkTo(methodOn(ContextFieldController.class).getContextField(projectId, updatedContextField.getId())).toUri())
                .body(updatedContextField);
    }

    @DeleteMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> deleteContextField(@PathVariable String projectId, @PathVariable String contextFieldId){
        ContextField contextField=contextFieldRepository.findByHashId(contextFieldId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        contextFieldService.deleteFromProject(project.getId(), contextField.getId());
        return ResponseEntity.noContent().build();
    }
}
