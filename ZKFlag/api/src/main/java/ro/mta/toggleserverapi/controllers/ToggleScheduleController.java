package ro.mta.toggleserverapi.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.ScheduleInfoDTO;
import ro.mta.toggleserverapi.DTOs.ScheduleRequest;
import ro.mta.toggleserverapi.DTOs.StrategiesRequest;
import ro.mta.toggleserverapi.DTOs.ToggleScheduleHistoryDto;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.repositories.EnvironmentRepository;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.repositories.ToggleRepository;
import ro.mta.toggleserverapi.services.ToggleScheduleService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/toggles/toggle-schedule")
@AllArgsConstructor
@Slf4j
public class ToggleScheduleController {

    private final ToggleScheduleService toggleScheduleService;
    private final ToggleRepository toggleRepository;
    private final InstanceRepository instanceRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;

    @PostMapping(value = "/schedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> scheduleToggle(@RequestBody ScheduleRequest request) {
        log.info("Scheduling toggle: {}", request);
        try {
            Toggle toggle = toggleRepository.findByHashId(request.getToggleId())
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Instance instance = instanceRepository.findByHashId(request.getInstanceId())
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            Project project = projectRepository.findByHashId(request.getProjectId())
                    .orElseThrow(() -> new NoSuchElementException("Project not found"));

            String recurrence = request.getRecurrence() != null ? request.getRecurrence() : "ONE_TIME";

            toggleScheduleService.scheduleToggleActivation(
                    project.getId(),
                    toggle.getId(),
                    instance.getId(),
                    request.getEnvironmentName(),
                    request.getActivateAt(),
                    request.getDeactivateAt(),
                    recurrence
            );
            log.info("Toggle scheduled successfully");
            return ResponseEntity.ok("Toggle scheduled successfully");
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while scheduling toggle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error scheduling toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error scheduling toggle: " + e.getMessage());
        }
    }

    @DeleteMapping("/cancel/{taskInstance}")
    public ResponseEntity<String> cancelScheduledToggle(@PathVariable String taskInstance) {
        log.info("Canceling scheduled toggle for task instance ID: {}", taskInstance);
        try {
            toggleScheduleService.cancelScheduledToggle(taskInstance);
            log.info("Scheduled toggle canceled successfully");
            return ResponseEntity.ok("Scheduled toggle canceled successfully");
        } catch (Exception e) {
            log.error("Error canceling scheduled toggle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error canceling scheduled toggle: " + e.getMessage());
        }
    }

    @PostMapping("/strategies")
    public ResponseEntity<?> getToggleStrategies(@RequestBody StrategiesRequest request) {
        log.info("Fetching toggle strategies for request: {}", request);
        try {
            Toggle toggle = toggleRepository.findByHashId(request.getToggleId())
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Instance instance = instanceRepository.findByHashId(request.getInstanceId())
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));

            List<ScheduleInfoDTO> strategies = toggleScheduleService
                    .getSchedulesForToggle(toggle.getId(), instance.getId(), request.getEnvironmentName());

            if (strategies.isEmpty()) {
                log.info("No scheduling strategies found for toggle {} in instance {}", request.getToggleId(), request.getInstanceId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No scheduling strategies found for the specified criteria");
            }

            return ResponseEntity.ok(strategies);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while fetching strategies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving strategies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving strategies: " + e.getMessage());
        }
    }

    @PostMapping("/history")
    public ResponseEntity<List<ToggleScheduleHistoryDto>> getToggleScheduleHistory(@RequestBody StrategiesRequest request) {
        log.info("Fetching toggle schedule history for request: {}", request);
        try {
            Toggle toggle = toggleRepository.findByHashId(request.getToggleId())
                    .orElseThrow(() -> new NoSuchElementException("Toggle not found"));
            Instance instance = instanceRepository.findByHashId(request.getInstanceId())
                    .orElseThrow(() -> new NoSuchElementException("Instance not found"));
            Environment environment = environmentRepository.findByName(request.getEnvironmentName());

            List<ToggleScheduleHistoryDto> history = toggleScheduleService
                    .getToggleScheduleHistory(environment.getId(), instance.getId(), toggle.getId());

            return ResponseEntity.ok(history);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found while fetching history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error retrieving toggle schedule history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
