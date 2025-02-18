package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.ApiTokensResponseDTO;
import ro.mta.toggleserverapi.entities.ApiToken;

import java.util.List;

public class ApiTokensResponseConverter {
    public static ApiTokensResponseDTO toDTO(List<ApiToken> apiTokens){
        ApiTokensResponseDTO apiTokensResponseDTO = new ApiTokensResponseDTO();
        apiTokensResponseDTO.setApiTokenDTOList(
                apiTokens
                        .stream()
                        .map(ApiTokenConverter::toDTO)
                        .toList()
        );
        return apiTokensResponseDTO;
    }
}
