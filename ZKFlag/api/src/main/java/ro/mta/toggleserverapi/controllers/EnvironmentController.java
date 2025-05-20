package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
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
    public ResponseEntity<?> getAllEnvironments() {
        try {
            log.info("Getting all environments");
            List<Environment> environmentList = environmentService.fetchAllEnvironments();
            return ResponseEntity.ok(environmentsAssembler.toModel(environmentList));
        } catch (Exception e) {
            log.error("Error fetching all environments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getEnvironmentById(@PathVariable String id) {
        log.info("Fetching environment with id {}", id);
        try {
            Environment env = environmentRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            Environment environment = environmentService.fetchEnvironment(env.getId());
            return ResponseEntity.ok(EnvironmentDTO.toDTO(environment));
        } catch (NoSuchElementException e) {
            log.warn("Environment not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Environment not found");
        } catch (Exception e) {
            log.error("Unexpected error fetching environment {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instances/{instanceId}")
    public ResponseEntity<?> getInstanceEnvironments(@PathVariable String instanceId) {
        log.info("Fetching environments for instance {}", instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            List<InstanceEnvironmentDTO> environmentList = environmentService.fetchInstanceEnvironments(instance.getId());
            return ResponseEntity.ok(instanceEnvironmentModelAssembler.toModel(environmentList));
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching instance environments {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping
    public ResponseEntity<?> createEnvironment(@RequestBody @Valid EnvironmentDTO newEnvDTO) {
        log.info("Creating environment: {}", newEnvDTO.getName());
        try {
            Environment newEnv = EnvironmentDTO.fromDTO(newEnvDTO);
            Environment createdEnv = environmentService.saveEnvironment(newEnv);
            if (createdEnv == null) {
                log.error("Could not create environment");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not create environment");
            }
            eventService.submitAction(ActionType.CREATE, createdEnv);
            log.info("Environment created: {}", createdEnv.getName());
            return ResponseEntity.created(
                            linkTo(methodOn(EnvironmentController.class).getEnvironmentById(createdEnv.getHashId())).toUri())
                    .body(EnvironmentDTO.toDTO(createdEnv));
        } catch (Exception e) {
            log.error("Error creating environment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping(path = "/{id}/on")
    public ResponseEntity<?> enableEnvironment(@PathVariable String id) {
        log.info("Enabling environment with id {}", id);
        try {
            Environment env = environmentRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            environmentService.toggleEnvironmentOn(env.getId());
            log.info("Environment {} is now enabled", env.getName());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Environment not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Environment not found");
        } catch (Exception e) {
            log.error("Error enabling environment {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping(path = "/{id}/off")
    public ResponseEntity<?> disableEnvironment(@PathVariable String id) {
        log.info("Disabling environment with id {}", id);
        try {
            Environment env = environmentRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            environmentService.toggleEnvironmentOff(env.getId());
            log.info("Environment {} is now disabled", env.getName());
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            log.warn("Environment not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Environment not found");
        } catch (Exception e) {
            log.error("Error disabling environment {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateEnvironment(@RequestBody @Valid EnvironmentDTO environmentDTO, @PathVariable String id) {
        log.info("Updating environment with id {}", id);
        try {
            Environment existing = environmentRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            Environment env = EnvironmentDTO.fromDTO(environmentDTO);
            Environment updatedEnvironment = environmentService.updateEnvironment(env, existing.getId());
            eventService.submitAction(ActionType.UPDATE, updatedEnvironment);
            log.info("Environment updated: {}", updatedEnvironment.getName());
            return ResponseEntity.created(
                            linkTo(methodOn(EnvironmentController.class).getEnvironmentById(updatedEnvironment.getHashId())).toUri())
                    .body(EnvironmentDTO.toDTO(updatedEnvironment));
        } catch (NoSuchElementException e) {
            log.warn("Environment not found for update: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Environment not found");
        } catch (Exception e) {
            log.error("Error updating environment {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteEnvironment(@PathVariable String id) {
        log.info("Deleting environment with id {}", id);
        try {
            Environment env = environmentRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            environmentService.deleteEnvironment(env.getId());
            log.info("Environment deleted: {}", env.getName());
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            log.warn("Environment not found for deletion: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Environment not found");
        } catch (Exception e) {
            log.error("Error deleting environment {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/active/{instanceId}")
    public ResponseEntity<?> getActiveEnvironments(@PathVariable String instanceId) {
        log.info("Fetching active environments for instance {}", instanceId);
        try {
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            List<InstanceEnvironmentDTO> environments = environmentService.getActiveEnvironmentsForInstance(instance.getId());
            return ResponseEntity.ok(environments);
        } catch (NoSuchElementException e) {
            log.warn("Instance not found: {}", instanceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        } catch (Exception e) {
            log.error("Error fetching active environments for instance {}: {}", instanceId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/{environmentId}/toggle-environments")
    public ResponseEntity<?> getAllToggleEnvForEnvironment(@PathVariable String environmentId) {
        log.info("Fetching toggle environments for environment {}", environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            List<ToggleEnvironmentDTO> toggleEnvironments = environmentService.getAllToggleEnvironmentsForEnvironment(env.getId());
            return ResponseEntity.ok(toggleEnvironments);
        } catch (NoSuchElementException e) {
            log.warn("Environment not found: {}", environmentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Environment not found");
        } catch (Exception e) {
            log.error("Error fetching toggle environments for environment {}: {}", environmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}

