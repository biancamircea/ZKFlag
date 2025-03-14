package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.InstanceDTO;
import ro.mta.toggleserverapi.entities.Instance;

@AllArgsConstructor
@Component
public class InstanceConverter {
    public static InstanceDTO toDTO(Instance instance) {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(instance.getHashId());
        instanceDTO.setName(instance.getName());
        instanceDTO.setStartedAt(instance.getStartedAt());
        instanceDTO.setProjectId(instance.getProject() != null ? instance.getProject().getHashId() : null); // Setăm projectId
        return instanceDTO;
    }

    public static Instance fromDTO(InstanceDTO instanceDTO) {
        Instance instance = new Instance();
        instance.setHashId(instanceDTO.getId());
        instance.setName(instanceDTO.getName());
        instance.setStartedAt(instanceDTO.getStartedAt());
        return instance;
    }
}
