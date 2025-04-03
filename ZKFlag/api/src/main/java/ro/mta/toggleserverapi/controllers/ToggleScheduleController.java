package ro.mta.toggleserverapi.controllers;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ScheduleInfoDTO;
import ro.mta.toggleserverapi.DTOs.ScheduleRequest;
import ro.mta.toggleserverapi.DTOs.StrategiesRequest;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.ToggleRepository;
import ro.mta.toggleserverapi.services.ToggleScheduleService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/toggle-schedule")
@AllArgsConstructor
public class ToggleScheduleController {

    private final ToggleScheduleService toggleScheduleService;
    private final ToggleRepository toggleRepository;
    private final InstanceRepository instanceRepository;
    private final ProjectRepository projectRepository;

    @PostMapping(value = "/schedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> scheduleToggle(
            @RequestBody ScheduleRequest request) {

        Toggle toggle = toggleRepository.findByHashId(request.getToggleId()).orElseThrow();
        Instance instance = instanceRepository.findByHashId(request.getInstanceId()).orElseThrow();
        Project project = projectRepository.findByHashId(request.getProjectId()).orElseThrow();

        String recurrence = request.getRecurrence() != null ? request.getRecurrence() : "ONE_TIME";

        try {
            toggleScheduleService.scheduleToggleActivation(
                    project.getId(),
                    toggle.getId(),
                    instance.getId(),
                    request.getEnvironmentName(),
                    request.getActivateAt(),
                    request.getDeactivateAt(),
                    recurrence
            );
            return ResponseEntity.ok("Toggle scheduled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error scheduling toggle: " + e.getMessage());
        }
    }

    @DeleteMapping("/cancel/{taskInstance}")
    public ResponseEntity<String> cancelScheduledToggle(
            @PathVariable String taskInstance) {

        try {
            System.out.println("Canceling scheduled toggle for task instance ID: " + taskInstance);
            toggleScheduleService.cancelScheduledToggle(taskInstance);
            return ResponseEntity.ok("Scheduled toggle canceled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error canceling scheduled toggle: " + e.getMessage());
        }
    }

    @PostMapping("/strategies")
    public ResponseEntity<?> getToggleStrategies(
            @RequestBody StrategiesRequest request) {

        Toggle toggle = toggleRepository.findByHashId(request.getToggleId()).orElseThrow();
        Instance instance = instanceRepository.findByHashId(request.getInstanceId()).orElseThrow();

        try {
            List<ScheduleInfoDTO> strategies = toggleScheduleService
                    .getSchedulesForToggle(toggle.getId(), instance.getId(), request.getEnvironmentName());

            if (strategies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No scheduling strategies found for the specified criteria");
            }

            return ResponseEntity.ok(strategies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving strategies: " + e.getMessage());
        }
    }
}


