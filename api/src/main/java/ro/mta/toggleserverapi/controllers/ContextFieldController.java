package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ContextFieldDTO;
import ro.mta.toggleserverapi.DTOs.ContextFieldsResponseDTO;
import ro.mta.toggleserverapi.entities.ContextField;
import ro.mta.toggleserverapi.services.ContextFieldService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects/{projectId}/context-fields")
@AllArgsConstructor
public class ContextFieldController {
    private final ContextFieldService contextFieldService;

    @GetMapping
    public ResponseEntity<?> getAllContextFields(@PathVariable Long projectId){
        ContextFieldsResponseDTO contextFieldsResponseDTO = contextFieldService.getAllFromProject(projectId);
        return ResponseEntity.ok(contextFieldsResponseDTO);
    }

    @GetMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> getContextField(@PathVariable Long projectId, @PathVariable Long contextFieldId){
        ContextFieldDTO contextFieldDTO = contextFieldService.getFromProject(projectId, contextFieldId);
        return ResponseEntity.ok(contextFieldDTO);
    }
    @PostMapping
    public ResponseEntity<?> createContextField(@RequestBody @Valid ContextFieldDTO contextFieldDTO, @PathVariable Long projectId){
        ContextField contextField = ContextFieldDTO.fromDTO(contextFieldDTO);
        ContextFieldDTO createdContextFieldDTO = contextFieldService.saveToProject(contextField, projectId);
        return ResponseEntity.created(linkTo(methodOn(ContextFieldController.class).getContextField(projectId, createdContextFieldDTO.getId())).toUri())
                .body(createdContextFieldDTO);

    }

    @PutMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> updateContextField(@RequestBody @Valid ContextFieldDTO contextFieldDTO,
                                                @PathVariable Long projectId,
                                                @PathVariable Long contextFieldId){
        ContextField contextField = ContextFieldDTO.fromDTO(contextFieldDTO);
        ContextFieldDTO updatedContextField = contextFieldService.updateFromProject(contextField, projectId, contextFieldId);
        return ResponseEntity
                .created(linkTo(methodOn(ContextFieldController.class).getContextField(projectId, updatedContextField.getId())).toUri())
                .body(updatedContextField);
    }

    @DeleteMapping(path = "/{contextFieldId}")
    public ResponseEntity<?> deleteContextField(@PathVariable Long projectId, @PathVariable Long contextFieldId){
        contextFieldService.deleteFromProject(projectId, contextFieldId);
        return ResponseEntity.noContent().build();
    }
}
