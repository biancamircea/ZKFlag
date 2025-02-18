package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.EventsResponseDTO;
import ro.mta.toggleserverapi.converters.EventsResponseConverter;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Event;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.enums.ActionType;
import ro.mta.toggleserverapi.repositories.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;


    private List<Event> fetchAllEvents(){
        return eventRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private List<Event> fetchAllEventsByProjectId(Long projectId){
        return eventRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId);
    }

    private List<Event> fetchAllEventsByInstanceId(Long instanceId){
        return eventRepository.findAllByInstanceIdOrderByCreatedAtDesc(instanceId);
    }

    private List<Event> fetchAllEventsByToggleId(Long toggleId){
        return eventRepository.findAllByToggleIdOrderByCreatedAtDesc(toggleId);
    }

    public EventsResponseDTO getAllEvents(){
        return EventsResponseConverter.toDTO(fetchAllEvents());
    }

    public EventsResponseDTO getAllEventsByProjectId(Long projectId){
        return EventsResponseConverter.toDTO(fetchAllEventsByProjectId(projectId));
    }

    public EventsResponseDTO getAllEventsByInstanceId(Long instanceId){
        return EventsResponseConverter.toDTO(fetchAllEventsByInstanceId(instanceId));
    }

    public EventsResponseDTO getAllEventsByToggleId(Long toggleId){
        return EventsResponseConverter.toDTO(fetchAllEventsByToggleId(toggleId));
    }

    private Event initialiseEvent(ActionType actionType){
        Event event = new Event();
        event.setAction(actionType);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }
    public void submitAction(ActionType actionType,
                             Environment environment){
        Event event = initialiseEvent(actionType);
        event.setEnvironment(environment);
        eventRepository.save(event);
    }

    public void submitAction(ActionType actionType,
                             Project project){
        Event event = initialiseEvent(actionType);
        event.setProject(project);
        eventRepository.save(event);
    }
    public void submitAction(ActionType actionType,
                             Project project,
                             Environment environment){
        Event event = initialiseEvent(actionType);
        event.setProject(project);
        event.setEnvironment(environment);
        eventRepository.save(event);
    }

    public void submitAction(ActionType actionType,
                             Project project,
                             Toggle toggle){
        Event event = initialiseEvent(actionType);
        event.setProject(project);
        event.setToggle(toggle);
        eventRepository.save(event);
    }

    public void submitAction(ActionType actionType,
                             Project project,
                             Toggle toggle,
                             Environment environment){
        Event event = initialiseEvent(actionType);
        event.setProject(project);
        event.setToggle(toggle);
        event.setEnvironment(environment);
        eventRepository.save(event);
    }


}
