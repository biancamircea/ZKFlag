package ro.mta.toggleserverapi.DTOs;

import lombok.Data;
import ro.mta.toggleserverapi.entities.InstanceEnvironment;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.enums.EnvironmentType;

@Data
public class InstanceEnvironmentDTO {
    private String id;

    private String name;

    private EnvironmentType type;

    private Boolean enabled;

    private Integer enabledInstanceToggleCount;
    private String enabledValue;
    private String disabledValue;
//    TODO: add projectAPITokenCount

    public static InstanceEnvironmentDTO toDTO(InstanceEnvironment  instanceEnvironment){
        InstanceEnvironmentDTO instanceEnvironmentDTO = new InstanceEnvironmentDTO();
        instanceEnvironmentDTO.setId( instanceEnvironment.getEnvironment().getHashId());
        instanceEnvironmentDTO.setName( instanceEnvironment.getEnvironment().getName());
        instanceEnvironmentDTO.setType( instanceEnvironment.getEnvironment().getType());
        instanceEnvironmentDTO.setEnabled( instanceEnvironment.getActive());

        return instanceEnvironmentDTO;
    }
}
