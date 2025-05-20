package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import ro.mta.toggleserverapi.repositories.UserRepository;
import ro.mta.toggleserverapi.services.*;


import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/projects")
@AllArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectUsersAddAccessConverter projectUsersAddAccessConverter;
    private final EventService eventService;
    private final UserProjectService userProjectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        log.info("Fetching all projects");
        try {
            ProjectsResponseDTO projectsResponseDTO = projectService.getAllProjects();
            return ResponseEntity.ok(projectsResponseDTO);
        } catch (Exception e) {
            log.error("Error fetching all projects: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectOverview(@PathVariable String id) {
        log.info("Fetching project overview for id {}", id);
        try {
            Project project = projectRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
            ProjectOverviewDTO projectOverviewDTO = projectService.getProjectOverview(project.getId());
            return ResponseEntity.ok(projectOverviewDTO);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error fetching project overview {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/{projectId}/access")
    public ResponseEntity<?> getProjectAccess(@PathVariable String projectId) {
        log.info("Fetching project access for project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            ProjectAccessDTO projectAccessDTO = projectService.getProjectAccess(project.getId());
            return ResponseEntity.ok(projectAccessDTO);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error fetching project access for {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody @Valid ProjectDTO newProjectDTO) {
        log.info("Creating new project with data: {}", newProjectDTO);
        try {
            Project newProject = ProjectDTO.fromDTO(newProjectDTO);
            Project createdProject = projectService.saveProject(newProject);

            if (createdProject == null) {
                log.error("Could not create project");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not create project");
            } else {
                eventService.submitAction(ActionType.CREATE, createdProject);
                log.info("Project created successfully: {}", createdProject.getId());
                return ResponseEntity.created(
                                linkTo(methodOn(ProjectController.class).getProjectOverview(createdProject.getHashId())).toUri())
                        .body(ProjectDTO.toDTO(createdProject));
            }
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping("/{projectId}/access")
    public ResponseEntity<?> addAccessToProject(@RequestBody @Valid ProjectUsersAddAccessDTO projectUsersAddAccessDTO,
                                                @PathVariable String projectId) {
        log.info("Adding access to project {} for users: {}", projectId, projectUsersAddAccessDTO);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            List<User> users = projectUsersAddAccessConverter.fromDTO(projectUsersAddAccessDTO);
            projectService.addAccessToProject(users, project.getId());
            log.info("Access granted to project {}", projectId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error adding access to project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping(path = "/{projectId}/access/remove")
    public ResponseEntity<?> removeAccessToProject(@RequestParam String userId,
                                                   @PathVariable String projectId) {
        log.info("Removing access for user {} from project {}", userId, projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            userProjectService.removeAccessFromProject(project, user.getId());
            log.info("Access removed for user {} from project {}", userId, projectId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while removing access: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing access for user {} from project {}: {}", userId, projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectDTO projectDTO, @PathVariable String id) {
        log.info("Updating project with id {}", id);
        try {
            Project project2 = projectRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
            Project project = ProjectDTO.fromDTO(projectDTO);
            Project updatedProject = projectService.updateProject(project, project2.getId());
            eventService.submitAction(ActionType.UPDATE, updatedProject);
            log.info("Project updated successfully: {}", updatedProject.getId());
            return ResponseEntity.created(
                            linkTo(methodOn(ProjectController.class).getProjectOverview(updatedProject.getHashId())).toUri())
                    .body(ProjectDTO.toDTO(updatedProject));
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error updating project {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        log.info("Deleting project with id {}", id);
        try {
            Project project = projectRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
            projectService.deleteProject(project.getId());
            log.info("Project deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error deleting project {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/{projectId}/project-admins")
    public ResponseEntity<?> getUsersWithProjectAdminRole(@PathVariable String projectId) {
        log.info("Fetching project admins for project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            List<UserDTO> users = projectService.getUsersWithProjectAdminRole(project.getId());
            return ResponseEntity.ok(users);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error fetching project admins for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

}
