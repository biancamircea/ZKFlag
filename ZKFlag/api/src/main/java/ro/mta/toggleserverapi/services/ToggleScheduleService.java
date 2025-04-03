package ro.mta.toggleserverapi.services;

import com.github.kagkarlsson.scheduler.Scheduler;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ScheduleType;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.util.ToggleTask;

import java.time.Duration;
import java.time.Instant;

@Service
@AllArgsConstructor
public class ToggleScheduleService {

    private final Scheduler scheduler;
    private final ToggleTask toggleTask;
    private final ToggleScheduleRepository toggleScheduleRepository;
    private final ToggleRepository toggleRepository;
    private final InstanceRepository instanceRepository;
    private final EnvironmentRepository environmentRepository;
    private final ProjectRepository projectRepository;

//    @Transactional
//    public void scheduleToggleActivation(Long projectId,Long toggleId, Long instanceId, String environmentName, Instant activateAt, Instant deactivateAt) {
//        System.out.println("Scheduling toggle activation for toggleId: " + toggleId + ", instanceId: " + instanceId + ", environmentName: " + environmentName);
//        toggleTask.scheduleToggleActivation(projectId,toggleId,environmentName, instanceId , activateAt, deactivateAt);
//
//        Toggle toggle = toggleRepository.findById(toggleId).orElseThrow(() -> new RuntimeException("Toggle not found"));
//        Instance instance = instanceRepository.findById(instanceId).orElseThrow(() -> new RuntimeException("Instance not found"));
//        Environment environment = environmentRepository.findByName(environmentName);
//        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
//
//        ToggleSchedule toggleSchedule = new ToggleSchedule();
//        toggleSchedule.setToggle(toggle);
//        toggleSchedule.setInstance(instance);
//        toggleSchedule.setEnvironment(environment);
//        toggleSchedule.setActivateAt(activateAt);
//        toggleSchedule.setDeactivateAt(deactivateAt);
//        toggleSchedule.setProject(project);
//
//
//        toggleScheduleRepository.save(toggleSchedule);
//    }


    @Transactional
    public void scheduleToggleActivation(Long projectId, Long toggleId, Long instanceId, String environmentName, Instant activateAt, Instant deactivateAt, String recurrence) {
        System.out.println("Scheduling toggle activation for toggleId: " + toggleId + ", instanceId: " + instanceId + ", environmentName: " + environmentName);

        toggleTask.scheduleToggleActivation(projectId, toggleId, environmentName, instanceId, activateAt, deactivateAt,recurrence);

        Toggle toggle = toggleRepository.findById(toggleId).orElseThrow(() -> new RuntimeException("Toggle not found"));
        Instance instance = instanceRepository.findById(instanceId).orElseThrow(() -> new RuntimeException("Instance not found"));
        Environment environment = environmentRepository.findByName(environmentName);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        ToggleSchedule toggleSchedule = new ToggleSchedule();
        toggleSchedule.setToggle(toggle);
        toggleSchedule.setInstance(instance);
        toggleSchedule.setEnvironment(environment);
        toggleSchedule.setActivateAt(activateAt);
        toggleSchedule.setDeactivateAt(deactivateAt);
        toggleSchedule.setProject(project);
        toggleSchedule.setScheduleType(ScheduleType.valueOf(recurrence));

        toggleScheduleRepository.save(toggleSchedule);
    }

}

