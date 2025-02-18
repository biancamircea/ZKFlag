package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ToggleEnvironmentDTO;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;

@AllArgsConstructor
@Component
public class ToggleEnvironmentConverter {

    public ToggleEnvironmentDTO toDTO(ToggleEnvironment toggleEnvironment) {
        ToggleEnvironmentDTO toggleEnvironmentDTO = new ToggleEnvironmentDTO();

        // Setează ID-ul și numele environment-ului
        toggleEnvironmentDTO.setId(toggleEnvironment.getId());
        toggleEnvironmentDTO.setEnvironmentName(toggleEnvironment.getEnvironment().getName());

        // Setează starea activării (enabled/disabled)
        toggleEnvironmentDTO.setEnabled(toggleEnvironment.getEnabled());
        toggleEnvironmentDTO.setEnabledValue(toggleEnvironment.getEnabledValue());
        toggleEnvironmentDTO.setDisabledValue(toggleEnvironment.getDisabledValue());
        toggleEnvironmentDTO.setType(toggleEnvironment.getEnvironment().getType());

        // Setează instanceId
        toggleEnvironmentDTO.setInstanceId(
                toggleEnvironment.getInstance() != null ? toggleEnvironment.getInstance().getId() : null
        );

        // Setează datele și orele
        toggleEnvironmentDTO.setStartDate(toggleEnvironment.getStartDate());
        toggleEnvironmentDTO.setEndDate(toggleEnvironment.getEndDate());
        toggleEnvironmentDTO.setStartOn(toggleEnvironment.getStartOn());
        toggleEnvironmentDTO.setStartOff(toggleEnvironment.getStartOff());


        return toggleEnvironmentDTO;
    }

}
