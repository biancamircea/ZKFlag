package ro.mta.toggleserverapi.controllers;

import lombok.AllArgsConstructor;
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

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final ProjectRepository projectRepository;
    private final InstanceRepository instanceRepository;

    @GetMapping
    public ResponseEntity<?> getAllEvents(@RequestParam Optional<String> project,
                                          @RequestParam Optional<String> toggleId){
        return project
                .map(s -> ResponseEntity.ok(eventService.getAllEventsByProjectId(Long.parseLong(project.get()))))
                .orElseGet(() -> toggleId
                                .map(aLong -> ResponseEntity.ok(eventService.getAllEventsByToggleId(Long.parseLong(toggleId.get()))))
                                .orElseGet(() -> ResponseEntity.ok(eventService.getAllEvents())));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<?> getEvents(@RequestParam(required = false) String projectId,
                                       @RequestParam(required = false) String instanceId) {
        if (projectId != null) {
            Project project = projectRepository.findByHashId(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found with id: " + projectId));
            return ResponseEntity.ok(eventService.getAllEventsByProjectId(project.getId()));
        }

        if (instanceId != null) {
            Instance instance=instanceRepository.findByHashId(instanceId).orElseThrow();
            return ResponseEntity.ok(eventService.getAllEventsByInstanceId(instance.getId()));
        }

        return ResponseEntity.ok(eventService.getAllEvents());
    }

}
