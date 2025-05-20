package ro.mta.toggleserverapi.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.mta.toggleserverapi.DTOs.ProjectDTO;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.ProjectRepository;
import ro.mta.toggleserverapi.services.EventService;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;

    @GetMapping
    public ResponseEntity<?> getAllEvents(@RequestParam Optional<String> project,
                                          @RequestParam Optional<String> toggleId) {
        log.info("Fetching events with project: {}, toggleId: {}",
                project.orElse("none"), toggleId.orElse("none"));

        try {
            return project
                    .map(s -> {
                        long projectId = Long.parseLong(s);
                        log.debug("Fetching events by projectId: {}", projectId);
                        return ResponseEntity.ok(eventService.getAllEventsByProjectId(projectId));
                    })
                    .orElseGet(() -> toggleId
                            .map(tid -> {
                                long toggleIdValue = Long.parseLong(tid);
                                log.debug("Fetching events by toggleId: {}", toggleIdValue);
                                return ResponseEntity.ok(eventService.getAllEventsByToggleId(toggleIdValue));
                            })
                            .orElseGet(() -> {
                                log.debug("Fetching all events with no filters");
                                return ResponseEntity.ok(eventService.getAllEvents());
                            }));
        } catch (Exception e) {
            log.error("Error fetching events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/all")
    public ResponseEntity<?> getEvents(@RequestParam(required = false) String projectId,
                                       @RequestParam(required = false) String instanceId) {
        log.info("Fetching events with projectId: {}, instanceId: {}", projectId, instanceId);
        try {
            if (projectId != null) {
                Project project = projectRepository.findByHashId(projectId)
                        .orElseThrow(() -> new NoSuchElementException("Project not found with id: " + projectId));
                log.debug("Found project {}. Fetching events...", projectId);
                return ResponseEntity.ok(eventService.getAllEventsByProjectId(project.getId()));
            }

            if (instanceId != null) {
                Instance instance = instanceRepository.findByHashId(instanceId)
                        .orElseThrow(() -> new NoSuchElementException("Instance not found with id: " + instanceId));
                log.debug("Found instance {}. Fetching events...", instanceId);
                return ResponseEntity.ok(eventService.getAllEventsByInstanceId(instance.getId()));
            }

            log.debug("Fetching all events with no filters");
            return ResponseEntity.ok(eventService.getAllEvents());

        } catch (NoSuchElementException e) {
            log.warn("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}

