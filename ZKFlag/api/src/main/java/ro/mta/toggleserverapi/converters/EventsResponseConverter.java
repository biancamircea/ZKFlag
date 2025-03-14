package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.EventsResponseDTO;
import ro.mta.toggleserverapi.entities.Event;

import java.util.List;

@AllArgsConstructor
@Component
public class EventsResponseConverter {
    public static EventsResponseDTO toDTO(List<Event> events){
        EventsResponseDTO eventsResponseDTO = new EventsResponseDTO();
        eventsResponseDTO.setEventList(
                events
                        .stream()
                        .map(EventConverter::toDTO)
                        .toList()
        );
        return eventsResponseDTO;
    }
}
