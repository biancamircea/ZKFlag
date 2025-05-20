package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestZKPDTO;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationResponseDTO;
import ro.mta.toggleserverapi.DTOs.ConstraintDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.services.ApiTokenService;
import ro.mta.toggleserverapi.services.ProjectService;
import ro.mta.toggleserverapi.services.ToggleEnvironmentService;
import ro.mta.toggleserverapi.services.ToggleService;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/client")
@RestController
public class ClientController {
    private final ToggleService toggleService;
    private final ApiTokenService apiTokenService;
    private final ToggleRepository toggleRepository;
    private final ToggleEnvironmentRepository toggleEnvironmentRepository;

    @PostMapping(path = "/evaluate")
    public ResponseEntity<?> evaluateClient(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid ClientToggleEvaluationRequestDTO clientToggleEvaluationRequestDTO) {

        log.info("Received evaluation request for toggle '{}'", clientToggleEvaluationRequestDTO.getToggleName());
        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);

        try {
            Toggle toggle =toggleRepository.findByNameAndProjectAndToggleType(clientToggleEvaluationRequestDTO.getToggleName(),apiToken.getProject(),Math.toIntExact(apiToken.getType()))
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            ToggleEnvironment toggleEnvironment = toggleEnvironmentRepository.findByToggleIdAndEnvironmentNameAndInstanceId(toggle.getId(),apiToken.getEnvironment().getName(),
                    apiToken.getInstance().getId()).orElseThrow(() -> new NoSuchElementException("Toggle environment not found"));

            List<Constraint> constraints = toggle.getConstraints();

            log.debug("Evaluating toggle '{}' for instance '{}', environment '{}'",
                    toggle.getName(), apiToken.getInstance().getName(), apiToken.getEnvironment().getName());

            ClientToggleEvaluationResponseDTO clientToggleEvaluationResponseDTO = toggleService.combinedEvaluateToggle(
                    toggle,
                    apiToken,
                    clientToggleEvaluationRequestDTO.getContextFields(),
                    clientToggleEvaluationRequestDTO.getProofs(),
                    constraints
            );

            if(clientToggleEvaluationResponseDTO.getEnabled()){
                Integer nr_true = toggleEnvironment.getEvaluated_true_count();
                log.info("Toggle '{}' evaluated as ENABLED (true). Count updated to {}", toggle.getName(), nr_true + 1);
                toggleEnvironment.setEvaluated_true_count(nr_true+1);
                toggleEnvironmentRepository.save(toggleEnvironment);
            }else{
                Integer nr_false = toggleEnvironment.getEvaluated_false_count();
                log.info("Toggle '{}' evaluated as DISABLED (false). Count updated to {}", toggle.getName(), nr_false + 1);
                toggleEnvironment.setEvaluated_false_count(nr_false+1);
                toggleEnvironmentRepository.save(toggleEnvironment);
            }

            return ResponseEntity.ok(clientToggleEvaluationResponseDTO);
        } catch (NoSuchElementException e) {
            log.warn("Evaluation failed: {}", e.getMessage());
            ClientToggleEvaluationResponseDTO defaultResponse = new ClientToggleEvaluationResponseDTO();
            defaultResponse.setEnabled(false);
            defaultResponse.setPayload("default");
            return ResponseEntity.ok(defaultResponse);
        }

    }

    @PostMapping(path = "/constraints")
    public ResponseEntity<?> getConstraints(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid String toggleName) {
        log.info("Received request to fetch constraints for toggle '{}'", toggleName);

        try {
            ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);
            List<ConstraintDTO> filteredConstraints = toggleService.getConstraints(apiToken, toggleName);

            log.debug("Fetched {} constraints for toggle '{}'", filteredConstraints.size(), toggleName);
            return ResponseEntity.ok(filteredConstraints);

        } catch (NoSuchElementException e) {
            log.warn("Toggle '{}' not found or access denied", toggleName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Toggle not found or inaccessible");

        } catch (AuthenticationException e) {
            log.warn("Invalid API token received");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API token");

        } catch (Exception e) {
            log.error("Unexpected error while fetching constraints for toggle '{}': {}", toggleName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }


}
