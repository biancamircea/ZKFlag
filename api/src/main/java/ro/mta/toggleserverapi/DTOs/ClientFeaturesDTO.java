package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClientFeaturesDTO {
    @JsonProperty("features")
    private List<ClientFeatureDTO> clientFeatureDTOList;

    @Data
    public static class ClientFeatureDTO {
        private String name;
        private boolean enabled;
        private String enabledValue;
        private String disabledValue;
        private List<ConstraintDTO> constraintsList;
        private Long instanceId;
        private String environmentName;
    }
}
