package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestZKPDTO;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationResponseDTO;
import ro.mta.toggleserverapi.DTOs.ConstraintDTO;
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

    @PostMapping(path = "/constraints")
    public ResponseEntity<?> getConstraints(@RequestHeader("Authorization") String apiTokenStr, @RequestBody @Valid String toggleName) {
        System.out.println("Get constraints request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        List<ConstraintDTO> filteredConstraints = toggleService.getConstraints(apiTokenStr, toggleName);

        System.out.println("Constraints: "+filteredConstraints);
        Long threshold = Long.parseLong(filteredConstraints.get(0).getValues().get(0));
        System.out.println("Threshold: "+threshold);

        return ResponseEntity.ok(filteredConstraints);
    }

    @PostMapping(path="/evaluateZKP")
    public ResponseEntity<?> evaluateAgeZKP(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid ClientToggleEvaluationRequestZKPDTO clientToggleEvaluationRequestZKPDTO) {

        LOG.info("Client Evaluation request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = toggleService.evaluateToggleInContextZKP(
                clientToggleEvaluationRequestZKPDTO.getToggleName(),
                apiTokenStr,
                clientToggleEvaluationRequestZKPDTO.getProof());


        return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
    }

}
