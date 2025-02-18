package ro.mta.toggleserverapi.converters;

import ro.mta.toggleserverapi.DTOs.EventDTO;
import ro.mta.toggleserverapi.entities.Event;

public class EventConverter {
    public static EventDTO toDTO(Event event){
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setCreatedAt(event.getCreatedAt());
        eventDTO.setAction(event.getAction());
        if(event.getToggle() != null){
            eventDTO.setToggle(event.getToggle().getName());
        }
        if(event.getEnvironment() != null){
            eventDTO.setEnvironment(event.getEnvironment().getName());
        }
        if(event.getProject() != null){
            eventDTO.setProject(event.getProject().getName());
        }

        return eventDTO;
    }
}
