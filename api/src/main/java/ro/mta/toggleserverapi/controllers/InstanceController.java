package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.InstanceConverter;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.services.ApiTokenService;
import ro.mta.toggleserverapi.services.InstanceService;
import ro.mta.toggleserverapi.services.ToggleService;

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

    @PostMapping("/projects/{projectId}/instances")
    public ResponseEntity<InstanceDTO> createInstance(
            @RequestBody InstanceDTO instanceDTO,
            @PathVariable Long projectId) {
        System.out.println("Creating instance for project: " + projectId);
        Instance instance = instanceConverter.fromDTO(instanceDTO);
        Instance savedInstance = instanceService.createInstanceWithDefaultEnvironments(instance, projectId);
        return ResponseEntity.ok(instanceConverter.toDTO(savedInstance));
    }


    @GetMapping(path = "/instances/{instanceId}/api-tokens")
    public ResponseEntity<?> getInstanceApiTokens(@PathVariable Long instanceId){
        ApiTokensResponseDTO apiTokensResponseDTO = instanceService.getInstanceApiTokens(instanceId);
        return ResponseEntity.ok(apiTokensResponseDTO);
    }

    @PostMapping(path = "/instances/{instanceId}/environments/{envId}/on")
    public ResponseEntity<?> enableInstanceEnvironment(@PathVariable Long instanceId, @PathVariable Long envId){

        try {
            instanceService.enableInstanceEnvironment(instanceId, envId);
            toggleService.createToggleEnvironments(instanceId, envId);

            System.out.println("Environment enabled successfully! Returning HTTP 204");
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (Exception e) {
            System.err.println("Error enabling environment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to enable environment: " + e.getMessage());
        }
    }


    @PostMapping(path = "/instances/{instanceId}/environments/{envId}/off")
    public ResponseEntity<?> disableInstanceEnvironment(@PathVariable Long instanceId, @PathVariable Long envId){

        toggleService.deleteToggleEnvironments(instanceId, envId);
        instanceService.disableInstanceEnvironment(instanceId, envId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/projects/{projectId}/instances/{instanceId}/api-tokens")
    public ResponseEntity<?> createInstanceApiToken(@RequestBody @Valid ApiTokenDTO apiTokenDTO,
                                                   @PathVariable Long projectId,
                                                   @PathVariable Long instanceId){
        //System.out.println("Creating API token for instance: " + instanceId);

        apiTokenDTO.setInstanceId(instanceId);
        apiTokenDTO.setProjectId(projectId);
        ApiTokenDTO createdApiToken = apiTokenService.saveApiToken(apiTokenDTO);

        return ResponseEntity.created(linkTo(methodOn(ApiTokenController.class).getApiToken(createdApiToken.getId())).toUri())
                .body(createdApiToken);
    }

    @DeleteMapping(path = "/instances/{instanceId}/api-tokens/{tokenId}")
    public ResponseEntity<?> deleteInstanceApiToken(@PathVariable Long instanceId,
                                                   @PathVariable Long tokenId){
        instanceService.deleteInstanceApiToken(instanceId, tokenId);
        return ResponseEntity.noContent().build();
    }

    //get instance overview by id
    @GetMapping(path = "/instances/{id}")
    public ResponseEntity<?> getInstanceOverview(@PathVariable Long id) {
        InstanceOverviewDTO instanceOverviewDTO = instanceService.getInstanceOverview(id);
        return ResponseEntity.ok(instanceOverviewDTO);
    }

    //get all instances from project
    @GetMapping(path = "/projects/{projectId}/instances")
    public ResponseEntity<?> getAllInstancesFromProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(instanceService.getAllInstancesFromProject(projectId));
    }

    //delete instance by id and project id
    @DeleteMapping(path = "/projects/{projectId}/instances/{instanceId}")
    public ResponseEntity<?> deleteInstance(@PathVariable Long projectId, @PathVariable Long instanceId) {
        instanceService.deleteInstance(projectId, instanceId);
        return ResponseEntity.noContent().build();
    }

    //get all environments from instance
    @GetMapping(path = "/instances/{instanceId}/environments")
    public ResponseEntity<?> getAllEnvironmentsFromInstance(@PathVariable Long instanceId) {
        var res= ResponseEntity.ok(instanceService.getAllEnvironmentsFromInstance(instanceId));
        //print the response
        System.out.println("response="+res);
        return res;
    }

    @GetMapping(path = "/instances/{instanceId}/toggles/{toggleId}/environments")
    public ResponseEntity<List<ToggleEnvironmentDTO>> getToggleEnvironments(
            @PathVariable Long instanceId, @PathVariable Long toggleId) {
        List<ToggleEnvironmentDTO> toggleEnvironments = instanceService.getToggleEnvironments(instanceId, toggleId);
        return ResponseEntity.ok(toggleEnvironments);
    }
}
