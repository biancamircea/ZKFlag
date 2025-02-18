package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.services.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class ToggleController {
    private final ToggleService toggleService;
    private final ToggleEnvironmentService toggleEnvironmentService;
    private final ConstraintService constraintService;
    private final EventService eventService;


    @GetMapping(path = "/toggles")
    public ResponseEntity<?> getAllToggles(){
        TogglesResponseDTO togglesResponseDTO = toggleService.getAllToggles();
        return ResponseEntity.ok(togglesResponseDTO);
    }

    @GetMapping(path = "/toggles/{id}")
    public ResponseEntity<?> getToggle(@PathVariable Long id){
//        TODO: modify method fetchToggle to getToggle and return DTO obj
        Toggle toggle = toggleService.fetchToggle(id);
        return ResponseEntity
                .ok(ToggleDTO.toDTO(toggle));
    }

    @GetMapping(path = "/projects/{projectId}/toggles")
    public ResponseEntity<?> getAllTogglesByProject(@PathVariable Long projectId){
        TogglesResponseDTO togglesResponseDTO = toggleService.getAllTogglesFromProject(projectId);
        return ResponseEntity.ok(togglesResponseDTO);
    }

    @GetMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> getToggleByProject(@PathVariable Long projectId, @PathVariable Long toggleId){
        ToggleDTO toggleDTO = toggleService.getToggleFromProject(projectId, toggleId);
        return ResponseEntity.ok(toggleDTO);
    }

    @GetMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}")
    public ResponseEntity<?> getToggleEnvironment(@PathVariable Long projectId, @PathVariable Long toggleId,@PathVariable Long instanceId,
                                                  @PathVariable Long environmentId){
//        Get infos about the feature from the project, in the specific environment, in a specific instance
        ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.getToggleEnvironment(projectId, toggleId,instanceId, environmentId);
        return ResponseEntity.ok(toggleEnvironmentDTO);
    }

    @GetMapping(path = "/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
    public ResponseEntity<?> getConstraintInToggle(@PathVariable Long projectId,
                                                               @PathVariable Long toggleId,
                                                              @PathVariable Long constraintId){
        ConstraintDTO constraintDTO = toggleService.getConstraintFromToggle(projectId,
                toggleId,
                constraintId);
        return ResponseEntity.ok(constraintDTO);
    }

    @GetMapping(path = "/projects/{projectId}/toggles/{toggleId}/constraints")
    public ResponseEntity<?> getAllConstraintsInToggle(@PathVariable Long projectId,
                                                       @PathVariable Long toggleId) {
        List<ConstraintDTO> constraints = toggleService.getAllConstraintsFromToggle(projectId, toggleId);
        //print the list of ConstraintDTO
        for (ConstraintDTO constraintDTO : constraints) {
            System.out.println("ConstraintDTO: " + constraintDTO.getContextName()+" "+ constraintDTO.getValues());
        }
        return ResponseEntity.ok(constraints);
    }

    @PostMapping(path = "/projects/{projectId}/toggles")
    public ResponseEntity<?> createToggle(@RequestBody @Valid ToggleDTO newToggleDTO, @PathVariable Long projectId){
        Toggle newToggle = ToggleDTO.fromDTO(newToggleDTO);
        Toggle createdToggle = toggleService.saveToggle(newToggle, projectId);
        if(createdToggle == null){
            throw new RuntimeException("Could not create project!");
        } else {

            return ResponseEntity
                    .created(linkTo(methodOn(ToggleController.class).getToggle(createdToggle.getId())).toUri())
                    .body(ToggleDTO.toDTO(createdToggle));
        }
    }

    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/on")
    public ResponseEntity<?> toggleFeatureOn(@PathVariable Long projectId, @PathVariable Long toggleId,@PathVariable Long instanceId,
                                             @PathVariable String environmentName){
        toggleService.enableToggleInEnvironment(projectId, toggleId, environmentName,instanceId);
        return ResponseEntity.noContent().build();

    }
    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/off")
    public ResponseEntity<?> toggleFeatureOff(@PathVariable Long projectId, @PathVariable Long toggleId,@PathVariable Long instanceId,
                                              @PathVariable String environmentName){
        toggleService.disableToggleInEnvironment(projectId, toggleId, environmentName,instanceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/tags/{tagId}")
    public ResponseEntity<?> addTag(@PathVariable Long projectId,
                                    @PathVariable Long toggleId,
                                    @PathVariable Long tagId){
        toggleService.addTagToToggle(tagId, toggleId, projectId);
        return ResponseEntity.ok(null);
    }

    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/constraints")
    public ResponseEntity<?> addConstraintInToggle(@RequestBody ConstraintDTO constraintDTO,
                                                              @PathVariable Long projectId,
                                                              @PathVariable Long toggleId){
//        TODO: move fromDTO method in ConstraintService
        Constraint constraint = constraintService.fromDTO(constraintDTO, projectId);
        ConstraintDTO createdConstraint = toggleService.addConstraintInToggle(constraint, projectId, toggleId);
        return ResponseEntity.created(linkTo(methodOn(ToggleController.class).getConstraintInToggle(projectId, toggleId, createdConstraint.getId())).toUri())
                .body(createdConstraint);
    }

    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> addPayloadInToggleEnvironment(@RequestBody @Valid PayloadDTO payloadDTO,
                                                              @PathVariable Long projectId,
                                                              @PathVariable Long toggleId,
                                                              @PathVariable Long instanceId,
                                                              @PathVariable Long environmentId){
        ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.addPayloadInToggleEnvironment(projectId,
                instanceId,
                toggleId,
                environmentId,
                payloadDTO.getEnabledValue(),
                payloadDTO.getDisabledValue());
        return ResponseEntity.ok(toggleEnvironmentDTO);
    }

    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> updateToggle(@RequestBody @Valid ToggleDTO toggleDTO, @PathVariable Long projectId, @PathVariable Long toggleId){
        Toggle toggle = ToggleDTO.fromDTO(toggleDTO);
        Toggle updatedToggle = toggleService.updateToggle(toggle, toggleId , projectId);
        eventService.submitAction(ActionType.UPDATE, updatedToggle.getProject(), updatedToggle);
        return ResponseEntity
                .created(linkTo(methodOn(ToggleController.class).getToggle(updatedToggle.getId())).toUri())
                .body(ToggleDTO.toDTO(updatedToggle));
    }

    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
    public ResponseEntity<?> updateConstraintInToggle(@RequestBody ConstraintDTO constraintDTO,
                                                                 @PathVariable Long projectId,
                                                                 @PathVariable Long toggleId,
                                                                 @PathVariable Long constraintId){
        Constraint constraint = constraintService.fromDTO(constraintDTO, projectId);
        ConstraintDTO updatedConstraint = toggleService.updateConstraintInToggleEnv(constraint, projectId, toggleId,  constraintId);
        return ResponseEntity.created(linkTo(methodOn(ToggleController.class).getConstraintInToggle(projectId, toggleId,  updatedConstraint.getId())).toUri())
                .body(updatedConstraint);
    }

    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> updatePayloadInToggleEnvironment(@RequestBody @Valid PayloadDTO payloadDTO,
                                                           @PathVariable Long projectId,
                                                           @PathVariable Long toggleId,
                                                           @PathVariable Long instanceId,
                                                           @PathVariable Long environmentId){
        ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.updatePayloadInToggleEnvironment(projectId,
                toggleId,
                environmentId,
                instanceId,
                payloadDTO.getEnabledValue(),
                payloadDTO.getDisabledValue());
        return ResponseEntity.ok(toggleEnvironmentDTO);
    }

    @DeleteMapping(path = "/toggles/{id}")
    public ResponseEntity<?> deleteToggle(@PathVariable Long id){
        toggleService.deleteToggle(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> deleteToggleByProject(@PathVariable Long projectId, @PathVariable Long toggleId){
        toggleService.deleteToggleByProject(projectId, toggleId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/tags/{tagId}")
    public ResponseEntity<?> removeTag(@PathVariable Long projectId,
                                    @PathVariable Long toggleId,
                                    @PathVariable Long tagId){
        toggleService.removeTagFromToggle(tagId, toggleId, projectId);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/constraints")
    public ResponseEntity<?> removeAllConstraintsInToggle(@PathVariable Long projectId,
                                                                 @PathVariable Long toggleId){
        toggleService.removeAllConstraintsFromToggleEnvironment(projectId,
                toggleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
    public ResponseEntity<?> removeConstraintInToggle(@PathVariable Long projectId,
                                                                 @PathVariable Long toggleId,
                                                                 @PathVariable Long constraintId){
        toggleService.removeConstraintFromToggleEnvironment(projectId,
                toggleId,
                constraintId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> removePayloadInToggleEnvironment(@PathVariable Long projectId,
                                                           @PathVariable Long toggleId,
                                                           @PathVariable Long instanceId,
                                                           @PathVariable Long environmentId){
        toggleService.removePayloadInToggleEnvironment(projectId,
                toggleId,
                environmentId,
                instanceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/schedule")
    public ResponseEntity<?> setToggleSchedule(@RequestBody ToggleScheduleDTO toggleScheduleDTO,
                                               @PathVariable Long toggleId,
                                               @PathVariable Long instanceId,
                                               @PathVariable Long environmentId) {
        ToggleEnvironment updatedToggleEnvironment = toggleEnvironmentService.setToggleSchedule(
                toggleId,
                environmentId,
                instanceId,
                toggleScheduleDTO.getStartOn(),
                toggleScheduleDTO.getStartOff(),
                toggleScheduleDTO.getStartDate(),
                toggleScheduleDTO.getEndDate()
        );
        return ResponseEntity.ok(updatedToggleEnvironment);
    }


    @GetMapping(path = "/toggles/{toggleId}/instances/{instanceId}/schedule_strategies")
    public List<ToggleScheduleDTO> getAllStrategiesForFlag( @PathVariable Long toggleId, @PathVariable Long instanceId) {
        List<ToggleScheduleDTO> strategies = toggleService.getAllStrategiesForFlag( toggleId,instanceId);
        //print the list of ToggleEnvironmentDTO
        for (ToggleScheduleDTO toggleScheduleDTO : strategies) {
            System.out.println("ToggleEnvironmentDTO: " + toggleScheduleDTO.getEnvironmentId()+" "+ toggleScheduleDTO.getStartDate());
        }
        return strategies;
    }


    @GetMapping("/toggles/{toggleId}/environments/{envId}/instances/{instanceId}/enabled")
    public ResponseEntity<Boolean> isToggleEnabled(
            @PathVariable Long toggleId,
            @PathVariable Long envId,
            @PathVariable Long instanceId) {
        Toggle toggle = toggleService.fetchToggleById(toggleId);
        Boolean isEnabled = toggleEnvironmentService.fetchByToggleAndEnvIdAndInstanceId(toggle, envId, instanceId);
        return ResponseEntity.ok(isEnabled);
    }
}
