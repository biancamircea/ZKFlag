package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ApiTokenDTO;
import ro.mta.toggleserverapi.converters.ApiTokenConverter;
import ro.mta.toggleserverapi.entities.ApiToken;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.exceptions.ApiTokenNotValidException;
import ro.mta.toggleserverapi.repositories.ApiTokenRepository;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.util.BinaryToHex;
import ro.mta.toggleserverapi.entities.Instance;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ApiTokenService {
    private final ApiTokenRepository apiTokenRepository;
    private final InstanceEnvironmentService instanceEnvironmentService;
    private final EnvironmentRepository environmentRepository;
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;

    public ApiToken checkApiToken(String secret){
        if(secret.contains("Bearer")){
            secret = secret.replace("Bearer ", "");
        }
        String[] parts =secret.split(":");

        String projectId = parts[0];
        String instanceId =parts[1];
        String environmentId = parts[2];
        String[] subParts = parts[3].split("\\.");
        Integer toggleType = Integer.parseInt(subParts[0]);

        Instance instance = instanceRepository.findByHashId(instanceId).orElseThrow();
        Environment environment = environmentRepository.findByHashId(environmentId).orElseThrow();

        if(!instanceEnvironmentService.isActive(instance.getId(), environment.getId())){
            throw new ApiTokenNotValidException();
        }

        return apiTokenRepository.findBySecret(secret)
                .orElseThrow(() -> new ApiTokenNotValidException());
    }


    private String createSecret(String projectId, String environmentId,String instanceId, Long type){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String hash = BinaryToHex.bytesToHex(bytes);
        String secret = projectId + ":"+instanceId+":" + environmentId +":" +type+ "." + hash;
        return secret;
    }

    public ApiToken createApiToken(ApiTokenDTO apiTokenDTO){
        ApiToken apiToken = new ApiToken();

        Project project = projectRepository.findByHashId(apiTokenDTO.getProjectId()).orElseThrow();
        Environment environment=environmentRepository.findByHashId(apiTokenDTO.getEnvironmentId()).orElseThrow();
        Instance instance = instanceRepository.findByHashId(apiTokenDTO.getInstanceId()).orElseThrow();

        apiToken.setName(apiTokenDTO.getName());
        apiToken.setCreatedAt(LocalDateTime.now());

        apiToken.setProject(project);
        apiToken.setEnvironment(environment);
        apiToken.setInstance(instance);
        apiToken.setType(apiTokenDTO.getType());

        apiToken.setSecret(createSecret(project.getHashId(), environment.getHashId(),instance.getHashId(), apiTokenDTO.getType()));
        return apiTokenRepository.save(apiToken);
    }

    public ApiTokenDTO saveApiToken(ApiTokenDTO apiTokenDTO) {
        ApiToken apiToken = createApiToken(apiTokenDTO);
        return ApiTokenConverter.toDTO(apiToken);
    }
}
