package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.assemblers.EnvironmentsModelAssembler;
import ro.mta.toggleserverapi.assemblers.InstanceEnvironmentModelAssembler;
import ro.mta.toggleserverapi.assemblers.ProjectEnvironmentModelAssembler;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
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
    private final InstanceRepository instanceRepository;
    private final EnvironmentRepository environmentRepository;

    @GetMapping
    public ResponseEntity<?> getAllEnvironments(){
//        TODO: create DTO in service not assembler, conditionally render toggleCount and projectCount if enabled
        List<Environment> environmentList = environmentService.fetchAllEnvironments();
        return ResponseEntity
                .ok(environmentsAssembler.toModel(environmentList));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getEnvironmentById(@PathVariable String id){
        Environment env=environmentRepository.findByHashId(id).orElseThrow();
        Environment environment = environmentService.fetchEnvironment(env.getId());
        return ResponseEntity
                .ok(EnvironmentDTO.toDTO(environment));
    }

    @GetMapping (path = "/instances/{instanceId}")
    public ResponseEntity<?> getInstanceEnvironments(@PathVariable String instanceId){
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        List<InstanceEnvironmentDTO> environmentList = environmentService.fetchInstanceEnvironments(instance.getId());
        return ResponseEntity.ok(instanceEnvironmentModelAssembler.toModel(environmentList));
    }

    @PostMapping
    public ResponseEntity<?> createEnvironment(@RequestBody @Valid EnvironmentDTO newEnvDTO){
        Environment newEnv = EnvironmentDTO.fromDTO(newEnvDTO);
        Environment createdEnv = environmentService.saveEnvironment(newEnv);
        if(createdEnv == null){
            throw new RuntimeException("Could not create environment!!");
        } else {
            eventService.submitAction(ActionType.CREATE, createdEnv);
            return ResponseEntity.created(linkTo(methodOn(EnvironmentController.class).getEnvironmentById(createdEnv.getHashId())).toUri())
                    .body(EnvironmentDTO.toDTO(createdEnv));
        }
    }

    @PostMapping(path = "/{id}/on")
    public ResponseEntity<?> enableEnvironment(@PathVariable String id){
        Environment env=environmentRepository.findByHashId(id).orElseThrow();
        environmentService.toggleEnvironmentOn(env.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/off")
    public ResponseEntity<?> disableEnvironment(@PathVariable String id){
        Environment env=environmentRepository.findByHashId(id).orElseThrow();
        environmentService.toggleEnvironmentOff(env.getId());
        return ResponseEntity.ok().build();
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateEnvironment(@RequestBody @Valid EnvironmentDTO environmentDTO, @PathVariable String id){
        Environment env2=environmentRepository.findByHashId(id).orElseThrow();
        Environment env = EnvironmentDTO.fromDTO(environmentDTO);
        Environment updatedEnvironment = environmentService.updateEnvironment(env, env2.getId());
        eventService.submitAction(ActionType.UPDATE, updatedEnvironment);
        return ResponseEntity.created(linkTo(methodOn(EnvironmentController.class).getEnvironmentById(updatedEnvironment.getHashId())).toUri())
                .body(EnvironmentDTO.toDTO(updatedEnvironment));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteEnvironment(@PathVariable String id){
        Environment env=environmentRepository.findByHashId(id).orElseThrow();
        environmentService.deleteEnvironment(env.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active/{instanceId}")
    public ResponseEntity<List<InstanceEnvironmentDTO>> getActiveEnvironments(@PathVariable String instanceId) {
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        List<InstanceEnvironmentDTO> environments = environmentService.getActiveEnvironmentsForInstance(instance.getId());
        return ResponseEntity.ok(environments);
    }

    @GetMapping("/{environmentId}/toggle-environments")
    public ResponseEntity<List<ToggleEnvironmentDTO>> getAllToggleEnvForEnvironment(@PathVariable String environmentId) {
        Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
        List<ToggleEnvironmentDTO> toggleEnvironments = environmentService.getAllToggleEnvironmentsForEnvironment(env.getId());
        return ResponseEntity.ok(toggleEnvironments);
    }

}
