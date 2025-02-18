package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ProjectRoleDTO;
import ro.mta.toggleserverapi.DTOs.ProjectRolesResponseDTO;
import ro.mta.toggleserverapi.converters.ProjectRoleConverter;
import ro.mta.toggleserverapi.entities.ProjectRole;
import ro.mta.toggleserverapi.services.ProjectRoleService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/project-roles")
public class ProjectRoleController {
    private final ProjectRoleService projectRoleService;

    @GetMapping
    public ResponseEntity<?> getAllProjectRoles(){
        ProjectRolesResponseDTO projectRolesResponseDTO = projectRoleService.getAllProjectRoles();
        return ResponseEntity.ok(projectRolesResponseDTO);
    }
    @GetMapping(path = "/{projectRoleId}")
    public ResponseEntity<?> getProjectRole(@PathVariable Long projectRoleId){
        ProjectRoleDTO projectRoleDTO = projectRoleService.getProjectRoleById(projectRoleId);
        return ResponseEntity.ok(projectRoleDTO);
    }
    @PostMapping
    public ResponseEntity<?> createProjectRole(@RequestBody @Valid ProjectRoleDTO projectRoleDTO){
        ProjectRole projectRole = ProjectRoleConverter.fromDTO(projectRoleDTO);
        ProjectRoleDTO createdProjectRole = projectRoleService.createProjectRole(projectRole);
        return ResponseEntity.created(linkTo(methodOn(ProjectRoleController.class).getProjectRole(createdProjectRole.getId())).toUri())
                .body(createdProjectRole);
    }
    @PutMapping(path = "/{projectRoleId}")
    public ResponseEntity<?> updateProjectRole(@RequestBody @Valid ProjectRoleDTO projectRoleDTO,
                                               @PathVariable Long projectRoleId){
        ProjectRole projectRole = ProjectRoleConverter.fromDTO(projectRoleDTO);
        ProjectRoleDTO updatedProjectRole = projectRoleService.updateProjectRole(projectRole, projectRoleId);
        return ResponseEntity.created(linkTo(methodOn(ProjectRoleController.class).getProjectRole(updatedProjectRole.getId())).toUri())
                .body(updatedProjectRole);
    }

    @DeleteMapping(path = "/{projectRoleId}")
    public ResponseEntity<?> deleteProjectRole(@PathVariable Long projectRoleId){
        projectRoleService.deleteProjectRole(projectRoleId);
        return ResponseEntity.noContent().build();
    }
}
