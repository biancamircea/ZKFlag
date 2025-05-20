package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.InstanceConverter;
import ro.mta.toggleserverapi.converters.InstanceUsersAddAccessConverter;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.services.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class InstanceController {
    private final InstanceService instanceService;
    private final ToggleService toggleService;
    private final ApiTokenService apiTokenService;
    private final InstanceConverter instanceConverter;
    private final InstanceUsersAddAccessConverter instanceUsersAddAccessConverter;
    private final UserInstanceService userInstanceService;
    private final EventService eventService;
    private final EnvironmentService environmentService;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;
    private final ToggleRepository toggleRepository;
    private final EnvironmentRepository environmentRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ToggleEnvironmentService toggleEnvironmentService;

    @PostMapping("/projects/{projectId}/instances")
    public ResponseEntity<InstanceDTO> createInstance(
            @RequestBody InstanceDTO instanceDTO,
            @PathVariable String projectId) {
        log.info("Creating instance for project {} with data: {}", projectId, instanceDTO);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            Instance instance = instanceConverter.fromDTO(instanceDTO);
            Instance savedInstance = instanceService.createInstanceWithDefaultEnvironments(instance, project.getId());
            eventService.submitAction(ActionType.CREATE, savedInstance.getProject(), savedInstance);
            log.info("Instance created successfully: {}", savedInstance.getName());
            return ResponseEntity.ok(instanceConverter.toDTO(savedInstance));
        } catch (NoSuchElementException e) {
            log.warn("Project not found while creating instance: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while creating instance: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(path = "/instances/{instanceId}/api-tokens")
    public ResponseEntity<?> getInstanceApiTokens(@PathVariable String instanceId) {
        log.info("Fetching API tokens for instance {}", instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            ApiTokensResponseDTO response = instanceService.getInstanceApiTokens(instance.getId());
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching API tokens for instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping(path = "/instances/{instanceId}/environments/{envId}/on")
    public ResponseEntity<?> enableInstanceEnvironment(@PathVariable String instanceId, @PathVariable String envId) {
        log.info("Enabling environment {} for instance {}", envId, instanceId);
        try {
            Environment env = environmentRepository.findByHashId(envId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found: " + envId));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));

            instanceService.enableInstanceEnvironment(instance.getId(), env.getId());
            toggleService.createToggleEnvironments(instance.getId(), env.getId());

            eventService.submitAction(ActionType.ENABLE, instanceService.fetchInstance(instance.getId()), environmentService.fetchEnvironment(env.getId()));
            log.info("Successfully enabled environment {} for instance {}", envId, instanceId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while enabling environment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error enabling environment {} for instance {}: {}", envId, instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to enable environment: " + e.getMessage());
        }
    }

    @PostMapping(path = "/instances/{instanceId}/environments/{envId}/off")
    public ResponseEntity<?> disableInstanceEnvironment(@PathVariable String instanceId, @PathVariable String envId) {
        log.info("Disabling environment {} for instance {}", envId, instanceId);
        try {
            Environment env = environmentRepository.findByHashId(envId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found: " + envId));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));

            toggleService.deleteToggleEnvironments(instance.getId(), env.getId());
            instanceService.disableInstanceEnvironment(instance.getId(), env.getId());
            eventService.submitAction(ActionType.DISABLE, instanceService.fetchInstance(instance.getId()), environmentService.fetchEnvironment(env.getId()));
            log.info("Successfully disabled environment {} for instance {}", envId, instanceId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while disabling environment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error disabling environment {} for instance {}: {}", envId, instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to disable environment: " + e.getMessage());
        }
    }

    @PostMapping(path = "/projects/{projectId}/instances/{instanceId}/api-tokens")
    public ResponseEntity<?> createInstanceApiToken(@RequestBody @Valid ApiTokenDTO apiTokenDTO,
                                                    @PathVariable String projectId,
                                                    @PathVariable String instanceId) {
        log.info("Creating API token for instance {} in project {}", instanceId, projectId);
        try {
            apiTokenDTO.setInstanceId(instanceId);
            apiTokenDTO.setProjectId(projectId);
            ApiTokenDTO createdApiToken = apiTokenService.saveApiToken(apiTokenDTO);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdApiToken.getId())
                    .toUri();

            log.info("API token created with id {}", createdApiToken.getId());
            return ResponseEntity.created(location).body(createdApiToken);
        } catch (Exception e) {
            log.error("Error creating API token for instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping(path = "/instances/{instanceId}/api-tokens/{tokenId}")
    public ResponseEntity<?> deleteInstanceApiToken(@PathVariable String instanceId,
                                                    @PathVariable Long tokenId) {
        log.info("Deleting API token {} for instance {}", tokenId, instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            instanceService.deleteInstanceApiToken(instance.getId(), tokenId);
            log.info("Deleted API token {} for instance {}", tokenId, instanceId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Instance not found for deleting API token: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error deleting API token {} for instance {}: {}", tokenId, instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instances/{id}")
    public ResponseEntity<?> getInstanceOverview(@PathVariable String id) {
        log.info("Fetching overview for instance {}", id);
        try {
            Instance instance = instanceRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + id));
            InstanceOverviewDTO overview = instanceService.getInstanceOverview(instance.getId());
            return ResponseEntity.ok(overview);
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching overview for instance {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/projects/{projectId}/instances")
    public ResponseEntity<?> getAllInstancesFromProject(@PathVariable String projectId) {
        log.info("Fetching all instances from project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            return ResponseEntity.ok(instanceService.getAllInstancesFromProject(project.getId()));
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            log.error("Error fetching instances from project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping(path = "/projects/{projectId}/instances/{instanceId}")
    public ResponseEntity<?> deleteInstance(@PathVariable String projectId, @PathVariable String instanceId) {
        log.info("Deleting instance {} from project {}", instanceId, projectId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));

            instanceService.deleteInstance(project.getId(), instance.getId());
            eventService.submitAction(ActionType.DELETE,
                    projectService.fetchProject(project.getId()),
                    instanceService.fetchInstance(instance.getId()));
            log.info("Instance {} deleted from project {}", instanceId, projectId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found during deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting instance {} from project {}: {}", instanceId, projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instances/{instanceId}/environments")
    public ResponseEntity<?> getAllEnvironmentsFromInstance(@PathVariable String instanceId) {
        log.info("Fetching environments for instance {}", instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            return ResponseEntity.ok(instanceService.getAllEnvironmentsFromInstance(instance.getId()));
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching environments for instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instances/{instanceId}/toggles/{toggleId}/environments")
    public ResponseEntity<List<ToggleEnvironmentDTO>> getToggleEnvironments(
            @PathVariable String instanceId, @PathVariable String toggleId) {
        log.info("Fetching toggle environments for toggle {} and instance {}", toggleId, instanceId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found: " + toggleId));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            List<ToggleEnvironmentDTO> toggleEnvironments = instanceService.getToggleEnvironments(instance.getId(), toggle.getId());
            return ResponseEntity.ok(toggleEnvironments);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while fetching toggle environments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error fetching toggle environments for instance {} and toggle {}: {}", instanceId, toggleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping(path = "/instances/{instanceId}/environments/{envId}/toggles")
    public ResponseEntity<List<ToggleEnvironmentDTO>> getToggleEnvironments2(@PathVariable String instanceId, @PathVariable String envId) {
        log.info("Fetching toggle environments for instance {} and environment {}", instanceId, envId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            Environment environment = environmentRepository.findByHashId(envId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found: " + envId));
            List<ToggleEnvironmentDTO> toggleEnvironments = toggleEnvironmentService.getToggleEnvironments(instance.getId(), environment.getId());
            return ResponseEntity.ok(toggleEnvironments);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error fetching toggle environments for instance {} and environment {}: {}", instanceId, envId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(path = "/instances/{instanceId}/access")
    public ResponseEntity<?> addAccessToInstance(@RequestBody @Valid InstanceUsersAddAccessDTO instanceUsersAddAccessDTO,
                                                 @PathVariable String instanceId) {
        log.info("Adding access to instance {} for users: {}", instanceId, instanceUsersAddAccessDTO);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            List<User> users = instanceUsersAddAccessConverter.fromDTO(instanceUsersAddAccessDTO);
            instanceService.addAccessToInstance(users, instance.getId());
            log.info("Access granted to instance {}", instanceId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error adding access to instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping(path = "/instances/{instanceId}/access/remove")
    public ResponseEntity<?> removeAccessToInstance(@RequestParam String userId,
                                                    @PathVariable String instanceId) {
        log.info("Removing access for user {} from instance {}", userId, instanceId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            Instance instance2 = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            Instance instance = instanceService.fetchInstance(instance2.getId());
            userInstanceService.removeAccessFromInstance(instance, user.getId());
            log.info("Access removed for user {} from instance {}", userId, instanceId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while removing access: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing access for user {} from instance {}: {}", userId, instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instances/{instanceId}/instance-admins")
    public ResponseEntity<?> getUsersWithInstanceAdminRole(@PathVariable String instanceId) {
        log.info("Fetching instance admins for instance {}", instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            List<UserDTO> users = instanceService.getUsersWithInstanceAdminRole(instance.getId());
            return ResponseEntity.ok(users);
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching instance admins for instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instances/{instanceId}/project")
    public ResponseEntity<?> getProjectForInstance(@PathVariable String instanceId) {
        log.info("Fetching project for instance {}", instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            Project project = instanceService.fetchProjectByInstanceId(instance.getId());
            return ResponseEntity.ok(ProjectDTO.toDTO(project));
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching project for instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

}
