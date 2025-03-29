package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ApiTokenDTO;
import ro.mta.toggleserverapi.entities.ApiToken;


@AllArgsConstructor
@Component
public class ApiTokenConverter {
    public static ApiTokenDTO toDTO(ApiToken apiToken){
        ApiTokenDTO apiTokenDTO = new ApiTokenDTO();
        apiTokenDTO.setId(apiToken.getId());
        apiTokenDTO.setName(apiToken.getName());
        apiTokenDTO.setSecret(apiToken.getSecret());
        apiTokenDTO.setCreatedAt(apiToken.getCreatedAt());
        apiTokenDTO.setExpiresAt(apiToken.getExpiresAt());

        apiTokenDTO.setProjectId(apiToken.getProject().getHashId());
        apiTokenDTO.setEnvironmentId(apiToken.getEnvironment().getHashId());
        apiTokenDTO.setEnvironmentName(apiToken.getEnvironment().getName());
        apiTokenDTO.setInstanceId(apiToken.getInstance().getHashId());
        apiTokenDTO.setType(apiToken.getType());
        return apiTokenDTO;
    }
}
