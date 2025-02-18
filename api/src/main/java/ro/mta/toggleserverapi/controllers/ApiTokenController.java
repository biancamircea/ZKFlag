package ro.mta.toggleserverapi.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ApiTokenDTO;
import ro.mta.toggleserverapi.DTOs.ApiTokensResponseDTO;
import ro.mta.toggleserverapi.services.ApiTokenService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RequestMapping(path = "/api-tokens")
@RestController
public class ApiTokenController {
    private final ApiTokenService apiTokenService;

//    methods for admin panel -> /api-tokens
    @GetMapping
    public ResponseEntity<?> getAllApiTokens(){
        ApiTokensResponseDTO apiTokensResponseDTO = apiTokenService.getAllApiTokens();
        return ResponseEntity.ok(apiTokensResponseDTO);
    }

    @GetMapping(path = "/{tokenId}")
    public ResponseEntity<?> getApiToken(@PathVariable Long tokenId){
        ApiTokenDTO apiTokenDTO = apiTokenService.getApiToken(tokenId);
        return ResponseEntity.ok(apiTokenDTO);
    }


    @PostMapping
    public ResponseEntity<?> createApiToken(@RequestBody @Valid ApiTokenDTO apiTokenDTO){
        ApiTokenDTO createdApiToken = apiTokenService.saveApiToken(apiTokenDTO);
        return ResponseEntity.created(linkTo(methodOn(ApiTokenController.class).getApiToken(createdApiToken.getId())).toUri())
                .body(createdApiToken);
    }

    @PutMapping(path = "/{tokenId}")
    public ResponseEntity<?> updateApiToken(@RequestBody @Valid ApiTokenDTO apiTokenDTO,
                                            @PathVariable Long tokenId){
//        TODO: update Api Token
        return ResponseEntity.ok(null);
    }

    @DeleteMapping(path = "/{tokenId}")
    public ResponseEntity<?> deleteApiToken(@PathVariable Long tokenId){
        apiTokenService.deleteApiToken(tokenId);
        return ResponseEntity.noContent().build();
    }


}
