package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @PostMapping("/projects/{projectId}/instances")
    public ResponseEntity<InstanceDTO> createInstance(
            @RequestBody InstanceDTO instanceDTO,
            @PathVariable String projectId) {
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        Instance instance = instanceConverter.fromDTO(instanceDTO);
        Instance savedInstance = instanceService.createInstanceWithDefaultEnvironments(instance, project.getId());

        eventService.submitAction(ActionType.CREATE, savedInstance.getProject(), savedInstance);
        return ResponseEntity.ok(instanceConverter.toDTO(savedInstance));
    }


    @GetMapping(path = "/instances/{instanceId}/api-tokens")
    public ResponseEntity<?> getInstanceApiTokens(@PathVariable String instanceId){
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        ApiTokensResponseDTO apiTokensResponseDTO = instanceService.getInstanceApiTokens(instance.getId());
        return ResponseEntity.ok(apiTokensResponseDTO);
    }

    @PostMapping(path = "/instances/{instanceId}/environments/{envId}/on")
    public ResponseEntity<?> enableInstanceEnvironment(@PathVariable String instanceId, @PathVariable String envId){
        Environment env=environmentRepository.findByHashId(envId).orElseThrow();

        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        try {
            instanceService.enableInstanceEnvironment(instance.getId(), env.getId());
            toggleService.createToggleEnvironments(instance.getId(), env.getId());

            eventService.submitAction(ActionType.ENABLE, instanceService.fetchInstance(instance.getId()), environmentService.fetchEnvironment(env.getId()));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error enabling environment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to enable environment: " + e.getMessage());
        }
    }


    @PostMapping(path = "/instances/{instanceId}/environments/{envId}/off")
    public ResponseEntity<?> disableInstanceEnvironment(@PathVariable String instanceId, @PathVariable String envId){
        Environment env=environmentRepository.findByHashId(envId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();

        toggleService.deleteToggleEnvironments(instance.getId(), env.getId());
        instanceService.disableInstanceEnvironment(instance.getId(), env.getId());
        eventService.submitAction(ActionType.DISABLE, instanceService.fetchInstance(instance.getId()), environmentService.fetchEnvironment(env.getId()));
        return ResponseEntity.noContent().build();
    }


    @PostMapping(path = "/projects/{projectId}/instances/{instanceId}/api-tokens")
    public ResponseEntity<?> createInstanceApiToken(@RequestBody @Valid ApiTokenDTO apiTokenDTO,
                                                    @PathVariable String projectId,
                                                    @PathVariable String instanceId) {

        apiTokenDTO.setInstanceId(instanceId);
        apiTokenDTO.setProjectId(projectId);
        ApiTokenDTO createdApiToken = apiTokenService.saveApiToken(apiTokenDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdApiToken.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdApiToken);
    }

    @DeleteMapping(path = "/instances/{instanceId}/api-tokens/{tokenId}")
    public ResponseEntity<?> deleteInstanceApiToken(@PathVariable String instanceId,
                                                   @PathVariable Long tokenId){
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        instanceService.deleteInstanceApiToken(instance.getId(), tokenId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/instances/{id}")
    public ResponseEntity<?> getInstanceOverview(@PathVariable String id) {
        Instance instance=instanceRepository.findByHashId(id).orElseThrow();
        InstanceOverviewDTO instanceOverviewDTO = instanceService.getInstanceOverview(instance.getId());
        return ResponseEntity.ok(instanceOverviewDTO);
    }

    @GetMapping(path = "/projects/{projectId}/instances")
    public ResponseEntity<?> getAllInstancesFromProject(@PathVariable String projectId) {
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        return ResponseEntity.ok(instanceService.getAllInstancesFromProject(project.getId()));
    }

    @DeleteMapping(path = "/projects/{projectId}/instances/{instanceId}")
    public ResponseEntity<?> deleteInstance(@PathVariable String projectId, @PathVariable String instanceId) {
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        instanceService.deleteInstance(project.getId(), instance.getId());
        eventService.submitAction(ActionType.DELETE, projectService.fetchProject(project.getId()), instanceService.fetchInstance(instance.getId()));
        return ResponseEntity.noContent().build();
    }


    @GetMapping(path = "/instances/{instanceId}/environments")
    public ResponseEntity<?> getAllEnvironmentsFromInstance(@PathVariable String instanceId) {
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        var res= ResponseEntity.ok(instanceService.getAllEnvironmentsFromInstance(instance.getId()));
        return res;
    }

    @GetMapping(path = "/instances/{instanceId}/toggles/{toggleId}/environments")
    public ResponseEntity<List<ToggleEnvironmentDTO>> getToggleEnvironments(
            @PathVariable String instanceId, @PathVariable String toggleId) {
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        List<ToggleEnvironmentDTO> toggleEnvironments = instanceService.getToggleEnvironments(instance.getId(), toggle.getId());
        return ResponseEntity.ok(toggleEnvironments);
    }

    @PostMapping(path = "/instances/{instanceId}/access")
    public ResponseEntity<?> addAccessToInstance(@RequestBody @Valid InstanceUsersAddAccessDTO instanceUsersAddAccessDTO,
                                                @PathVariable String instanceId){
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        List<User> users = instanceUsersAddAccessConverter.fromDTO(instanceUsersAddAccessDTO);
        instanceService.addAccessToInstance(users, instance.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/instances/{instanceId}/access/remove")
    public ResponseEntity<?> removeAccessToInstance(@RequestParam String userId,
                                                   @PathVariable String instanceId){
        System.out.println("Removing access for user: " + userId);
        User user=userRepository.findByHashId(userId).orElseThrow();
        Instance instance2=instanceRepository.findByHashId(instanceId).orElseThrow();
        Instance instance = instanceService.fetchInstance(instance2.getId());
        userInstanceService.removeAccessFromInstance(instance, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/instances/{instanceId}/instance-admins")
    public ResponseEntity<?> getUsersWithInstanceAdminRole(@PathVariable String instanceId) {
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        List<UserDTO> users = instanceService.getUsersWithInstanceAdminRole(instance.getId());
        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/instances/{instanceId}/project")
    public ResponseEntity<?> getProjectForInstance(@PathVariable String instanceId) {
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project = instanceService.fetchProjectByInstanceId(instance.getId());
        return ResponseEntity.ok(ProjectDTO.toDTO(project));
    }
}
