package ro.mta.toggleserverapi.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.mta.toggleserverapi.services.EventService;

import java.util.Optional;

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<?> getAllEvents(@RequestParam Optional<String> project,
                                          @RequestParam Optional<String> toggleId){
        return project
                .map(s -> ResponseEntity.ok(eventService.getAllEventsByProjectId(Long.parseLong(project.get()))))
                .orElseGet(() -> toggleId
                                .map(aLong -> ResponseEntity.ok(eventService.getAllEventsByToggleId(Long.parseLong(toggleId.get()))))
                                .orElseGet(() -> ResponseEntity.ok(eventService.getAllEvents())));
    }
}
