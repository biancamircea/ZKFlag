package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationResponseDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.services.ApiTokenService;
import ro.mta.toggleserverapi.services.ProjectService;
import ro.mta.toggleserverapi.services.ToggleEnvironmentService;
import ro.mta.toggleserverapi.services.ToggleService;

import java.util.List;
import java.util.NoSuchElementException;


@AllArgsConstructor
@RequestMapping(path = "/client")
@RestController
public class ClientController {
    private final ToggleService toggleService;
    private final ApiTokenService apiTokenService;
    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);
    private final ToggleEnvironmentService toggleEnvironmentService;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final InstanceRepository instanceRepository;

    @PostMapping(path = "/evaluate")
    public ResponseEntity<?> evaluateClient(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid ClientToggleEvaluationRequestDTO clientToggleEvaluationRequestDTO) {
        System.out.println("Client Evaluation request."+clientToggleEvaluationRequestDTO.getToggleName());
        for (ClientToggleEvaluationRequestDTO.ContextFromClientDTO context : clientToggleEvaluationRequestDTO.getContextFields()) {
            System.out.println("Name context field: " + context.getName() + ", Value context field: " + context.getValue());
        }


        LOG.info("Client Evaluation request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = toggleService.evaluateToggleInContext(
                clientToggleEvaluationRequestDTO.getToggleName(),
                apiTokenStr,
                clientToggleEvaluationRequestDTO.getContextFields());
        LOG.info("Client Evaluation processed.");
        return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
    }

    @GetMapping(path = "/constraints")
    public ResponseEntity<?> getConstraints(@RequestHeader("Authorization") String apiTokenStr, @RequestParam("toggleName") String toggleName) {
        LOG.info("Get constraints request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        if(apiTokenStr.contains("Bearer")){
            apiTokenStr = apiTokenStr.replace("Bearer ", "");
        }
        String[] parts =apiTokenStr.split(":");

        String projectId = parts[0];
        String instanceId =parts[1];
        String environmentId = parts[2];

        Long projectIdLong = projectRepository.findByHashId(projectId).orElseThrow().getId();
        Long environmentIdLong = environmentRepository.findByHashId(environmentId).orElseThrow().getId();
        Long instanceIdLong = instanceRepository.findByHashId(instanceId).orElseThrow().getId();

        List<Toggle> toggles=toggleService.fetchAllTogglesByProjectId(projectIdLong);
        Toggle targetToggle = toggles.stream()
                .filter(toggle -> toggle.getName().equals(toggleName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Toggle not found with name: " + toggleName));

        ToggleEnvironment toggleEnvironment= toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(targetToggle.getId(),environmentIdLong, instanceIdLong);


        List<Constraint> filteredConstraints = targetToggle.getConstraints().stream().map(constraint -> {
            List<ConstraintValue> specificValues = constraint.getValues().stream()
                    .filter(cv -> cv.getToggleEnvironment() != null && toggleEnvironment.getId().equals(cv.getToggleEnvironment().getId()))
                    .toList();

            List<ConstraintValue> defaultValues = constraint.getValues().stream()
                    .filter(cv -> cv.getToggleEnvironment() == null)
                    .toList();

            List<ConstraintValue> selectedValues = !specificValues.isEmpty() ? specificValues : defaultValues;

            constraint.setValues(selectedValues);
            return constraint;
        }).toList();

        return ResponseEntity.ok(filteredConstraints);
    }
}
