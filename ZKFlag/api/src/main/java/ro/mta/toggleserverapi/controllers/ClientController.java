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

import java.util.Collections;
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

        LOG.info("Client Evaluation request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        try {
            Toggle toggle =toggleRepository.findByNameAndProjectAndToggleType(clientToggleEvaluationRequestDTO.getToggleName(),apiToken.getProject(),Math.toIntExact(apiToken.getType()))
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            List<Constraint> constraints = toggle.getConstraints();

            ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = toggleService.combinedEvaluateToggle(
                    toggle,
                    apiToken,
                    clientToggleEvaluationRequestDTO.getContextFields(),
                    clientToggleEvaluationRequestDTO.getProofs(),
                    constraints
            );

            return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
        } catch (NoSuchElementException e) {
            ClientToggleEvaluationResponseDTO defaultResponse = new ClientToggleEvaluationResponseDTO();
            defaultResponse.setEnabled(false);
            defaultResponse.setPayload("default");
            return ResponseEntity.ok(defaultResponse);
        }
    }

    @PostMapping(path = "/constraints")
    public ResponseEntity<?> getConstraints(@RequestHeader("Authorization") String apiTokenStr, @RequestBody @Valid String toggleName) {
        System.out.println("Get constraints request.");
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        List<ConstraintDTO> filteredConstraints = toggleService.getConstraints(apiToken, toggleName);

        System.out.println("Constraints: "+filteredConstraints);

        return ResponseEntity.ok(filteredConstraints);
    }

}
