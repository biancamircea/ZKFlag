package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.EnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.InstanceEnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.ProjectEnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.ProjectEnvironmentsResponseDTO;
import ro.mta.toggleserverapi.assemblers.EnvironmentsModelAssembler;
import ro.mta.toggleserverapi.assemblers.InstanceEnvironmentModelAssembler;
import ro.mta.toggleserverapi.assemblers.ProjectEnvironmentModelAssembler;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.services.EnvironmentService;
import ro.mta.toggleserverapi.services.EventService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping(path = "/environments")
@AllArgsConstructor
public class EnvironmentController {
    private final EnvironmentService environmentService;
    private final EnvironmentsModelAssembler environmentsAssembler;
    private final InstanceEnvironmentModelAssembler instanceEnvironmentModelAssembler;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<?> getAllEnvironments(){
//        Method used by admin to see all existing environments
//        TODO: create DTO in service not assembler, conditionally render toggleCount and projectCount if enabled
        List<Environment> environmentList = environmentService.fetchAllEnvironments();
        return ResponseEntity
                .ok(environmentsAssembler.toModel(environmentList));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getEnvironmentById(@PathVariable Long id){
//        Method used by admin
        Environment environment = environmentService.fetchEnvironment(id);
        return ResponseEntity
                .ok(EnvironmentDTO.toDTO(environment));
    }

    @GetMapping (path = "/instances/{instanceId}")
    public ResponseEntity<?> getInstanceEnvironments(@PathVariable Long instanceId){
//        Method used inside a instance settings to see the existing environments that could be activated inside instance
        List<InstanceEnvironmentDTO> environmentList = environmentService.fetchInstanceEnvironments(instanceId);
        System.out.println("✅ Returning environments: " + environmentList);
        return ResponseEntity.ok(instanceEnvironmentModelAssembler.toModel(environmentList));
    }

    @PostMapping
    public ResponseEntity<?> createEnvironment(@RequestBody @Valid EnvironmentDTO newEnvDTO){
//        Method used by admin to create a new environment
//        when created, it should be on disabled state, and not be connected with any env
        Environment newEnv = EnvironmentDTO.fromDTO(newEnvDTO);
        Environment createdEnv = environmentService.saveEnvironment(newEnv);
        if(createdEnv == null){
            throw new RuntimeException("Could not create environment!!");
        } else {
            eventService.submitAction(ActionType.CREATE, createdEnv);
            return ResponseEntity.created(linkTo(methodOn(EnvironmentController.class).getEnvironmentById(createdEnv.getId())).toUri())
                    .body(EnvironmentDTO.toDTO(createdEnv));
        }
    }

    @PostMapping(path = "/{id}/on")
    public ResponseEntity<?> enableEnvironment(@PathVariable Long id){
//        Method used by admin to enable an environment => that means it should be visible in every project
        environmentService.toggleEnvironmentOn(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/off")
    public ResponseEntity<?> disableEnvironment(@PathVariable Long id){
//        Method used by admin to disable an environment => it should erase all records with the specific environment
        environmentService.toggleEnvironmentOff(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateEnvironment(@RequestBody @Valid EnvironmentDTO environmentDTO, @PathVariable Long id){
//        Method used by admin to modify an existing environment
        Environment env = EnvironmentDTO.fromDTO(environmentDTO);
        Environment updatedEnvironment = environmentService.updateEnvironment(env, id);
        eventService.submitAction(ActionType.UPDATE, updatedEnvironment);
        return ResponseEntity.created(linkTo(methodOn(EnvironmentController.class).getEnvironmentById(updatedEnvironment.getId())).toUri())
                .body(EnvironmentDTO.toDTO(updatedEnvironment));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteEnvironment(@PathVariable Long id){
//       Method used by admin to delete an environment
        environmentService.deleteEnvironment(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/active/{instanceId}")
    public ResponseEntity<List<InstanceEnvironmentDTO>> getActiveEnvironments(@PathVariable Long instanceId) {
        List<InstanceEnvironmentDTO> environments = environmentService.getActiveEnvironmentsForInstance(instanceId);
        return ResponseEntity.ok(environments);
    }

}
