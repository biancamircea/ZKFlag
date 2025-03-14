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
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.services.*;


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
    private final UserProjectService userProjectService;
    private final ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<?> getAllProjects(){
        ProjectsResponseDTO projectsResponseDTO = projectService.getAllProjects();
        return ResponseEntity
                .ok(projectsResponseDTO);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getProjectOverview(@PathVariable String id){
        Project project=projectRepository.findByHashId(id).orElseThrow();
        ProjectOverviewDTO projectOverviewDTO = projectService.getProjectOverview(project.getId());
        return ResponseEntity
                .ok(projectOverviewDTO);
    }

    @GetMapping(path = "/{projectId}/access")
    public ResponseEntity<?> getProjectAccess(@PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ProjectAccessDTO projectAccessDTO = projectService.getProjectAccess(project.getId());
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
                    .created(linkTo(methodOn(ProjectController.class).getProjectOverview(createdProject.getHashId())).toUri())
                    .body(ProjectDTO.toDTO(createdProject));
        }
    }

    @PostMapping(path = "/{projectId}/access")
    public ResponseEntity<?> addAccessToProject(@RequestBody @Valid ProjectUsersAddAccessDTO projectUsersAddAccessDTO,
                                                @PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        List<User> users = projectUsersAddAccessConverter.fromDTO(projectUsersAddAccessDTO);
        projectService.addAccessToProject(users,project.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{projectId}/access/remove")
    public ResponseEntity<?> removeAccessToProject(@RequestBody @Valid Long userId,
                                                   @PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        userProjectService.removeAccessFromProject(project, userId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectDTO projectDTO, @PathVariable String id){
        Project project2=projectRepository.findByHashId(id).orElseThrow();

        Project project = ProjectDTO.fromDTO(projectDTO);
        Project updatedProject = projectService.updateProject(project, project2.getId() );
        eventService.submitAction(ActionType.UPDATE, updatedProject);
        return ResponseEntity
                .created(linkTo(methodOn(ProjectController.class).getProjectOverview(updatedProject.getHashId())).toUri())
                .body(ProjectDTO.toDTO(updatedProject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id){
        Project project=projectRepository.findByHashId(id).orElseThrow();

        projectService.deleteProject(project.getId());
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(path = "/{projectId}/project-admins")
    public ResponseEntity<?> getUsersWithProjectAdminRole(@PathVariable String projectId) {
        Project project2=projectRepository.findByHashId(projectId).orElseThrow();
        List<UserDTO> users = projectService.getUsersWithProjectAdminRole(project2.getId());
        return ResponseEntity.ok(users);
    }

}
