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
        private String name; // Numele toggle-ului
        private boolean enabled; // Statusul toggle-ului
        private String enabledValue; // Valoarea când este activ
        private String disabledValue; // Valoarea când este inactiv
        private List<ConstraintDTO> constraintsList; // Lista de constrângeri
        private Long instanceId; // ID-ul instanței
        private String environmentName; // Numele mediului
    }
}
