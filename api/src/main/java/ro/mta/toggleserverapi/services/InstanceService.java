package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.ApiTokensResponseConverter;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.ApiTokenNotFoundException;
import ro.mta.toggleserverapi.exceptions.ProjectNotFoundException;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.converters.ToggleConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final InstanceEnvironmentService instanceEnvironmentService;
    private final ApiTokenRepository apiTokenRepository;
    private final InstanceEnvironmentRepository instanceEnvironmentRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final ToggleConverter toggleConverter;
    private final ToggleEnvironmentRepository toggleEnvironmentRepository;

    public Instance fetchInstance(Long instanceId) {
        return instanceRepository.findById(instanceId)
                .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId));
    }

    public ApiTokensResponseDTO getInstanceApiTokens(Long instanceId) {
        Instance instance = fetchInstance(instanceId);
        List<ApiToken> apiTokens = instance.getApiTokens();
        return ApiTokensResponseConverter.toDTO(apiTokens);
    }

    public void enableInstanceEnvironment(Long instanceId, Long envId) {
        instanceEnvironmentService.enableInstanceEnvironment(instanceId, envId);
    }

    public void disableInstanceEnvironment(Long instanceId, Long envId) {
        instanceEnvironmentService.disableInstanceEnvironment(instanceId, envId);
    }

    public void deleteInstanceApiToken(Long instanceId, Long tokenId) {
        Instance instance = fetchInstance(instanceId);

        ApiToken tokenToDelete = instance.getApiTokens()
                .stream()
                .filter(token -> token.getId().equals(tokenId))
                .findFirst()
                .orElseThrow(() -> new ApiTokenNotFoundException(tokenId));

        instance.getApiTokens().remove(tokenToDelete);

        apiTokenRepository.deleteById(tokenId);
    }

    public Project fetchProjectByInstanceId(Long instanceId) {
        Instance instance = fetchInstance(instanceId);
        return instance.getProject();
    }

    public List<Instance> fetchInstancesByProject(Long projectId) {
        return instanceRepository.findAllByProjectId(projectId);
    }

    public List<Environment> fetchEnabledEnvironmentsInInstance(Instance instance) {
        List<InstanceEnvironment> activeInstanceEnvironments = instanceEnvironmentRepository
                .findActiveInstanceEnvironmentsByInstanceId(instance.getId());

        return activeInstanceEnvironments.stream()
                .map(InstanceEnvironment::getEnvironment)
                .collect(Collectors.toList());
    }

    public Instance saveInstanceForProject(Instance newInstance, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        newInstance.setProject(project);
        newInstance.setStartedAt(LocalDateTime.now());

        return instanceRepository.save(newInstance);
    }

    public void saveInstanceEnvironment(Instance instance, Environment environment){
        InstanceEnvironmentKey instanceEnvironmentKey = new InstanceEnvironmentKey();
        instanceEnvironmentKey.setInstanceId(instance.getId());
        instanceEnvironmentKey.setEnvironmentId(environment.getId());

        InstanceEnvironment instanceEnvironment = new InstanceEnvironment();
        instanceEnvironment.setId(instanceEnvironmentKey);
        instanceEnvironment.setInstance(instance);
        instanceEnvironment.setEnvironment(environment);
        instanceEnvironment.setActive(false); // Implicit dezactivat

        instance.getInstanceEnvironmentList().add(instanceEnvironment);

        instanceEnvironmentRepository.flush(); // sincronizează sesiunile curente
        instanceEnvironmentRepository.saveAndFlush(instanceEnvironment); // Salvează imediat și eliberează
    }


    public Instance createInstanceWithDefaultEnvironments(Instance newInstance, Long projectId) {
        Instance savedInstance = saveInstanceForProject(newInstance, projectId);

        if (savedInstance.getInstanceEnvironmentList() == null) {
            savedInstance.setInstanceEnvironmentList(new ArrayList<>());
        }

        List<Environment> allEnvironments = environmentRepository.findAllByIsEnabledTrue();
        if (allEnvironments.isEmpty()) {
            System.out.println("No environments found.");
        } else {
            allEnvironments.forEach(env -> System.out.println("Environment name: " + env.getName()));
        }

        for (Environment environment : allEnvironments) {
            saveInstanceEnvironment(savedInstance,environment);
        }

        return savedInstance;
    }


    public void linkInstanceToEnvironments(Instance instance, List<Long> environmentIds) {
        List<Environment> environments = environmentRepository.findAllById(environmentIds);
        for (Environment environment : environments) {
            InstanceEnvironment instanceEnvironment = new InstanceEnvironment();
            instanceEnvironment.setInstance(instance);
            instanceEnvironment.setEnvironment(environment);
            instanceEnvironment.setActive(false); // Setăm default ca inactiv
            instanceEnvironmentRepository.save(instanceEnvironment);
        }
    }

    private void addEnabledEnvToInstanceOverviewDTO(InstanceOverviewDTO instanceOverviewDTO, Instance instance) {
        List<Environment> environments = fetchEnabledEnvironmentsInInstance(instance);

        List<String> environmentsName = environments.stream()
                .map(environment -> {
                    return environment.getName();
                })
                .collect(Collectors.toList());

        instanceOverviewDTO.setEnvironments(environmentsName);
    }

    public InstanceOverviewDTO makeInstanceOverviewFromInstance(Instance instance) {
        // Creează DTO-ul cu câmpurile statice
        InstanceOverviewDTO instanceOverviewDTO = new InstanceOverviewDTO();
        instanceOverviewDTO.setId(instance.getId());
        instanceOverviewDTO.setName(instance.getName());

        // Adaugă lista de environments active
        List<String> environments = instance.getInstanceEnvironmentList()
                .stream()
                .filter(InstanceEnvironment::getActive)
                .map(env -> env.getEnvironment().getName())
                .sorted()
                .toList();
        instanceOverviewDTO.setEnvironments(environments);

        // Adaugă numărul de API tokens
        Long apiTokenCount = instance.getApiTokens() != null ? (long) instance.getApiTokens().size() : 0L;
        instanceOverviewDTO.setApiTokenCount(apiTokenCount);

        // Adaugă lista de toggles din proiectul asociat instanței
        List<ToggleDTO> toggles = instance.getProject().getToggleList()
                .stream()
                .map(toggleConverter::toDTO) // Folosim ToggleConverter pentru a transforma în ToggleDTO
                .toList();
        instanceOverviewDTO.setToggles(toggles);

        return instanceOverviewDTO;
    }

    public InstanceOverviewDTO getInstanceOverview(Long instanceId) {
        Instance instance = fetchInstance(instanceId); // Obține instanța din baza de date
        return makeInstanceOverviewFromInstance(instance);
    }

    // getAllInstancesFromProject(projectId)
    public List<InstanceOverviewDTO> getAllInstancesFromProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        List<Instance> instances = project.getInstanceList();
        return instances.stream()
                .map(this::makeInstanceOverviewFromInstance)
                .collect(Collectors.toList());
    }

    public void deleteInstance(Long projectId, Long instanceId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Instance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId));

        // Șterge toate entitățile `ApiToken` asociate cu instanța
        List<ApiToken> apiTokens = instance.getApiTokens();
        if (apiTokens != null && !apiTokens.isEmpty()) {
            apiTokenRepository.deleteAll(apiTokens);
        }

        // Șterge toate entitățile `InstanceEnvironment` asociate cu instanța
        List<InstanceEnvironment> instanceEnvironments = instance.getInstanceEnvironmentList();
        if (instanceEnvironments != null && !instanceEnvironments.isEmpty()) {
            instanceEnvironmentRepository.deleteAll(instanceEnvironments);
        }

        // Șterge toate entitățile `ToggleEnvironment` asociate cu instanța
        List<ToggleEnvironment> toggleEnvironments = instance.getToggleEnvironmentsList();
        if (toggleEnvironments != null && !toggleEnvironments.isEmpty()) {
            toggleEnvironmentRepository.deleteAll(toggleEnvironments);
        }

        // Elimină instanța din lista de instanțe a proiectului
        project.getInstanceList().remove(instance);
        projectRepository.save(project);

        // Șterge instanța în sine
        instanceRepository.delete(instance);
    }

    public List<InstanceEnvironmentDTO> getAllEnvironmentsFromInstance(Long instanceId) {
        List<InstanceEnvironment> instanceEnvironments = instanceEnvironmentRepository.findByInstanceIdAndActiveTrue(instanceId);

        return instanceEnvironments.stream()
                .map(instanceEnvironment -> {
                    InstanceEnvironmentDTO dto = InstanceEnvironmentDTO.toDTO(instanceEnvironment);

                    dto.setEnabledInstanceToggleCount(0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ToggleEnvironmentDTO> getToggleEnvironments(Long instanceId, Long toggleId) {
        List<ToggleEnvironment> toggleEnvironments = toggleEnvironmentRepository.findByInstanceIdAndToggleId(instanceId, toggleId);
        return toggleEnvironments.stream()
                .map(ToggleEnvironmentDTO::toDTO)
                .collect(Collectors.toList());
    }

}