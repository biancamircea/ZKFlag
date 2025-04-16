package ro.mta.toggleserverapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kagkarlsson.scheduler.Scheduler;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ScheduleDTO;
import ro.mta.toggleserverapi.DTOs.ScheduleInfoDTO;
import ro.mta.toggleserverapi.DTOs.ToggleScheduleHistoryDto;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.ScheduleType;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.util.ToggleTask;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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

        if (activateAt != null) {
            toggleSchedule.setActivateAt(activateAt);
        }
        if( deactivateAt != null) {
            toggleSchedule.setDeactivateAt(deactivateAt);
        }
        toggleSchedule.setProject(project);
        toggleSchedule.setScheduleType(ScheduleType.valueOf(recurrence));

        toggleScheduleRepository.save(toggleSchedule);
    }

    public void cancelScheduledToggle(String taskInstanceId) {
        System.out.println("Canceling scheduled toggle for task instance ID: " + taskInstanceId);
        scheduler.getScheduledExecutions().stream()
                .filter(execution -> execution.getTaskInstance().getId().equals(taskInstanceId))
                .forEach(execution -> scheduler.cancel(execution.getTaskInstance()));
    }


    public List<ScheduleInfoDTO> getSchedulesForToggle(Long toggleId, Long instanceId, String environmentName) {
        return scheduler.getScheduledExecutions().stream()
                .filter(execution -> {
                    String taskInstance = execution.getTaskInstance().getId();
                    System.out.println("TaskInstance: " + taskInstance);
                    String[] parts = taskInstance.split("-");

                    if (parts.length < 7) return false;

                    try {
                        Long taskToggleId = Long.parseLong(parts[1]);
                        Long taskInstanceId = Long.parseLong(parts[3]);
                        String taskEnv = parts[5];

                        return taskToggleId.equals(toggleId)
                                && taskInstanceId.equals(instanceId)
                                && taskEnv.equals(environmentName);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .map(execution -> {
                    String taskInstance = execution.getTaskInstance().getId();
                    String[] parts = taskInstance.split("-");

                    ScheduleInfoDTO info = new ScheduleInfoDTO();
                    if(execution.getTaskInstance().getTaskName().contains("deactivate")){
                        info.setTaskType("deactivate");
                    }else{
                        info.setTaskType("activate");
                    }
                    info.setTaskInstanceId(taskInstance);
                    info.setExecutionTime(execution.getExecutionTime());

                    if (parts.length > 7) {
                        info.setRecurenceType(parts[7]);
                    }
                    return info;
                })
                .collect(Collectors.toList());
    }

    public List<ToggleScheduleHistoryDto> getToggleScheduleHistory(Long environmentId, Long instanceId, Long toggleId) {
        List<ToggleSchedule> schedules = toggleScheduleRepository
                .findHistoryByEnvironmentInstanceAndToggle(environmentId, instanceId, toggleId);

        return schedules.stream()
                .map(schedule -> new ToggleScheduleHistoryDto(
                        schedule.getId(),
                        schedule.getActivateAt(),
                        schedule.getDeactivateAt(),
                        schedule.getEnvironment().getHashId(),
                        schedule.getInstance().getHashId(),
                        schedule.getToggle().getHashId(),
                        schedule.getProject().getHashId(),
                        schedule.getEnvironment().getName(),
                        schedule.getRecurrenceCount(),
                        schedule.getScheduleType().toString()))
                .collect(Collectors.toList());
    }

}

