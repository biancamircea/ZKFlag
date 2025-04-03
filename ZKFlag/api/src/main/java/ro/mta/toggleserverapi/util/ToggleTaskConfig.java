package ro.mta.toggleserverapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.CronSchedule;
import com.github.kagkarlsson.scheduler.task.schedule.Schedule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.config.FixedRateTask;
import ro.mta.toggleserverapi.DTOs.ScheduleDTO;
import ro.mta.toggleserverapi.services.ToggleService;

import java.time.Duration;

@Configuration
public class ToggleTaskConfig {

    private final ObjectMapper objectMapper;

    public ToggleTaskConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public OneTimeTask<String> activateToggleTask(ToggleService toggleService) {
        return Tasks.oneTime("activate-toggle", String.class)
                .execute((instance, executionContext) -> {
                    try {
                    String taskData = instance.getData();
                    System.out.println("Raw task data: " + instance.getData());
                    System.out.println("Data class: " + instance.getData().getClass());

                    if (taskData != null && !taskData.isEmpty()) {
                        try {
                            ScheduleDTO toggleSchedule = objectMapper.readValue(taskData, ScheduleDTO.class);
                            toggleService.enableToggleInEnvironment(
                                    toggleSchedule.getProjectId(),
                                    toggleSchedule.getToggleId(),
                                    toggleSchedule.getEnvironmentName(),
                                    toggleSchedule.getInstanceId()
                            );
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to deserialize task data", e);
                        }
                    }
                    } catch (Exception e) {
                        System.err.println("FULL ERROR DETAILS:");
                        e.printStackTrace();
                        throw e;
                    }
                });

    }


    @Bean
    public OneTimeTask<String> deactivateToggleTask(ToggleService toggleService) {
        return Tasks.oneTime("deactivate-toggle", String.class)
                .execute((instance, executionContext) -> {
                    String taskData = instance.getData();
                    System.out.println("Executing deactivate ToggleTask with data: " + taskData);

                    if (taskData != null && !taskData.isEmpty()) {
                        try {
                            ScheduleDTO toggleSchedule = objectMapper.readValue(taskData, ScheduleDTO.class);
                            System.out.println("Toggle name: " + toggleSchedule.getToggleId() + ", Deactivate at: " + toggleSchedule.getDeactivateAt());

                            if (toggleSchedule.getActivateAt() != null) {
                                toggleService.disableToggleInEnvironment(
                                        toggleSchedule.getProjectId(),
                                        toggleSchedule.getToggleId(),
                                        toggleSchedule.getEnvironmentName(),
                                        toggleSchedule.getInstanceId());
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to deserialize task data", e);
                        }
                    }
                });
    }


    @Bean
    public RecurringTask<String> activateToggleRecurringTask(ToggleService toggleService) {
        return Tasks.recurring("activate-toggle-recurring", String.class)
                .execute((instance, executionContext) -> {
                    try {
                        String taskData = instance.getData();
                        if (taskData != null && !taskData.isEmpty()) {
                            try {
                                ScheduleDTO toggleSchedule = objectMapper.readValue(taskData, ScheduleDTO.class);
                                toggleService.enableToggleInEnvironment(
                                        toggleSchedule.getProjectId(),
                                        toggleSchedule.getToggleId(),
                                        toggleSchedule.getEnvironmentName(),
                                        toggleSchedule.getInstanceId()
                                );
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("Failed to deserialize task data", e);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                });
    }

    // Task pentru dezactivarea toggle-ului (recurring)
    @Bean
    public RecurringTask<String> deactivateToggleRecurringTask(ToggleService toggleService) {
        return Tasks.recurring("activate-toggle-recurring", schedule, String.class)
                .execute((instance, executionContext) -> {
                    String taskData = instance.getData();
                    if (taskData != null && !taskData.isEmpty()) {
                        try {
                            ScheduleDTO toggleSchedule = objectMapper.readValue(taskData, ScheduleDTO.class);
                            toggleService.disableToggleInEnvironment(
                                    toggleSchedule.getProjectId(),
                                    toggleSchedule.getToggleId(),
                                    toggleSchedule.getEnvironmentName(),
                                    toggleSchedule.getInstanceId());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to deserialize task data", e);
                        }
                    }
                });
    }


}



