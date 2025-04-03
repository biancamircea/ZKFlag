package ro.mta.toggleserverapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.mta.toggleserverapi.DTOs.ScheduleDTO;
import ro.mta.toggleserverapi.services.ToggleService;

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

                        if (taskData == null || taskData.isEmpty()) {
                            return;
                        }

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

                if (taskData == null || taskData.isEmpty()) {
                    return;
                }

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
            });
        }


@Bean
public RecurringTask<String> activateToggleRecurringDaily(ToggleService toggleService) {
    return Tasks.recurring("activate-toggle-daily", FixedDelay.ofMinutes(7), String.class)
            .doNotScheduleOnStartup()
            .execute((instance, executionContext) -> {
                try {
                    String taskData = instance.getData();
                    System.out.println("Raw task data: " + instance.getData());
                    System.out.println("Data class: " + instance.getData().getClass());

                    if (taskData == null || taskData.isEmpty()) {
                        return;
                    }

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
            } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
});
        }

@Bean
public RecurringTask<String> deactivateToggleRecurringDaily(ToggleService toggleService) {
    return Tasks.recurring("deactivate-toggle-daily", FixedDelay.ofMinutes(7), String.class)
            .doNotScheduleOnStartup()
            .execute((instance, executionContext) -> {
                String taskData = instance.getData();
                System.out.println("Raw task data: " + instance.getData());
                System.out.println("Data class: " + instance.getData().getClass());

                if (taskData == null || taskData.isEmpty()) {
                    return;
                }

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

            });
    }

    @Bean
    public RecurringTask<String> activateToggleRecurringWeekly(ToggleService toggleService) {
        return Tasks.recurring("activate-toggle-weekly", FixedDelay.ofHours(7*24), String.class)
                .doNotScheduleOnStartup()
                .execute((instance, executionContext) -> {
                    try {
                        String taskData = instance.getData();
                        System.out.println("Raw task data: " + instance.getData());
                        System.out.println("Data class: " + instance.getData().getClass());

                        if (taskData == null || taskData.isEmpty()) {
                            return;
                        }

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
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                });
    }

    @Bean
    public RecurringTask<String> deactivateToggleRecurringWeekly(ToggleService toggleService) {
        return Tasks.recurring("deactivate-toggle-weekly", FixedDelay.ofHours(7*24), String.class)
                .doNotScheduleOnStartup()
                .execute((instance, executionContext) -> {
                    String taskData = instance.getData();
                    System.out.println("Raw task data: " + instance.getData());
                    System.out.println("Data class: " + instance.getData().getClass());

                    if (taskData == null || taskData.isEmpty()) {
                        return;
                    }

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
                });
    }

    @Bean
    public RecurringTask<String> activateToggleRecurringMonthly(ToggleService toggleService) {
        return Tasks.recurring("activate-toggle-monthly", FixedDelay.ofHours(30*7*24), String.class)
                .doNotScheduleOnStartup()
                .execute((instance, executionContext) -> {
                    try {
                        String taskData = instance.getData();
                        System.out.println("Raw task data: " + instance.getData());
                        System.out.println("Data class: " + instance.getData().getClass());

                        if (taskData == null || taskData.isEmpty()) {
                            return;
                        }

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
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                });
    }

    @Bean
    public RecurringTask<String> deactivateToggleRecurringMonthly(ToggleService toggleService) {
        return Tasks.recurring("deactivate-toggle-monthly", FixedDelay.ofHours(30*24), String.class)
                .doNotScheduleOnStartup()
                .execute((instance, executionContext) -> {
                    String taskData = instance.getData();
                    System.out.println("Raw task data: " + instance.getData());
                    System.out.println("Data class: " + instance.getData().getClass());

                    if (taskData == null || taskData.isEmpty()) {
                        return;
                    }

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
                });
    }

    }
