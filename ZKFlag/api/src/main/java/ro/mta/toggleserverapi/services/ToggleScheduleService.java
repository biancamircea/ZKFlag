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

        // Schedule the task based on recurrence
        if (recurrence == null || recurrence.equalsIgnoreCase("one-time")) {
            toggleTask.scheduleToggleActivation(projectId, toggleId, environmentName, instanceId, activateAt, deactivateAt);
        } else {
            switch (recurrence.toLowerCase()) {
                case "daily":
                    scheduleDailyTask(projectId, toggleId, instanceId, environmentName, activateAt, deactivateAt);
                    break;
                case "weekly":
                    scheduleWeeklyTask(projectId, toggleId, instanceId, environmentName, activateAt, deactivateAt);
                    break;
                case "monthly":
                    scheduleMonthlyTask(projectId, toggleId, instanceId, environmentName, activateAt, deactivateAt);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid recurrence type: " + recurrence);
            }
        }

        // Save to database
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

        toggleScheduleRepository.save(toggleSchedule);
    }

    private void scheduleDailyTask(Long projectId, Long toggleId, Long instanceId, String environmentName, Instant activateAt, Instant deactivateAt) {
        Instant nextActivateAt = activateAt.plus(Duration.ofDays(1));
        Instant nextDeactivateAt = deactivateAt.plus(Duration.ofDays(1));

        toggleTask.scheduleToggleActivation(projectId, toggleId, environmentName, instanceId, nextActivateAt, nextDeactivateAt);
        // Recursively schedule the task for the next day (you can use scheduler.schedule() here with a delay for the next day)
    }

    private void scheduleWeeklyTask(Long projectId, Long toggleId, Long instanceId, String environmentName, Instant activateAt, Instant deactivateAt) {
        Instant nextActivateAt = activateAt.plus(Duration.ofDays(7));
        Instant nextDeactivateAt = deactivateAt.plus(Duration.ofDays(7));

        toggleTask.scheduleToggleActivation(projectId, toggleId, environmentName, instanceId, nextActivateAt, nextDeactivateAt);
        // Recursively schedule the task for the next week
    }

    private void scheduleMonthlyTask(Long projectId, Long toggleId, Long instanceId, String environmentName, Instant activateAt, Instant deactivateAt) {
        Instant nextActivateAt = activateAt.plus(Duration.ofDays(30));  // Simple approach: adding 30 days for a month
        Instant nextDeactivateAt = deactivateAt.plus(Duration.ofDays(30));

        toggleTask.scheduleToggleActivation(projectId, toggleId, environmentName, instanceId, nextActivateAt, nextDeactivateAt);
        // Recursively schedule the task for the next month
    }

}

