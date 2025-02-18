package ro.mta.toggleserverapi.converters;

import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ToggleScheduleDTO;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;

@Component
public class ToggleScheduleConverter {
    public ToggleScheduleDTO toDTO(ToggleEnvironment toggleEnvironment) {
        ToggleScheduleDTO dto = new ToggleScheduleDTO();
        dto.setToggleId(toggleEnvironment.getToggle().getId());
        dto.setEnvironmentId(toggleEnvironment.getEnvironment().getId());
        dto.setStartOn(toggleEnvironment.getStartOn());
        dto.setStartOff(toggleEnvironment.getStartOff());
        dto.setStartDate(toggleEnvironment.getStartDate());
        dto.setEndDate(toggleEnvironment.getEndDate());
        return dto;
    }
}
