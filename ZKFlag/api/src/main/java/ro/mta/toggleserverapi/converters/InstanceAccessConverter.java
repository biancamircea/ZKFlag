package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.ProjectAccessDTO;
import ro.mta.toggleserverapi.entities.UserInstance;
import ro.mta.toggleserverapi.entities.UserProject;
import ro.mta.toggleserverapi.DTOs.InstanceAccessDTO;

import java.util.List;

public class InstanceAccessConverter {
    public static InstanceAccessDTO toDTO(List<UserInstance> users){
        InstanceAccessDTO instanceAccessDTO = new InstanceAccessDTO();
        instanceAccessDTO.setUsers(
                users.stream()
                        .map(InstanceUserConverter::toDTO)
                        .toList()
        );
        return instanceAccessDTO;
    }
}
