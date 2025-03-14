package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.InstanceOverviewDTO;
import ro.mta.toggleserverapi.DTOs.ToggleDTO;
import ro.mta.toggleserverapi.entities.InstanceEnvironment;
import ro.mta.toggleserverapi.entities.Instance;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InstanceOverviewConverter {
    private final ToggleConverter toggleConverter;

    public InstanceOverviewDTO toDTO(Instance instance) {
        InstanceOverviewDTO instanceOverviewDTO = new InstanceOverviewDTO();
        instanceOverviewDTO.setId(instance.getHashId());
        instanceOverviewDTO.setName(instance.getName());

        List<String> environments = instance.getInstanceEnvironmentList()
                .stream()
                .filter(InstanceEnvironment::getActive)
                .map(env -> env.getEnvironment().getName())
                .sorted()
                .collect(Collectors.toList());
        instanceOverviewDTO.setEnvironments(environments);

        instanceOverviewDTO.setApiTokenCount(
                instance.getApiTokens() != null ? (long) instance.getApiTokens().size() : 0L
        );

        List<ToggleDTO> toggleDTOList = instance.getProject().getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();
        instanceOverviewDTO.setToggles(toggleDTOList);

        return instanceOverviewDTO;
    }

    private void addEnabledEnvironmentsToInstanceOverviewDTO(InstanceOverviewDTO instanceOverviewDTO, Instance instance) {
        List<String> environments = instance.getInstanceEnvironmentList()
                .stream()
                .filter(InstanceEnvironment::getActive)
                .map(env -> env.getEnvironment().getName())
                .sorted()
                .toList();

        instanceOverviewDTO.setEnvironments(environments);
    }

    private void addTogglesToInstanceOverviewDTO(InstanceOverviewDTO instanceOverviewDTO, Instance instance) {
        List<ToggleDTO> toggleDTOList = instance.getProject().getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();

        instanceOverviewDTO.setToggles(toggleDTOList);
    }
}
