package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.enums.EnvironmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ToggleEnvironmentDTO {
    private Long id;

    @JsonProperty("name")
    private String environmentName;

    private Boolean enabled;

    private EnvironmentType type;

    private String enabledValue;

    private String disabledValue;

    @JsonProperty("constraints")
    private List<ConstraintDTO> constraintDTOList;

    private String instanceId;
    private String environmentId;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startOn;
    private LocalTime startOff;


    public static ToggleEnvironmentDTO toDTO(ToggleEnvironment toggleEnvironment) {
        ToggleEnvironmentDTO toggleEnvironmentDTO = new ToggleEnvironmentDTO();
        toggleEnvironmentDTO.setId(toggleEnvironment.getId());
        toggleEnvironmentDTO.setEnvironmentName(toggleEnvironment.getEnvironment().getName());
        toggleEnvironmentDTO.setEnabled(toggleEnvironment.getEnabled());
        toggleEnvironmentDTO.setType(toggleEnvironment.getEnvironment().getType());
        toggleEnvironmentDTO.setEnabledValue(toggleEnvironment.getEnabledValue());
        toggleEnvironmentDTO.setDisabledValue(toggleEnvironment.getDisabledValue());
        toggleEnvironmentDTO.setInstanceId(
                toggleEnvironment.getInstance() != null ? toggleEnvironment.getInstance().getHashId() : null
        );
        toggleEnvironmentDTO.setEnvironmentId(toggleEnvironment.getEnvironment()!=null?toggleEnvironment.getEnvironment().getHashId():null);
        toggleEnvironmentDTO.setStartDate(toggleEnvironment.getStartDate());
        toggleEnvironmentDTO.setEndDate(toggleEnvironment.getEndDate());
        toggleEnvironmentDTO.setStartOn(toggleEnvironment.getStartOn());
        toggleEnvironmentDTO.setStartOff(toggleEnvironment.getStartOff());

        return toggleEnvironmentDTO;
    }
}
