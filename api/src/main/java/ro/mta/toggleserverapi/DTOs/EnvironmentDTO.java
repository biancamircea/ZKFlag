package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.InstanceEnvironment;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.enums.EnvironmentType;
import ro.mta.toggleserverapi.util.ListUtil;
import java.util.Optional;

@Data
public class EnvironmentDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private EnvironmentType type;

    private Boolean enabled;

    private Long enabledToggleCount;

    private Long instanceCount;

    private Long apiTokenCount;

    public static EnvironmentDTO toDTO(Environment environment){
        EnvironmentDTO environmentDTO = new EnvironmentDTO();
        environmentDTO.setId(environment.getId());
        environmentDTO.setName(environment.getName());
        environmentDTO.setType(environment.getType());
        environmentDTO.setEnabled(environment.getIsEnabled());

        environmentDTO.setEnabledToggleCount(
                Optional.ofNullable(environment.getToggleEnvironmentList())
                        .map(toggleEnvironments -> toggleEnvironments.stream().filter(ToggleEnvironment::getEnabled).count())
                        .orElse(0L)
        );
        environmentDTO.setInstanceCount(
                Optional.ofNullable(environment.getInstanceEnvironmentList())
                        .map(instanceEnvironments -> instanceEnvironments.stream()
                                .filter(InstanceEnvironment::getActive)
                                .count()
                        ).orElse(0L)
        );
        environmentDTO.setApiTokenCount(ListUtil.listSize(environment.getApiTokens()));
        return environmentDTO;
    }

    public static Environment fromDTO(EnvironmentDTO environmentDTO) {
        Environment environment = new Environment();
        environment.setName(environmentDTO.getName());
        environment.setType(environmentDTO.getType());
        //environment.setIsEnabled(environmentDTO.getEnabled());
        return environment;
    }
}
