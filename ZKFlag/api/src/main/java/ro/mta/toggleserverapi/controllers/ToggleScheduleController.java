package ro.mta.toggleserverapi.controllers;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.mta.toggleserverapi.services.ToggleScheduleService;

import java.time.Instant;

@RestController
@RequestMapping("/toggle-schedule")
@AllArgsConstructor
public class ToggleScheduleController {

    private final ToggleScheduleService toggleScheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleToggle(
            @RequestParam Long projectId,
            @RequestParam Long toggleId,
            @RequestParam Long instanceId,
            @RequestParam String environmentName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant activateAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant deactivateAt,
            @RequestParam(required = false) String recurrence) {

        if(recurrence == null) {
            recurrence = "ONE_TIME";
        }
        try {
            toggleScheduleService.scheduleToggleActivation(
                    projectId, toggleId, instanceId, environmentName,
                    activateAt, deactivateAt, recurrence
            );
            return ResponseEntity.ok("Toggle scheduled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error scheduling toggle: " + e.getMessage());
        }
    }
}


