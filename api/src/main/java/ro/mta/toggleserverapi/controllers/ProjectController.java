package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.assemblers.ProjectModelAssembler;
import ro.mta.toggleserverapi.assemblers.ToggleModelAssembler;
import ro.mta.toggleserverapi.converters.ProjectUsersAddAccessConverter;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.services.ApiTokenService;
import ro.mta.toggleserverapi.services.EventService;
import ro.mta.toggleserverapi.services.ProjectService;
import ro.mta.toggleserverapi.services.ToggleService;


import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectUsersAddAccessConverter projectUsersAddAccessConverter;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<?> getAllProjects(){
        ProjectsResponseDTO projectsResponseDTO = projectService.getAllProjects();
        return ResponseEntity
                .ok(projectsResponseDTO);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getProjectOverview(@PathVariable Long id){
        ProjectOverviewDTO projectOverviewDTO = projectService.getProjectOverview(id);
        return ResponseEntity
                .ok(projectOverviewDTO);
    }

    @GetMapping(path = "/{projectId}/access")
    public ResponseEntity<?> getProjectAccess(@PathVariable Long projectId){
        ProjectAccessDTO projectAccessDTO = projectService.getProjectAccess(projectId);
        return ResponseEntity.ok(projectAccessDTO);
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody @Valid ProjectDTO newProjectDTO){
        Project newProject = ProjectDTO.fromDTO(newProjectDTO);
        Project createdProject = projectService.saveProject(newProject);

        if(createdProject == null){
            throw new RuntimeException("Could not create project!");
        } else{
            eventService.submitAction(ActionType.CREATE, createdProject);
            return ResponseEntity
                    .created(linkTo(methodOn(ProjectController.class).getProjectOverview(createdProject.getId())).toUri())
                    .body(ProjectDTO.toDTO(createdProject));
        }
    }

    @PostMapping(path = "/{projectId}/roles/{roleId}/access")
    public ResponseEntity<?> addAccessToProject(@RequestBody @Valid ProjectUsersAddAccessDTO projectUsersAddAccessDTO,
                                                @PathVariable Long projectId,
                                                @PathVariable Long roleId){
        List<User> users = projectUsersAddAccessConverter.fromDTO(projectUsersAddAccessDTO);
        projectService.addAccessToProject(users, projectId, roleId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectDTO projectDTO, @PathVariable Long id){
        Project project = ProjectDTO.fromDTO(projectDTO);
        Project updatedProject = projectService.updateProject(project, id);
        eventService.submitAction(ActionType.UPDATE, updatedProject);
        return ResponseEntity
                .created(linkTo(methodOn(ProjectController.class).getProjectOverview(updatedProject.getId())).toUri())
                .body(ProjectDTO.toDTO(updatedProject));
    }

    @PutMapping(path = "/{projectId}/users/{userId}/roles/{roleId}")
    public ResponseEntity<?> changeRoleForUser(@PathVariable Long projectId,
                                               @PathVariable Long userId,
                                               @PathVariable Long roleId){
        projectService.changeRoleForUser(projectId, userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(path = "/{projectId}/users/{userId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleForUser(@PathVariable Long projectId,
                                               @PathVariable Long userId,
                                               @PathVariable Long roleId){
        projectService.removeRoleForUser(projectId, userId, roleId);
        return ResponseEntity.noContent().build();
    }

}
