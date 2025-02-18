package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ApiTokenDTO;
import ro.mta.toggleserverapi.DTOs.ApiTokensResponseDTO;
import ro.mta.toggleserverapi.converters.ApiTokenConverter;
import ro.mta.toggleserverapi.converters.ApiTokensResponseConverter;
import ro.mta.toggleserverapi.entities.ApiToken;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.ProjectRole;
import ro.mta.toggleserverapi.exceptions.ApiTokenNotFoundException;
import ro.mta.toggleserverapi.exceptions.ApiTokenNotValidException;
import ro.mta.toggleserverapi.repositories.ApiTokenRepository;
import ro.mta.toggleserverapi.util.BinaryToHex;
import ro.mta.toggleserverapi.entities.Instance;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@AllArgsConstructor
@Service
public class ApiTokenService {
    private final ApiTokenRepository apiTokenRepository;
    private final ProjectService projectService;
    private final EnvironmentService environmentService;
    private final InstanceService instanceService;
    private final InstanceEnvironmentService instanceEnvironmentService;

    public ApiToken checkApiToken(String secret){
        String[] parts = secret.split(":");

        Long projectId = Long.parseLong(parts[0]);
        Long instanceId = Long.parseLong(parts[1]);
        String[] subParts = parts[2].split("\\.");
        Long environmentId = Long.parseLong(subParts[0]);

        System.out.println("Project ID: " + projectId);
        System.out.println("Instance ID: " + instanceId);
        System.out.println("Environment ID: " + environmentId);

        //if instance_environemnt for this instanceId and environmentId is false then throw exception
        if(!instanceEnvironmentService.isActive(instanceId, environmentId)){
            throw new ApiTokenNotValidException();
        }
        return apiTokenRepository.findBySecret(secret)
                .orElseThrow(() -> new ApiTokenNotValidException());
    }

    public ApiToken fetchApiTokenById(Long id){
        return apiTokenRepository.findById(id)
                .orElseThrow(() -> new ApiTokenNotFoundException(id));
    }
    public ApiTokensResponseDTO getAllApiTokens() {
        List<ApiToken> apiTokens = apiTokenRepository.findAll();
        return ApiTokensResponseConverter.toDTO(apiTokens);
    }

    public ApiTokenDTO getApiToken(Long tokenId) {
        ApiToken apiToken = fetchApiTokenById(tokenId);
        return ApiTokenConverter.toDTO(apiToken);
    }

    private String createSecret(Long projectId, Long environmentId,Long instanceId){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String hash = BinaryToHex.bytesToHex(bytes);
        String secret = projectId + ":"+instanceId+":" + environmentId + "." + hash;
        return secret;
    }

    public ApiToken createApiToken(ApiTokenDTO apiTokenDTO){
        ApiToken apiToken = new ApiToken();
        //print projectId, environmentId, instanceId
        System.out.println("projectId: "+apiTokenDTO.getProjectId());
        System.out.println("environmentId: "+apiTokenDTO.getEnvironmentId());

        Project project = projectService.fetchProject(apiTokenDTO.getProjectId());
        Environment environment = environmentService.fetchEnvironment(apiTokenDTO.getEnvironmentId());
        Instance instance = instanceService.fetchInstance(apiTokenDTO.getInstanceId());

        apiToken.setName(apiTokenDTO.getName());
        apiToken.setCreatedAt(LocalDateTime.now());
//        apiToken.setExpiresAt(LocalDateTime.now().plusMonths(6));

        apiToken.setProject(project);
        apiToken.setEnvironment(environment);
        apiToken.setInstance(instance);

        apiToken.setSecret(createSecret(project.getId(), environment.getId(),instance.getId()));
        return apiTokenRepository.save(apiToken);
    }

    public ApiTokenDTO saveApiToken(ApiTokenDTO apiTokenDTO) {
        ApiToken apiToken = createApiToken(apiTokenDTO);
        return ApiTokenConverter.toDTO(apiToken);
    }

    public void deleteApiToken(Long tokenId) {
        apiTokenRepository.deleteById(tokenId);
    }


}
