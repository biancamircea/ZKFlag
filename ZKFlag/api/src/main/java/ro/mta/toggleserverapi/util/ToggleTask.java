package ro.mta.toggleserverapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
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

    private final RecurringTask<String> activateToggleRecurringDaily;
    private final RecurringTask<String> activateToggleRecurringWeekly;
    private final RecurringTask<String> activateToggleRecurringMonthly;

    private final RecurringTask<String> deactivateToggleRecurringDaily;
    private final RecurringTask<String> deactivateToggleRecurringWeekly;
    private final RecurringTask<String> deactivateToggleRecurringMonthly;


    public void scheduleToggleActivation(Long projectId, Long toggleId, String environmentName, Long instanceId, Instant activateAt, Instant deactivateAt, String recurrence) {
        try {
            ScheduleDTO toggleScheduleDTO = new ScheduleDTO();

            if(activateAt!=null) {
                toggleScheduleDTO.setActivateAt(activateAt);
            }
            if(deactivateAt!=null) {
                toggleScheduleDTO.setDeactivateAt(deactivateAt);
            }
            toggleScheduleDTO.setToggleId(toggleId);
            toggleScheduleDTO.setInstanceId(instanceId);
            toggleScheduleDTO.setEnvironmentName(environmentName);
            toggleScheduleDTO.setProjectId(projectId);

            //System.out.println("toggle schedule DTO: " + toggleId + " " + toggleScheduleDTO.getActivateAt() + " " + toggleScheduleDTO.getDeactivateAt());

            String taskData = objectMapper.writeValueAsString(toggleScheduleDTO);
            System.out.println("task data: " + taskData);

            String taskInstance;
            if(activateAt!=null) {
                taskInstance = "toggle-" + toggleId + "-instance-"+instanceId+"-env-"+environmentName+"-"+ activateAt.toEpochMilli()+"-"+recurrence;
            }else if(deactivateAt!=null) {
                taskInstance = "toggle-" + toggleId + "-instance-"+instanceId+"-env-"+environmentName+"-"+ deactivateAt.toEpochMilli()+"-"+recurrence;
            }else{
                System.out.println("Both activateAt and deactivateAt are null");
                return;
            }

            if (recurrence.equals("ONE_TIME")) {
                if (activateAt != null) {
                    scheduler.schedule(activateToggleTask.instance(taskInstance, taskData), activateAt);
                }
                if (deactivateAt != null) {
                    scheduler.schedule(deactivateToggleTask.instance(taskInstance, taskData), deactivateAt);
                }
            } else if(recurrence.equals("DAILY")) {
                    scheduler.schedule(activateToggleRecurringDaily.instance(taskInstance, taskData), activateAt);
                if (deactivateAt != null) {
                    scheduler.schedule(deactivateToggleRecurringDaily.instance(taskInstance, taskData), deactivateAt);
                }
           }else if(recurrence.equals("WEEKLY")) {
                    scheduler.schedule(activateToggleRecurringWeekly.instance(taskInstance, taskData), activateAt);
                if (deactivateAt != null) {
                    scheduler.schedule(deactivateToggleRecurringWeekly.instance(taskInstance, taskData), deactivateAt);
                }
           }else if(recurrence.equals("MONTHLY")) {
                     scheduler.schedule(activateToggleRecurringMonthly.instance(taskInstance, taskData), activateAt);
                if (deactivateAt != null) {
                     scheduler.schedule(deactivateToggleRecurringMonthly.instance(taskInstance, taskData), deactivateAt);
                }
           }else{
               throw new IllegalArgumentException("Unknown recurrence type: " + recurrence);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ToggleScheduleDTO", e);
        }
    }
}