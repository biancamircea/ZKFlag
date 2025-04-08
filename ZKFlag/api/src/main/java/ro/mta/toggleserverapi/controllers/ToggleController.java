package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.services.*;

import java.time.ZoneId;
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
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;
    private final ToggleRepository toggleRepository;
    private final EnvironmentRepository environmentRepository;
    private final TagRepository tagRepository;


    @GetMapping(path = "/toggles")
    public ResponseEntity<?> getAllToggles(){
        TogglesResponseDTO togglesResponseDTO = toggleService.getAllToggles();
        return ResponseEntity.ok(togglesResponseDTO);
    }

    @GetMapping(path = "/toggles/{id}")
    public ResponseEntity<?> getToggle(@PathVariable String id){
        Toggle toggle2=toggleRepository.findByHashId(id).orElseThrow();
//        TODO: modify method fetchToggle to getToggle and return DTO obj
        Toggle toggle = toggleService.fetchToggle(toggle2.getId());
        return ResponseEntity
                .ok(ToggleDTO.toDTO(toggle));
    }

    @GetMapping(path = "/projects/{projectId}/toggles")
    public ResponseEntity<?> getAllTogglesByProject(@PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        TogglesResponseDTO togglesResponseDTO = toggleService.getAllTogglesFromProject(project.getId());
        return ResponseEntity.ok(togglesResponseDTO);
    }

    @GetMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> getToggleByProject(@PathVariable String projectId, @PathVariable String toggleId){
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();

        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ToggleDTO toggleDTO = toggleService.getToggleFromProject(project.getId(), toggle.getId());
        return ResponseEntity.ok(toggleDTO);
    }

    @GetMapping(path = "/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}")
    public ResponseEntity<?> getToggleEnvironment( @PathVariable String toggleId,@PathVariable String instanceId,
                                                  @PathVariable String environmentId){
        Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.getToggleEnvironment( toggle.getId(),instance.getId(), env.getId());
        return ResponseEntity.ok(toggleEnvironmentDTO);
    }


    @PostMapping(path = "/projects/{projectId}/toggles")
    public ResponseEntity<?> createToggle(@RequestBody @Valid ToggleDTO newToggleDTO, @PathVariable String projectId){
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        Toggle newToggle = ToggleDTO.fromDTO(newToggleDTO);
        Toggle createdToggle = toggleService.saveToggle(newToggle, project.getId());

        if(createdToggle == null){
            throw new RuntimeException("Could not create project!");
        } else {

            return ResponseEntity
                    .created(linkTo(methodOn(ToggleController.class).getToggle(createdToggle.getHashId())).toUri())
                    .body(ToggleDTO.toDTO(createdToggle));
        }
    }

    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/on")
    public ResponseEntity<?> toggleFeatureOn(@PathVariable String projectId, @PathVariable String toggleId,@PathVariable String instanceId,
                                             @PathVariable String environmentName){
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();

        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        toggleService.enableToggleInEnvironment(project.getId(), toggle.getId(), environmentName,instance.getId());
        return ResponseEntity.noContent().build();

    }
    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/off")
    public ResponseEntity<?> toggleFeatureOff(@PathVariable String projectId, @PathVariable String toggleId,@PathVariable String instanceId,
                                              @PathVariable String environmentName){
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();

        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        toggleService.disableToggleInEnvironment(project.getId(), toggle.getId(), environmentName,instance.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/tags/{tagId}")
    public ResponseEntity<?> addTag(@PathVariable String projectId,
                                    @PathVariable String toggleId,
                                    @PathVariable String tagId){
        Tag tag=tagRepository.findByHashId(tagId).orElseThrow();
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        toggleService.addTagToToggle(tag.getId(), toggle.getId(), project.getId());
        return ResponseEntity.ok(null);
    }


    @PostMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> addPayloadInToggleEnvironment(@RequestBody @Valid PayloadDTO payloadDTO,
                                                              @PathVariable String projectId,
                                                              @PathVariable String toggleId,
                                                              @PathVariable String instanceId,
                                                              @PathVariable String environmentId){
        Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();

        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.addPayloadInToggleEnvironment(project.getId(),
                instance.getId(),
                toggle.getId(),
                env.getId(),
                payloadDTO.getEnabledValue(),
                payloadDTO.getDisabledValue());
        return ResponseEntity.ok(toggleEnvironmentDTO);
    }

    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> updateToggle(@RequestBody @Valid ToggleDTO toggleDTO, @PathVariable String projectId, @PathVariable String toggleId){
        Toggle toggle2=toggleRepository.findByHashId(toggleId).orElseThrow();
        System.out.println("updatetoggle: name: "+toggleDTO.getName()+" type: "+toggleDTO.getToggle_type());

        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        Toggle toggle = ToggleDTO.fromDTO(toggleDTO);
        System.out.println("toggle type dupa from dto: "+toggle.getToggleType());

        Toggle updatedToggle = toggleService.updateToggle(toggle, toggle2.getId() , project.getId());
        eventService.submitAction(ActionType.UPDATE, updatedToggle.getProject(), updatedToggle);
        return ResponseEntity
                .created(linkTo(methodOn(ToggleController.class).getToggle(updatedToggle.getHashId())).toUri())
                .body(ToggleDTO.toDTO(updatedToggle));
    }
//----------------------------------------------------------------------------------------------------------------------------
        @GetMapping("/projects/{projectId}/toggles/{toggleId}/constraints")
        public ResponseEntity<List<ConstraintDTO>> getAllConstraintsForToggle(@PathVariable String projectId,
                                                                              @PathVariable String toggleId) {
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();
            List<ConstraintDTO> constraints = toggleService.getAllConstraintsForToggle(project.getId(), toggle.getId());
            return ResponseEntity.ok(constraints);
        }

        @GetMapping("/toggles/{toggleId}/instances/{instanceId}/environment/{environmentId}/constraints")
        public ResponseEntity<List<ConstraintDTO>> getAllConstraintsForInstanceEnvironment(@PathVariable String toggleId,
                                                                                           @PathVariable String instanceId,
                                                                                           @PathVariable String environmentId) {
            Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
            List<ConstraintDTO> constraints = toggleService.getAllConstraintsForInstanceEnvironment(toggle.getId(), instance.getId(), env.getId());
            return ResponseEntity.ok(constraints);
        }


        @GetMapping("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
        public ResponseEntity<ConstraintDTO> getConstraintFromToggle(@PathVariable String projectId,
                                                                     @PathVariable String toggleId,
                                                                     @PathVariable Long constraintId) {
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();
            ConstraintDTO constraintDTO = toggleService.getConstraintFromToggle(project.getId(), toggle.getId(), constraintId);
            return ResponseEntity.ok(constraintDTO);
        }


        @GetMapping("/toggles/{toggleId}/instances/{instanceId}/environment/{environmentId}/constraints/{constraintId}")
        public ResponseEntity<ConstraintDTO> getConstraintFromToggleEnvironment(@PathVariable Long constraintId,
                                                                                @PathVariable String toggleId,
                                                                                @PathVariable String instanceId,
                                                                                @PathVariable String environmentId) {
            Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
            ConstraintDTO constraintDTO = toggleService.getConstraintFromToggleEnvironment(constraintId, toggle.getId(), instance.getId(), env.getId());
            return ResponseEntity.ok(constraintDTO);
        }

        @PostMapping("/projects/{projectId}/toggles/{toggleId}/constraints")
        public ResponseEntity<ConstraintDTO> addConstraintInToggle(@RequestBody ConstraintDTO constraintDTO,
                                                                   @PathVariable String projectId,
                                                                   @PathVariable String toggleId) {
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();
            Constraint constraint = constraintService.fromDTO(constraintDTO, project.getId());
            ConstraintDTO createdConstraint = toggleService.addConstraintInToggle(constraint, project.getId(), toggle.getId());

            return ResponseEntity.created(linkTo(methodOn(ToggleController.class)
                            .getConstraintFromToggle(projectId, toggleId, createdConstraint.getId())).toUri())
                    .body(createdConstraint);
        }


        @PutMapping("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
        public ResponseEntity<ConstraintDTO> updateConstraintInToggle(@RequestBody ConstraintDTO constraintDTO,
                                                                      @PathVariable String projectId,
                                                                      @PathVariable String toggleId,
                                                                      @PathVariable Long constraintId) {
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();
            Constraint constraint = constraintService.fromDTO(constraintDTO, project.getId());
            ConstraintDTO updatedConstraint = toggleService.updateConstraintInToggle(constraint, project.getId(), toggle.getId(), constraintId);

            return ResponseEntity.ok(updatedConstraint);
        }

        @PutMapping("/projects/{projectId}/toggles/{toggleId}/environment/{environmentId}/instances/{instanceId}/constraints/{constraintId}/values")
        public ResponseEntity<ConstraintDTO> updateConstraintValuesForToggleEnvironment(
                @PathVariable String projectId,
                @PathVariable String toggleId,
                @PathVariable String instanceId,
                @PathVariable String environmentId,
                @PathVariable Long constraintId,
                @RequestBody ConstraintValueUpdateDTO newValues) {
            Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();

            Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();

            ConstraintDTO updatedConstraint = toggleService.updateConstraintValuesForToggleEnvironment(
                    project.getId(), toggle.getId(), instance.getId(), env.getId(), constraintId, newValues);

            return ResponseEntity.ok(updatedConstraint);
        }


        @DeleteMapping("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}")
        public ResponseEntity<Void> removeConstraintFromToggle(@PathVariable String projectId,
                                                               @PathVariable String toggleId,
                                                               @PathVariable Long constraintId) {
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();
            toggleService.removeConstraintFromToggleEnvironment(project.getId(), toggle.getId(), constraintId);
            return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/projects/{projectId}/toggles/{toggleId}/constraints")
        public ResponseEntity<Void> removeAllConstraintsFromToggle(@PathVariable String projectId,
                                                                   @PathVariable String toggleId) {
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
            Project project=projectRepository.findByHashId(projectId).orElseThrow();
            toggleService.removeAllConstraintsFromToggleEnvironment(project.getId(), toggle.getId());
            return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/projects/{projectId}/toggles/{toggleId}/environment/{environmentId}/instances/{instanceId}/constraints/{constraintId}/reset")
        public ResponseEntity<Void> resetConstraintValuesToDefault(@PathVariable String toggleId,
                                                                   @PathVariable String projectId,
                                                                   @PathVariable String instanceId,
                                                                   @PathVariable String environmentId,
                                                                   @PathVariable Long constraintId) {
            Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
            Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();

            Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
            toggleService.resetConstraintValuesToDefault(toggle.getId(), env.getId(), instance.getId(), constraintId);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/constraints/{constraintId}/values")
        public ResponseEntity<?> getConstraintValues(@PathVariable Long constraintId) {
            List<ConstraintValueDTO> constraintValues = toggleService.getConstraintValues( constraintId);
            ConstraintValuesResponseDTO constraintValuesResponseDTO = new ConstraintValuesResponseDTO();
            constraintValuesResponseDTO.setConstraintValues(constraintValues);
            return ResponseEntity.ok(constraintValuesResponseDTO);
        }

        //-----------------------------------------------------------------------------------------------------------------------------

    @PutMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> updatePayloadInToggleEnvironment(@RequestBody @Valid PayloadDTO payloadDTO,
                                                           @PathVariable String projectId,
                                                           @PathVariable String toggleId,
                                                           @PathVariable String instanceId,
                                                           @PathVariable String environmentId){
        Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        ToggleEnvironmentDTO toggleEnvironmentDTO = toggleService.updatePayloadInToggleEnvironment(project.getId(),
                toggle.getId(),
                env.getId(),
                instance.getId(),
                payloadDTO.getEnabledValue(),
                payloadDTO.getDisabledValue());
        return ResponseEntity.ok(toggleEnvironmentDTO);
    }

    @DeleteMapping(path = "/toggles/{id}")
    public ResponseEntity<?> deleteToggle(@PathVariable String id){
        Toggle toggle=toggleRepository.findByHashId(id).orElseThrow();
        toggleService.deleteToggle(toggle.getId());
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}")
    public ResponseEntity<?> deleteToggleByProject(@PathVariable String projectId, @PathVariable String toggleId){
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        toggleService.deleteToggleByProject(project.getId(), toggle.getId());
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/tags/{tagId}")
    public ResponseEntity<?> removeTag(@PathVariable String projectId,
                                    @PathVariable String toggleId,
                                    @PathVariable String tagId){
        Tag tag=tagRepository.findByHashId(tagId).orElseThrow();
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        toggleService.removeTagFromToggle(tag.getId(), toggle.getId(), project.getId());
        return ResponseEntity.ok(null);
    }


    @DeleteMapping(path = "/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload")
    public ResponseEntity<?> removePayloadInToggleEnvironment(@PathVariable String projectId,
                                                           @PathVariable String toggleId,
                                                           @PathVariable String instanceId,
                                                           @PathVariable String environmentId){
        Environment env=environmentRepository.findByHashId(environmentId).orElseThrow();
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Project project=projectRepository.findByHashId(projectId).orElseThrow();
        toggleService.removePayloadInToggleEnvironment(project.getId(),
                toggle.getId(),
                env.getId(),
                instance.getId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/toggles/{toggleId}/environments/{envId}/instances/{instanceId}/enabled")
    public ResponseEntity<Boolean> isToggleEnabled(
            @PathVariable String toggleId,
            @PathVariable String envId,
            @PathVariable String  instanceId) {
        Environment env=environmentRepository.findByHashId(envId).orElseThrow();
        Toggle toggle2=toggleRepository.findByHashId(toggleId).orElseThrow();
        Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
        Toggle toggle = toggleService.fetchToggleById(toggle2.getId());
        Boolean isEnabled = toggleEnvironmentService.fetchByToggleAndEnvIdAndInstanceId(toggle, env.getId(), instance.getId());
        return ResponseEntity.ok(isEnabled);
    }

    @GetMapping("/toggles/{toggleId}/getProjectId")
    public ResponseEntity<?> getProjectByToggleId(@PathVariable String toggleId) {
        System.out.println("intra aici: "+toggleId);
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        Project project = toggle.getProject();
        System.out.println("id="+project.getHashId());
        return ResponseEntity.ok(project.getHashId());
    }

    @GetMapping("/toggles/{toggleId}/getType")
    public ResponseEntity<?> getTypeByToggleId(@PathVariable String toggleId) {
        Toggle toggle=toggleRepository.findByHashId(toggleId).orElseThrow();
        return ResponseEntity.ok(toggle.getToggleType());
    }
}
