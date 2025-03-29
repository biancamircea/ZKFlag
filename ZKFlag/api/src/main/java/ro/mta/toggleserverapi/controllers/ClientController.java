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
import ro.mta.toggleserverapi.repositories.ToggleRepository;
import ro.mta.toggleserverapi.services.ApiTokenService;
import ro.mta.toggleserverapi.services.ProjectService;
import ro.mta.toggleserverapi.services.ToggleEnvironmentService;
import ro.mta.toggleserverapi.services.ToggleService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;


@AllArgsConstructor
@RequestMapping(path = "/client")
@RestController
public class ClientController {
    private final ToggleService toggleService;
    private final ApiTokenService apiTokenService;
    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);
    private final ToggleRepository toggleRepository;

    @PostMapping(path = "/evaluate")
    public ResponseEntity<?> evaluateClient(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid ClientToggleEvaluationRequestDTO clientToggleEvaluationRequestDTO) {
        System.out.println("Client Evaluation request."+clientToggleEvaluationRequestDTO.getToggleName());
        for (ClientToggleEvaluationRequestDTO.ContextFromClientDTO context : clientToggleEvaluationRequestDTO.getContextFields()) {
            System.out.println("Name context field: " + context.getName() + ", Value context field: " + context.getValue());
        }

        LOG.info("Client Evaluation request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        Toggle toggle=toggleRepository.findByNameAndProject(clientToggleEvaluationRequestDTO.getToggleName(), apiToken.getProject())
                .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
        List<Constraint> constraints=toggle.getConstraints();

        List<Long> distinctConstrGroupIds = constraints.stream()
                .map(Constraint::getConstrGroupId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        System.out.println("Distinct constr group ids size: " + distinctConstrGroupIds.size());
        ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = new ClientToggleEvaluationResponseDTO();

        for (Long constrGroupId : distinctConstrGroupIds) {
            Boolean check = toggleService.evaluateToggleInContext(
                    clientToggleEvaluationRequestDTO.getToggleName(),
                    apiTokenStr,
                    clientToggleEvaluationRequestDTO.getContextFields(),
                    constrGroupId);

            Boolean checkZKP= toggleService.evaluateProofs(clientToggleEvaluationRequestDTO.getToggleName(),
                    apiTokenStr,clientToggleEvaluationRequestDTO.getProofs(), constrGroupId);

            Boolean enable = check && checkZKP;
            System.out.println("constrGroupId: " + constrGroupId + ", check: " + check + ", checkZKP: " + checkZKP);
            if(enable){
                String payload = toggleService.getPayload(clientToggleEvaluationRequestDTO.getToggleName(), apiTokenStr, enable);

                clientToggleEvaluationResponseDTO.setEnabled(enable);
                clientToggleEvaluationResponseDTO.setPayload(payload);
                return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
            }
        }

        String payload2 = toggleService.getPayload(clientToggleEvaluationRequestDTO.getToggleName(), apiTokenStr, false);
        clientToggleEvaluationResponseDTO.setEnabled(false);
        clientToggleEvaluationResponseDTO.setPayload(payload2);

        return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
    }

    @PostMapping(path = "/constraints")
    public ResponseEntity<?> getConstraints(@RequestHeader("Authorization") String apiTokenStr, @RequestBody @Valid String toggleName) {
        System.out.println("Get constraints request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        List<ConstraintDTO> filteredConstraints = toggleService.getConstraints(apiTokenStr, toggleName);

        System.out.println("Constraints: "+filteredConstraints);

        return ResponseEntity.ok(filteredConstraints);
    }

    @PostMapping(path="/evaluateZKP")
    public ResponseEntity<?> evaluateAgeZKP(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid ClientToggleEvaluationRequestZKPDTO clientToggleEvaluationRequestZKPDTO) {

        LOG.info("Client Evaluation request.");
        System.out.println("Client Evaluation request: evaluate age ZKP.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);
        System.out.println("Client Evaluation request: apiToken checked.");

        ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = toggleService.evaluateToggleInContextZKP(
                clientToggleEvaluationRequestZKPDTO.getToggleName(),
                apiTokenStr,
                clientToggleEvaluationRequestZKPDTO.getProof());


        return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
    }

}
