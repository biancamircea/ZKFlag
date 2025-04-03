package ro.mta.toggleserverapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import ro.mta.toggleserverapi.DTOs.ScheduleDTO;

import java.time.*;

@Component
@AllArgsConstructor
public class ToggleTask {

    private final Scheduler scheduler;
    private final OneTimeTask<String> activateToggleTask;
    private final OneTimeTask<String> deactivateToggleTask;
    private final ObjectMapper objectMapper;

    public void scheduleToggleActivation(Long projectId, Long toggleId, String environmentName, Long instanceId, Instant activateAt, Instant deactivateAt, String recurrence) {
        try {
            ScheduleDTO toggleScheduleDTO = new ScheduleDTO();
            toggleScheduleDTO.setActivateAt(activateAt);
            toggleScheduleDTO.setDeactivateAt(deactivateAt);
            toggleScheduleDTO.setToggleId(toggleId);
            toggleScheduleDTO.setInstanceId(instanceId);
            toggleScheduleDTO.setEnvironmentName(environmentName);
            toggleScheduleDTO.setProjectId(projectId);

            System.out.println("toggle schedule DTO: " + toggleId + " " + toggleScheduleDTO.getActivateAt() + " " + toggleScheduleDTO.getDeactivateAt());

            String taskData = objectMapper.writeValueAsString(toggleScheduleDTO);
            System.out.println("task data: " + taskData);

            String taskInstance = "toggle-" + toggleId;

            scheduler.schedule(activateToggleTask.instance(taskInstance, taskData), activateAt);
            scheduler.schedule(deactivateToggleTask.instance(taskInstance, taskData), deactivateAt);

            switch (recurrence != null ? recurrence.toLowerCase() : "one-time") {
                case "daily":
                    scheduleDailyTask( toggleId, activateAt, deactivateAt, taskData);
                    break;
                case "weekly":
                    scheduleWeeklyTask(toggleId,  activateAt, deactivateAt, taskData);
                    break;
                case "monthly":
                    scheduleMonthlyTask(toggleId, activateAt, deactivateAt, taskData);
                    break;
                default:
                    System.out.println("No recurrence or invalid recurrence, using one-time scheduling.");
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ToggleScheduleDTO", e);
        }
    }

    private void scheduleDailyTask(Long toggleId, Instant activateAt, Instant deactivateAt, String taskData) {
        Instant nextActivateAt = activateAt.plus(Duration.ofDays(1));
        Instant nextDeactivateAt = deactivateAt.plus(Duration.ofDays(1));

        scheduler.schedule(activateToggleTask.instance("toggle-" + toggleId, taskData), nextActivateAt);
        scheduler.schedule(deactivateToggleTask.instance("toggle-" + toggleId, taskData), nextDeactivateAt);
    }

    private void scheduleWeeklyTask(Long toggleId, Instant activateAt, Instant deactivateAt, String taskData) {
        Instant nextActivateAt = activateAt.plus(Duration.ofDays(7));
        Instant nextDeactivateAt = deactivateAt.plus(Duration.ofDays(7));

        scheduler.schedule(activateToggleTask.instance("toggle-" + toggleId, taskData), nextActivateAt);
        scheduler.schedule(deactivateToggleTask.instance("toggle-" + toggleId, taskData), nextDeactivateAt);
    }

    private void scheduleMonthlyTask( Long toggleId, Instant activateAt, Instant deactivateAt, String taskData) {
        Instant nextActivateAt = activateAt.plus(Duration.ofDays(30));
        Instant nextDeactivateAt = deactivateAt.plus(Duration.ofDays(30));

        scheduler.schedule(activateToggleTask.instance("toggle-" + toggleId, taskData), nextActivateAt);
        scheduler.schedule(deactivateToggleTask.instance("toggle-" + toggleId, taskData), nextDeactivateAt);
    }
}


