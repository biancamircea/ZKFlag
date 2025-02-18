package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.InstanceEnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.ProjectEnvironmentDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.exceptions.EnvironmentNotFoundException;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceEnvironmentRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EnvironmentService {
    private final EnvironmentRepository environmentRepository;
    private final InstanceService instanceService;
    private final InstanceEnvironmentService instanceEnvironmentService;
    private final ToggleEnvironmentService toggleEnvironmentService;
    private final EventService eventService;
    private final InstanceEnvironmentRepository instanceEnvironmentRepository;

    public List<Environment> fetchAllEnvironments() {
        return environmentRepository.findAll();
    }

    public Environment fetchEnvironment(Long id){
        return environmentRepository.findById(id)
                .orElseThrow(() -> new EnvironmentNotFoundException(id));
    }

    public Environment saveEnvironment(Environment newEnv) {
        return environmentRepository.save(newEnv);
    }

    public Environment updateEnvironment(Environment env, Long id) {
        return environmentRepository.findById(id)
                .map(environment -> {
                    environment.setType(env.getType());
                    return environmentRepository.save(environment);
                })
                .orElseGet(() -> environmentRepository.save(env));
    }

    public void deleteEnvironment(Long id){
        environmentRepository.deleteById(id);
    }


    @Transactional
    public void toggleEnvironmentOn(Long id) {
        Environment environment = fetchEnvironment(id);

        if (Boolean.TRUE.equals(environment.getIsEnabled())) {
            throw new IllegalStateException("Environment is already enabled.");
        }

        instanceEnvironmentService.createInstanceEnvironments(environment);

        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentService.fetchAllByEnvironmentId(environment.getId());
        for (InstanceEnvironment instanceEnvironment : instanceEnvironments) {
            //instanceEnvironment.setActive(false);
            instanceEnvironmentService.save(instanceEnvironment);
        }

        environment.setIsEnabled(Boolean.TRUE);
        environmentRepository.save(environment);

        eventService.submitAction(ActionType.ENABLE, environment);
    }


    @Transactional
    public void toggleEnvironmentOff(Long id) {
        // Obține mediul
        Environment environment = fetchEnvironment(id);

        // Verifică dacă mediul este deja dezactivat
        if (Boolean.FALSE.equals(environment.getIsEnabled())) {
            throw new IllegalStateException("Environment is already disabled.");
        }

        // Dezactivează legăturile dintre mediu și instanțe
        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentService.fetchAllByEnvironmentId(environment.getId());
        for (InstanceEnvironment instanceEnvironment : instanceEnvironments) {
            if (Boolean.TRUE.equals(instanceEnvironment.getActive())) {
                instanceEnvironment.setActive(false);
                instanceEnvironmentService.save(instanceEnvironment); // Persistăm modificarea
            }
        }
        toggleEnvironmentService.deleteByEnvironmentId(id);

        // Dezactivează mediul general
        environment.setIsEnabled(Boolean.FALSE);
        environmentRepository.save(environment); // Salvăm modificarea

        // Generează evenimentul
        eventService.submitAction(ActionType.DISABLE, environment);
    }


    public List<InstanceEnvironmentDTO> fetchInstanceEnvironments(Long instanceId) {
        // Obține instanța după ID
        Instance instance = instanceService.fetchInstance(instanceId);

        // Obține toate legăturile `InstanceEnvironment` pentru instanță
        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentService.fetchAllByInstanceId(instanceId);

        // Creează lista DTO-urilor
        List<InstanceEnvironmentDTO> instanceEnvironmentDTOList = instanceEnvironments.stream()
                .map(instanceEnvironment -> {
                    // Convertește fiecare legătură într-un DTO
                    InstanceEnvironmentDTO instanceEnvironmentDTO = InstanceEnvironmentDTO.toDTO(instanceEnvironment);

                    // Obține numărul de toggles activate pentru acest mediu în instanța curentă
                    Long enabledToggleCount = toggleEnvironmentService.getEnabledTogglesCountInEnvironmentAndInstance(
                            instanceEnvironment.getEnvironment(),
                            instance
                    );

                    // Setează numărul de toggles active
                    instanceEnvironmentDTO.setEnabledInstanceToggleCount(Math.toIntExact(enabledToggleCount));

                    return instanceEnvironmentDTO;
                })
                .toList();

        return instanceEnvironmentDTOList;
    }

    public List<InstanceEnvironmentDTO> getActiveEnvironmentsForInstance(Long instanceId) {
        List<Environment> activeEnvironments = environmentRepository.findByEnabledTrue();
        List<InstanceEnvironment> instanceEnvs = instanceEnvironmentRepository.findByInstanceId(instanceId);

        Map<Long, Boolean> activeEnvMap = instanceEnvs.stream()
                .collect(Collectors.toMap(
                        env -> env.getEnvironment().getId(),
                        InstanceEnvironment::getActive
                ));

        return activeEnvironments.stream().map(env -> {
            InstanceEnvironmentDTO dto = new InstanceEnvironmentDTO();
            dto.setId(env.getId());
            dto.setName(env.getName());
            dto.setType(env.getType());
            dto.setEnabled(activeEnvMap.getOrDefault(env.getId(), false)); // implicit false dacă nu există
            return dto;
        }).collect(Collectors.toList());
    }

}
