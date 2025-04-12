package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.InstanceEnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.ProjectEnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.ToggleEnvironmentDTO;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.exceptions.EnvironmentNotFoundException;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceEnvironmentRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.ToggleEnvironmentRepository;

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
    private final ToggleEnvironmentRepository toggleEnvironmentRepository;

    public List<Environment> fetchAllEnvironments() {
        return environmentRepository.findAll();
    }

    public Environment fetchEnvironment(Long id){
        return environmentRepository.findById(id)
                .orElseThrow(() -> new EnvironmentNotFoundException(id));
    }

    public Environment saveEnvironment(Environment newEnv) {
        Environment savedEnv=environmentRepository.save(newEnv);

        if (savedEnv.getHashId() == null || savedEnv.getHashId().isEmpty()) {
            Hashids hashids = new Hashids("environment-salt", 8);
            savedEnv.setHashId(hashids.encode(savedEnv.getId()));

            savedEnv = environmentRepository.save(savedEnv);
        }

        return savedEnv;
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
        Environment environment = fetchEnvironment(id);

        if (Boolean.FALSE.equals(environment.getIsEnabled())) {
            throw new IllegalStateException("Environment is already disabled.");
        }

        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentService.fetchAllByEnvironmentId(environment.getId());
        for (InstanceEnvironment instanceEnvironment : instanceEnvironments) {
            if (Boolean.TRUE.equals(instanceEnvironment.getActive())) {
                instanceEnvironment.setActive(false);
                instanceEnvironmentService.save(instanceEnvironment);
            }
        }
        toggleEnvironmentService.deleteByEnvironmentId(id);

        environment.setIsEnabled(Boolean.FALSE);
        environmentRepository.save(environment);

        eventService.submitAction(ActionType.DISABLE, environment);
    }


    public List<InstanceEnvironmentDTO> fetchInstanceEnvironments(Long instanceId) {
        Instance instance = instanceService.fetchInstance(instanceId);
        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentService.fetchAllByInstanceId(instanceId);

        List<InstanceEnvironmentDTO> instanceEnvironmentDTOList = instanceEnvironments.stream()
                .map(instanceEnvironment -> {
                    InstanceEnvironmentDTO instanceEnvironmentDTO = InstanceEnvironmentDTO.toDTO(instanceEnvironment);

                    Long enabledToggleCount = toggleEnvironmentService.getEnabledTogglesCountInEnvironmentAndInstance(
                            instanceEnvironment.getEnvironment(),
                            instance
                    );

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
            dto.setId(env.getHashId());
            dto.setName(env.getName());
            dto.setType(env.getType());
            dto.setEnabled(activeEnvMap.getOrDefault(env.getId(), false));
            return dto;
        }).collect(Collectors.toList());
    }

    public Environment fetchEnvironmentByName(String name) {
        return  environmentRepository.findByName(name);
    }

    public List<ToggleEnvironmentDTO> getAllToggleEnvironmentsForEnvironment(Long envId){
        List<ToggleEnvironment> toggleEnvironments=toggleEnvironmentRepository.findAllByEnvironmentId(envId);
       System.out.println("toggleEnvironments rezultate: "+toggleEnvironments);
        List<ToggleEnvironmentDTO> toggleEnvironmentDTOList = new ArrayList<>();
        for (ToggleEnvironment toggleEnvironment : toggleEnvironments) {
            ToggleEnvironmentDTO toggleEnvironmentDTO = ToggleEnvironmentDTO.toDTO(toggleEnvironment);
            toggleEnvironmentDTOList.add(toggleEnvironmentDTO);
        }

        return toggleEnvironmentDTOList;
    }
}
