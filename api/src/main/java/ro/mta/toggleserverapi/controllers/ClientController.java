package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ClientFeaturesDTO;
import ro.mta.toggleserverapi.DTOs.ClientRegistrationDTO;
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

    //TODO: DE INTELES CE FACE ACEST CLIENT
//    @GetMapping(path = "/features")
//    public ResponseEntity<?> getAllClientFeatures(@RequestHeader("Authorization") String apiTokenStr){
//        LOG.info("Client Features request.");
//        ApiToken apiToken = apiTokenService.checkApiToken(apiTokenStr);
//        ClientFeaturesDTO clientFeaturesDTO = toggleService.getClientFeatures(apiToken.getEnvironment().getId(), apiToken.getProject().getId());
//        LOG.info("Client Features fetched.");
//        return ResponseEntity.ok(clientFeaturesDTO);
//    }
//    @PostMapping(path = "/register")
//    public ResponseEntity<?> registerClient(@RequestBody @Valid ClientRegistrationDTO clientRegistrationDTO){
//        applicationService.registerClient(clientRegistrationDTO.getAppName(),
//                clientRegistrationDTO.getInstanceId(),
//                clientRegistrationDTO.getStarted());
//        LOG.info("Client Registration processed.");
//        return ResponseEntity.noContent().build();
//    }
    @PostMapping(path = "/evaluate")
    public ResponseEntity<?> evaluateClient(@RequestHeader("Authorization") String apiTokenStr,
                                            @RequestBody @Valid ClientToggleEvaluationRequestDTO clientToggleEvaluationRequestDTO){
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
