package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.services.*;

import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@Slf4j
public class ToggleController {
    private final ToggleService toggleService;
    private final ToggleEnvironmentService toggleEnvironmentService;
    private final ConstraintService constraintService;
    private final EventService eventService;
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;
    private final ToggleRepository toggleRepository;
    private final EnvironmentRepository environmentRepository;
    private final TagRepository tagRepository;
    private final ToggleEnvironmentRepository toggleEnvironmentRepository;


    @GetMapping(path = "/toggles")
    public ResponseEntity<?> getAllToggles() {
        log.info("Fetching all toggles");
        try {
            TogglesResponseDTO togglesResponseDTO = toggleService.getAllToggles();
            return ResponseEntity.ok(togglesResponseDTO);
        } catch (Exception e) {
            log.error("Error fetching toggles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @GetMapping(path = "/toggles/{id}")
    public ResponseEntity<?> getToggle(@PathVariable String id) {
        log.info("Fetching toggle with ID {}", id);
        try {
            Toggle toggle2 = toggleRepository.findByHashId(id)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found: " + id));
            Toggle toggle = toggleService.fetchToggle(toggle2.getId());
            return ResponseEntity.ok(ToggleDTO.toDTO(toggle));
        } catch (NoSuchElementException e) {
            log.warn("Toggle not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching toggle {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @GetMapping(path = "/projects/{projectId}/toggles")
    public ResponseEntity<?> getAllTogglesByProject(@PathVariable String projectId) {
        log.info("Fetching toggles for project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            TogglesResponseDTO togglesResponseDTO = toggleService.getAllTogglesFromProject(project.getId());
            return ResponseEntity.ok(togglesResponseDTO);
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching toggles for project {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> getToggleByProject(@PathVariable String projectId, @PathVariable String toggleId) {
        log.info("Fetching toggle {} for project {}", toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found: " + toggleId));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            ToggleDTO toggleDTO = toggleService.getToggleFromProject(project.getId(), toggle.getId());
            return ResponseEntity.ok(toggleDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching toggle {} for project {}: {}", toggleId, projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @GetMapping(path = "/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}")
    public ResponseEntity<?> getToggleEnvironment(@PathVariable String toggleId,
                                                  @PathVariable String instanceId,
                                                  @PathVariable String environmentId) {
        log.info("Fetching toggle environment for toggle {}, instance {}, environment {}", toggleId, instanceId, environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found: " + environmentId));
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found: " + toggleId));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found: " + instanceId));
            ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.getToggleEnvironment(toggle.getId(), instance.getId(), env.getId());
            return ResponseEntity.ok(toggleEnvironmentDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching toggle environment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping(path = "/projects/{projectId}/toggles")
    public ResponseEntity<?> createToggle(@RequestBody @Valid ToggleDTO newToggleDTO, @PathVariable String projectId) {
        log.info("Creating new toggle in project {}", projectId);
        try {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
            Toggle newToggle = ToggleDTO.fromDTO(newToggleDTO);
            Toggle createdToggle = toggleService.saveToggle(newToggle, project.getId());

            if (createdToggle == null) {
                throw new RuntimeException("Toggle creation returned null");
            }

            return ResponseEntity
                    .created(linkTo(methodOn(ToggleController.class).getToggle(createdToggle.getHashId())).toUri())
                    .body(ToggleDTO.toDTO(createdToggle));
        } catch (NoSuchElementException e) {
            log.warn("Project not found: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/on")
    public ResponseEntity<?> toggleFeatureOn(@PathVariable String projectId, @PathVariable String toggleId,
                                             @PathVariable String instanceId, @PathVariable String environmentName) {
        log.info("Enabling toggle {} in environment {} for instance {}", toggleId, environmentName, instanceId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));

            toggleService.enableToggleInEnvironment(project.getId(), toggle.getId(), environmentName, instance.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error enabling toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/off")
    public ResponseEntity<?> toggleFeatureOff(@PathVariable String projectId, @PathVariable String toggleId,
                                              @PathVariable String instanceId, @PathVariable String environmentName) {
        log.info("Disabling toggle {} in environment {} for instance {}", toggleId, environmentName, instanceId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));

            toggleService.disableToggleInEnvironment(project.getId(), toggle.getId(), environmentName, instance.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error disabling toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/tags/{tagId}")
    public ResponseEntity<?> addTag(@PathVariable String projectId,
                                    @PathVariable String toggleId,
                                    @PathVariable String tagId) {
        log.info("Adding tag {} to toggle {} in project {}", tagId, toggleId, projectId);
        try {
            Tag tag = tagRepository.findByHashId(tagId)
                    .orElseThrow(() -> new NoSuchElementException("Tag not found"));
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));

            toggleService.addTagToToggle(tag.getId(), toggle.getId(), project.getId());
            return ResponseEntity.ok("Tag added successfully");
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error adding tag to toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> addPayloadInToggleEnvironment(@RequestBody @Valid PayloadDTO payloadDTO,
                                                           @PathVariable String projectId,
                                                           @PathVariable String toggleId,
                                                           @PathVariable String instanceId,
                                                           @PathVariable String environmentId) {
        log.info("Adding payload for toggle {} in environment {} for instance {}", toggleId, environmentId, instanceId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId)
                    .orElseThrow(() -> new NoSuchElementException("Environment not found"));
            Toggle toggle = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Instance instance = instanceRepository.findByHashId(instanceId)
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));

            ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.addPayloadInToggleEnvironment(
                    project.getId(),
                    instance.getId(),
                    toggle.getId(),
                    env.getId(),
                    payloadDTO.getEnabledValue(),
                    payloadDTO.getDisabledValue());

            return ResponseEntity.ok(toggleEnvironmentDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error adding payload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> updateToggle(@RequestBody @Valid ToggleDTO toggleDTO,
                                          @PathVariable String projectId,
                                          @PathVariable String toggleId) {
        log.info("Updating toggle {} in project {}", toggleId, projectId);
        try {
            Toggle toggle2 = toggleRepository.findByHashId(toggleId)
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));
            Toggle toggle = ToggleDTO.fromDTO(toggleDTO);

            Toggle updatedToggle = toggleService.updateToggle(toggle, toggle2.getId(), project.getId());
            eventService.submitAction(ActionType.UPDATE, updatedToggle.getProject(), updatedToggle);
            return ResponseEntity
                    .created(linkTo(methodOn(ToggleController.class).getToggle(updatedToggle.getHashId())).toUri())
                    .body(ToggleDTO.toDTO(updatedToggle));
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/projects/{projectId}/toggles/{toggleId}/constraints")
    public ResponseEntity<List<ConstraintDTO>> getAllConstraintsForToggle(@PathVariable String projectId,
                                                                          @PathVariable String toggleId) {
        log.info("Fetching all constraints for toggle {} in project {}", toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            List<ConstraintDTO> constraints = toggleService.getAllConstraintsForToggle(project.getId(), toggle.getId());
            return ResponseEntity.ok(constraints);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error fetching constraints: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/toggles/{toggleId}/instances/{instanceId}/environment/{environmentId}/constraints")
    public ResponseEntity<List<ConstraintDTO>> getAllConstraintsForInstanceEnvironment(@PathVariable String toggleId,
                                                                                       @PathVariable String instanceId,
                                                                                       @PathVariable String environmentId) {
        log.info("Fetching constraints for toggle {} in instance {} and environment {}", toggleId, instanceId, environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();
            List<ConstraintDTO> constraints = toggleService.getAllConstraintsForInstanceEnvironment(toggle.getId(), instance.getId(), env.getId());
            return ResponseEntity.ok(constraints);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error fetching constraints: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
    public ResponseEntity<ConstraintDTO> getConstraintFromToggle(@PathVariable String projectId,
                                                                 @PathVariable String toggleId,
                                                                 @PathVariable Long constraintId) {
        log.info("Fetching constraint {} for toggle {} in project {}", constraintId, toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            ConstraintDTO constraintDTO = toggleService.getConstraintFromToggle(project.getId(), toggle.getId(), constraintId);
            return ResponseEntity.ok(constraintDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error fetching constraint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/toggles/{toggleId}/instances/{instanceId}/environment/{environmentId}/constraints/{constraintId}")
    public ResponseEntity<ConstraintDTO> getConstraintFromToggleEnvironment(@PathVariable Long constraintId,
                                                                            @PathVariable String toggleId,
                                                                            @PathVariable String instanceId,
                                                                            @PathVariable String environmentId) {
        log.info("Fetching constraint {} for toggle {} in instance {} and environment {}", constraintId, toggleId, instanceId, environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();
            ConstraintDTO constraintDTO = toggleService.getConstraintFromToggleEnvironment(constraintId, toggle.getId(), instance.getId(), env.getId());
            return ResponseEntity.ok(constraintDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error fetching constraint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/projects/{projectId}/toggles/{toggleId}/constraints")
    public ResponseEntity<ConstraintDTO> addConstraintInToggle(@RequestBody ConstraintDTO constraintDTO,
                                                               @PathVariable String projectId,
                                                               @PathVariable String toggleId) {
        log.info("Adding constraint to toggle {} in project {}", toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            Constraint constraint = constraintService.fromDTO(constraintDTO, project.getId());
            ConstraintDTO createdConstraint = toggleService.addConstraintInToggle(constraint, project.getId(), toggle.getId());

            return ResponseEntity.created(linkTo(methodOn(ToggleController.class)
                            .getConstraintFromToggle(projectId, toggleId, createdConstraint.getId())).toUri())
                    .body(createdConstraint);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error adding constraint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PutMapping("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
    public ResponseEntity<ConstraintDTO> updateConstraintInToggle(@RequestBody ConstraintDTO constraintDTO,
                                                                  @PathVariable String projectId,
                                                                  @PathVariable String toggleId,
                                                                  @PathVariable Long constraintId) {
        log.info("Updating constraint {} in toggle {} for project {}", constraintId, toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            Constraint constraint = constraintService.fromDTO(constraintDTO, project.getId());
            ConstraintDTO updatedConstraint = toggleService.updateConstraintInToggle(constraint, project.getId(), toggle.getId(), constraintId);

            return ResponseEntity.ok(updatedConstraint);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error updating constraint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/projects/{projectId}/toggles/{toggleId}/environment/{environmentId}/instances/{instanceId}/constraints/{constraintId}/values")
    public ResponseEntity<ConstraintDTO> updateConstraintValuesForToggleEnvironment(
            @PathVariable String projectId,
            @PathVariable String toggleId,
            @PathVariable String instanceId,
            @PathVariable String environmentId,
            @PathVariable Long constraintId,
            @RequestBody ConstraintValueUpdateDTO newValues) {
        log.info("Updating constraint values for toggle {} in instance {} and environment {}", toggleId, instanceId, environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();

            ConstraintDTO updatedConstraint = toggleService.updateConstraintValuesForToggleEnvironment(
                    project.getId(), toggle.getId(), instance.getId(), env.getId(), constraintId, newValues);

            return ResponseEntity.ok(updatedConstraint);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error updating constraint values: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @DeleteMapping("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
    public ResponseEntity<Void> removeConstraintFromToggle(@PathVariable String projectId,
                                                           @PathVariable String toggleId,
                                                           @PathVariable Long constraintId) {
        log.info("Removing constraint {} from toggle {} in project {}", constraintId, toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            toggleService.removeConstraintFromToggleEnvironment(project.getId(), toggle.getId(), constraintId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error removing constraint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/projects/{projectId}/toggles/{toggleId}/constraints")
    public ResponseEntity<Void> removeAllConstraintsFromToggle(@PathVariable String projectId,
                                                               @PathVariable String toggleId) {
        log.info("Removing all constraints from toggle {} in project {}", toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            toggleService.removeAllConstraintsFromToggleEnvironment(project.getId(), toggle.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error removing all constraints: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/projects/{projectId}/toggles/{toggleId}/environment/{environmentId}/instances/{instanceId}/constraints/{constraintId}/reset")
    public ResponseEntity<Void> resetConstraintValuesToDefault(@PathVariable String toggleId,
                                                               @PathVariable String projectId,
                                                               @PathVariable String instanceId,
                                                               @PathVariable String environmentId,
                                                               @PathVariable Long constraintId) {
        log.info("Resetting constraint {} to default for toggle {}, instance {}, environment {}", constraintId, toggleId, instanceId, environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();

            toggleService.resetConstraintValuesToDefault(toggle.getId(), env.getId(), instance.getId(), constraintId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error resetting constraint values: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/constraints/{constraintId}/values")
    public ResponseEntity<?> getConstraintValues(@PathVariable Long constraintId) {
        log.info("Fetching constraint values for constraint {}", constraintId);
        try {
            List<ConstraintValueDTO> constraintValues = toggleService.getConstraintValues(constraintId);
            ConstraintValuesResponseDTO response = new ConstraintValuesResponseDTO();
            response.setConstraintValues(constraintValues);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching constraint values: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------

    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> updatePayloadInToggleEnvironment(@RequestBody @Valid PayloadDTO payloadDTO,
                                                              @PathVariable String projectId,
                                                              @PathVariable String toggleId,
                                                              @PathVariable String instanceId,
                                                              @PathVariable String environmentId) {
        log.info("Updating payload for toggle {} in project {}, instance {}, environment {}", toggleId, projectId, instanceId, environmentId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();

            ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.updatePayloadInToggleEnvironment(
                    project.getId(), toggle.getId(), env.getId(), instance.getId(),
                    payloadDTO.getEnabledValue(), payloadDTO.getDisabledValue());

            return ResponseEntity.ok(toggleEnvironmentDTO);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating payload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @DeleteMapping(path = "/toggles/{id}")
    public ResponseEntity<?> deleteToggle(@PathVariable String id) {
        log.info("Deleting toggle with ID {}", id);
        try {
            Toggle toggle = toggleRepository.findByHashId(id).orElseThrow();
            toggleService.deleteToggle(toggle.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Toggle not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toggle not found");
        } catch (Exception e) {
            log.error("Error deleting toggle {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> deleteToggleByProject(@PathVariable String projectId, @PathVariable String toggleId) {
        log.info("Deleting toggle {} from project {}", toggleId, projectId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            toggleService.deleteToggleByProject(project.getId(), toggle.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting toggle from project: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/tags/{tagId}")
    public ResponseEntity<?> removeTag(@PathVariable String projectId,
                                       @PathVariable String toggleId,
                                       @PathVariable String tagId) {
        log.info("Removing tag {} from toggle {} in project {}", tagId, toggleId, projectId);
        try {
            Tag tag = tagRepository.findByHashId(tagId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();
            toggleService.removeTagFromToggle(tag.getId(), toggle.getId(), project.getId());
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error removing tag: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> removePayloadInToggleEnvironment(@PathVariable String projectId,
                                                              @PathVariable String toggleId,
                                                              @PathVariable String instanceId,
                                                              @PathVariable String environmentId) {
        log.info("Removing payload for toggle {} in instance {}, environment {}, project {}", toggleId, instanceId, environmentId, projectId);
        try {
            Environment env = environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();
            Project project = projectRepository.findByHashId(projectId).orElseThrow();

            toggleService.removePayloadInToggleEnvironment(project.getId(), toggle.getId(), env.getId(), instance.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error removing payload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/toggles/{toggleId}/environments/{envId}/instances/{instanceId}/enabled")
    public ResponseEntity<Boolean> isToggleEnabled(@PathVariable String toggleId,
                                                   @PathVariable String envId,
                                                   @PathVariable String instanceId) {
        log.info("Checking if toggle {} is enabled in environment {} for instance {}", toggleId, envId, instanceId);
        try {
            Environment env = environmentRepository.findByHashId(envId).orElseThrow();
            Toggle toggle2 = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();

            Toggle toggle = toggleService.fetchToggleById(toggle2.getId());
            Boolean isEnabled = toggleEnvironmentService.fetchByToggleAndEnvIdAndInstanceId(toggle, env.getId(), instance.getId());
            return ResponseEntity.ok(isEnabled);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            log.error("Error checking if toggle is enabled: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }


    @GetMapping("/toggles/{toggleId}/getProjectId")
    public ResponseEntity<?> getProjectByToggleId(@PathVariable String toggleId) {
        log.info("Getting project for toggle {}", toggleId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            return ResponseEntity.ok(toggle.getProject().getHashId());
        } catch (NoSuchElementException e) {
            log.warn("Toggle not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toggle not found");
        } catch (Exception e) {
            log.error("Error getting project by toggle ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/toggles/{toggleId}/getType")
    public ResponseEntity<?> getTypeByToggleId(@PathVariable String toggleId) {
        log.info("Getting type for toggle {}", toggleId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            return ResponseEntity.ok(toggle.getToggleType());
        } catch (NoSuchElementException e) {
            log.warn("Toggle not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toggle not found");
        } catch (Exception e) {
            log.error("Error getting toggle type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    @GetMapping("/toggles/{toggleId}/instances/{instanceId}/statistics")
    public ResponseEntity<?> getStatisticsByToggleIdAndInstanceId(@PathVariable String toggleId,
                                                                  @PathVariable String instanceId) {
        log.info("Fetching toggle statistics for toggle {} and instance {}", toggleId, instanceId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();

            List<ToggleEnvironment> toggleEnvs = toggleEnvironmentRepository.findByInstanceIdAndToggleId(instance.getId(), toggle.getId());
            List<ToggleEnvStatisticsDTO> response = toggleEnvs.stream()
                    .map(env -> {
                        int trueCount = env.getEvaluated_true_count();
                        int falseCount = env.getEvaluated_false_count();
                        double truePct = trueCount + falseCount > 0 ? (double) trueCount / (trueCount + falseCount) : 0;
                        double falsePct = trueCount + falseCount > 0 ? (double) falseCount / (trueCount + falseCount) : 0;

                        ToggleEnvStatisticsDTO dto = new ToggleEnvStatisticsDTO();
                        dto.setEnvironmentId(env.getEnvironment().getHashId());
                        dto.setTrueCount(trueCount);
                        dto.setFalseCount(falseCount);
                        dto.setTruePercentage(truePct);
                        dto.setFalsePercentage(falsePct);
                        dto.setEnvironmentName(env.getEnvironment().getName());
                        return dto;
                    }).toList();

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toggle or instance not found");
        } catch (Exception e) {
            log.error("Error fetching toggle statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping("/toggles/{toggleId}/statistics")
    public ResponseEntity<?> getStatisticsByToggleId(@PathVariable String toggleId) {
        log.info("Fetching global statistics for toggle {}", toggleId);
        try {
            Toggle toggle = toggleRepository.findByHashId(toggleId).orElseThrow();
            List<ToggleEnvironment> toggleEnvs = toggleEnvironmentRepository.findAllByToggleId(toggle.getId());

            int totalTrue = toggleEnvs.stream().mapToInt(ToggleEnvironment::getEvaluated_true_count).sum();
            int totalFalse = toggleEnvs.stream().mapToInt(ToggleEnvironment::getEvaluated_false_count).sum();

            ToggleEnvStatisticsDTO response = new ToggleEnvStatisticsDTO();
            response.setTrueCount(totalTrue);
            response.setFalseCount(totalFalse);

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            log.warn("Toggle not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toggle not found");
        } catch (Exception e) {
            log.error("Error fetching toggle statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

}
