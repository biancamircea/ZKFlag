package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationResponseDTO;
import ro.mta.toggleserverapi.entities.ApiToken;
import ro.mta.toggleserverapi.services.ApiTokenService;
import ro.mta.toggleserverapi.services.ToggleService;


@AllArgsConstructor
@RequestMapping(path = "/client")
@RestController
public class ClientController {
    private final ToggleService toggleService;
    private final ApiTokenService apiTokenService;
    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

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
}
