package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.EnvironmentNotFoundException;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.exceptions.InstanceNotFoundException;


import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class InstanceEnvironmentService {
    private final InstanceEnvironmentRepository instanceEnvironmentRepository;
    private final InstanceRepository instanceRepository;
    private final EnvironmentRepository environmentRepository;


    public void saveInstanceEnvironment(Instance instance, Environment environment){
        InstanceEnvironmentKey instanceEnvironmentKey = new InstanceEnvironmentKey();
        instanceEnvironmentKey.setInstanceId(instance.getId());
        instanceEnvironmentKey.setEnvironmentId(environment.getId());

        InstanceEnvironment instanceEnvironment = new InstanceEnvironment();
        instanceEnvironment.setId(instanceEnvironmentKey);
        instanceEnvironment.setEnvironment(environment);
        instanceEnvironment.setInstance(instance);
        instanceEnvironment.setActive(environment.getIsEnabled());

        instanceEnvironmentRepository.save(instanceEnvironment);
    }

    public void createInstanceEnvironments(Environment environment){
//        get all projects
        List<Instance> instanceList = instanceRepository.findAll();
//        for every project create a link with environment
        for(Instance instance : instanceList){
            saveInstanceEnvironment(instance, environment);
        }
    }

    public void createInstanceEnvironments(Instance instance){
//        get all projects
        List<Environment> environmentList = environmentRepository.findAllByIsEnabledTrue();
//        for every enabled env, create a link with project
        for(Environment environment : environmentList){
            saveInstanceEnvironment(instance, environment);
        }
    }

    public void deleteInstanceEnvironments(Environment environment){
        instanceEnvironmentRepository.deleteAllByEnvironment(environment);
    }

    public List<Environment> fetchEnvironmentsByInstance(Long instance_id){
        return instanceEnvironmentRepository
                .findAllByInstanceId(instance_id)
                .stream()
                .map(instanceEnvironment -> {
                    return instanceEnvironment.getEnvironment();
                })
                .collect(Collectors.toList());
    }

    public List<InstanceEnvironment> fetchInstanceEnvironmentsByInstance(Long instance_id){
        return instanceEnvironmentRepository.findAllByInstanceId(instance_id);
    }

    public Instance fetchInstanceById(Long instance_id){
        return instanceRepository.findById(instance_id)
                .orElseThrow(() -> new InstanceNotFoundException(instance_id));
    }

    public List<Environment> fetchAllEnvByInstanceIdAndActiveTrue(Long instance_id){
        return fetchAllByInstanceIdAndActiveTrue(instance_id)
                .stream()
                .map(instanceEnvironment -> {
                    return instanceEnvironment.getEnvironment();
                })
                .collect(Collectors.toList());
    }

    public List<InstanceEnvironment> fetchAllByInstanceIdAndActiveTrue(Long instance_id){
        return instanceEnvironmentRepository.findAllByInstanceIdAndActiveTrue(instance_id);
    }

    public List<InstanceEnvironment> fetchAllByInstanceId(Long instance_id){
        return instanceEnvironmentRepository.findAllByInstanceId(instance_id);
    }

    public List<Environment> fetchAllEnvByEnabled(){
        return environmentRepository.findAllByIsEnabledTrue();
    }

    public InstanceEnvironment fetchByInstanceIdAndEnvironmentId(Long instanceId, Long envId){
        return instanceEnvironmentRepository.findByInstanceIdAndEnvironmentId(instanceId, envId)
                .orElseThrow(() -> new EnvironmentNotFoundException(envId));
    }

    @Transactional
    public void enableInstanceEnvironment(Long instanceId, Long envId) {
        InstanceEnvironment instanceEnvironment = fetchByInstanceIdAndEnvironmentId(instanceId, envId);
        instanceEnvironment.setActive(Boolean.TRUE);
        //eventService.submitAction(ActionType.ENABLE, instanceEnvironment.getInstance(), instanceEnvironment.getEnvironment());
    }

    @Transactional
    public void disableInstanceEnvironment(Long instanceId, Long envId) {
        InstanceEnvironment instanceEnvironment = fetchByInstanceIdAndEnvironmentId(instanceId, envId);
        instanceEnvironment.setActive(Boolean.FALSE);
        //eventService.submitAction(ActionType.DISABLE, projectEnvironment.getProject(), projectEnvironment.getEnvironment());
    }

    public InstanceEnvironment save(InstanceEnvironment instanceEnvironment) {
        return instanceEnvironmentRepository.save(instanceEnvironment);
    }

    public List<InstanceEnvironment> fetchAllByEnvironmentId(Long environmentId) {
        return instanceEnvironmentRepository.findAllByEnvironmentId(environmentId);
    }

    public boolean isActive(Long instanceId, Long envId){
        return instanceEnvironmentRepository.findByInstanceIdAndEnvironmentId(instanceId, envId)
                .map(InstanceEnvironment::getActive)
                .orElse(false);
    }
}
