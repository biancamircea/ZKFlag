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
        instanceDTO.setId(instance.getId());
        instanceDTO.setName(instance.getName());
        instanceDTO.setStartedAt(instance.getStartedAt());
        instanceDTO.setProjectId(instance.getProject() != null ? instance.getProject().getId() : null); // Setăm projectId
        return instanceDTO;
    }

    public static Instance fromDTO(InstanceDTO instanceDTO) {
        Instance instance = new Instance();
        instance.setId(instanceDTO.getId());
        instance.setName(instanceDTO.getName());
        instance.setStartedAt(instanceDTO.getStartedAt());
        // Note: `Project` trebuie setat separat, deoarece implică o relație mai complexă
        return instance;
    }
}
