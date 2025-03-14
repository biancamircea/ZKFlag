package ro.mta.toggleserverapi.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ClientFeaturesDTO;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientFeaturesConverter {

    private final ConstraintConverter constraintConverter;

    @Autowired
    public ClientFeaturesConverter(ConstraintConverter constraintConverter) {
        this.constraintConverter = constraintConverter;
    }

    public static ClientFeaturesDTO toDTO(List<ToggleEnvironment> toggleEnvironments) {
        ClientFeaturesDTO clientFeaturesDTO = new ClientFeaturesDTO();

        List<ClientFeaturesDTO.ClientFeatureDTO> clientFeaturesDTOList = new ArrayList<>();
        for (ToggleEnvironment toggleEnvironment : toggleEnvironments) {
            ClientFeaturesDTO.ClientFeatureDTO clientFeatureDTO = new ClientFeaturesDTO.ClientFeatureDTO();
            clientFeatureDTO.setName(toggleEnvironment.getToggle().getName());
            clientFeatureDTO.setEnabled(toggleEnvironment.getEnabled());
            clientFeatureDTO.setEnabledValue(toggleEnvironment.getEnabledValue());
            clientFeatureDTO.setDisabledValue(toggleEnvironment.getDisabledValue());

            clientFeaturesDTOList.add(clientFeatureDTO);
        }
        clientFeaturesDTO.setClientFeatureDTOList(clientFeaturesDTOList);
        return clientFeaturesDTO;
    }
}
