package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.UserInstanceDTO;
import ro.mta.toggleserverapi.entities.UserInstance;

public class InstanceUserConverter {
    public static UserInstanceDTO toDTO(UserInstance userInstance){
        UserInstanceDTO userInstanceDTO = new UserInstanceDTO();
        userInstanceDTO.setId(userInstance.getUser().getHashId());
        userInstanceDTO.setUserName(userInstance.getUser().getEmail());
        userInstanceDTO.setInstanceName(userInstance.getInstance().getName());
        userInstanceDTO.setInstanceId(userInstance.getInstance().getHashId());
        return userInstanceDTO;
    }
}
