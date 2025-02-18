package ro.mta.toggleserverapi.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ro.mta.toggleserverapi.entities.Event;

import java.util.List;

@Data
public class EventsResponseDTO {
    @JsonProperty("events")
    private List<EventDTO> eventList;
}
