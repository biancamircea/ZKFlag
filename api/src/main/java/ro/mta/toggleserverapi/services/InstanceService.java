package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.*;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.ApiTokenNotFoundException;
import ro.mta.toggleserverapi.exceptions.ProjectNotFoundException;
import ro.mta.toggleserverapi.repositories.*;

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
    private final UserInstanceService userInstanceService;
    private final UserConverter userConverter;

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

        Instance savedInstance = instanceRepository.save(newInstance);

        if (savedInstance.getHashId() == null || savedInstance.getHashId().isEmpty()) {
            Hashids hashids = new Hashids("instance-salt", 8);
            savedInstance.setHashId(hashids.encode(savedInstance.getId()));
            savedInstance = instanceRepository.save(savedInstance);
        }

        return instanceRepository.save(savedInstance);
    }

    public void saveInstanceEnvironment(Instance instance, Environment environment){
        InstanceEnvironmentKey instanceEnvironmentKey = new InstanceEnvironmentKey();
        instanceEnvironmentKey.setInstanceId(instance.getId());
        instanceEnvironmentKey.setEnvironmentId(environment.getId());

        InstanceEnvironment instanceEnvironment = new InstanceEnvironment();
        instanceEnvironment.setId(instanceEnvironmentKey);
        instanceEnvironment.setInstance(instance);
        instanceEnvironment.setEnvironment(environment);
        instanceEnvironment.setActive(false);

        instance.getInstanceEnvironmentList().add(instanceEnvironment);

        instanceEnvironmentRepository.flush();
        instanceEnvironmentRepository.saveAndFlush(instanceEnvironment);
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

    public InstanceOverviewDTO makeInstanceOverviewFromInstance(Instance instance) {
        InstanceOverviewDTO instanceOverviewDTO = new InstanceOverviewDTO();
        instanceOverviewDTO.setId(instance.getHashId());
        instanceOverviewDTO.setName(instance.getName());

        List<String> environments = instance.getInstanceEnvironmentList()
                .stream()
                .filter(InstanceEnvironment::getActive)
                .map(env -> env.getEnvironment().getName())
                .sorted()
                .toList();
        instanceOverviewDTO.setEnvironments(environments);

        Long apiTokenCount = instance.getApiTokens() != null ? (long) instance.getApiTokens().size() : 0L;
        instanceOverviewDTO.setApiTokenCount(apiTokenCount);

        List<ToggleDTO> toggles = instance.getProject().getToggleList()
                .stream()
                .map(toggleConverter::toDTO)
                .toList();
        instanceOverviewDTO.setToggles(toggles);

        List<UserDTO> users=getUsersWithInstanceAdminRole(instance.getId());
        instanceOverviewDTO.setMembers(users.stream().count());

        return instanceOverviewDTO;
    }

    public InstanceOverviewDTO getInstanceOverview(Long instanceId) {
        Instance instance = fetchInstance(instanceId);
        return makeInstanceOverviewFromInstance(instance);
    }

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

        List<ApiToken> apiTokens = instance.getApiTokens();
        if (apiTokens != null && !apiTokens.isEmpty()) {
            apiTokenRepository.deleteAll(apiTokens);
        }

        List<InstanceEnvironment> instanceEnvironments = instance.getInstanceEnvironmentList();
        if (instanceEnvironments != null && !instanceEnvironments.isEmpty()) {
            instanceEnvironmentRepository.deleteAll(instanceEnvironments);
        }

        List<ToggleEnvironment> toggleEnvironments = instance.getToggleEnvironmentsList();
        if (toggleEnvironments != null && !toggleEnvironments.isEmpty()) {
            toggleEnvironmentRepository.deleteAll(toggleEnvironments);
        }

        project.getInstanceList().remove(instance);
        projectRepository.save(project);

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

    public void addAccessToInstance(List<User> users, Long instanceId) {

        Instance instance = fetchInstance(instanceId);
        for(User user : users){
            userInstanceService.addAccessToInstance(user, instance);
        }
    }

    public List<UserDTO> getUsersWithInstanceAdminRole(Long instanceId) {
        Instance instance = fetchInstance(instanceId);
        List<UserInstance> userInstances = instance.getUserInstances();
        return userInstances.stream()
                .filter(userInstance -> userInstance.getUser().getRole().getRoleType().name().equals("InstanceAdmin"))
                .map(UserInstance::getUser)
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }
}