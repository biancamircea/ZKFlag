package ro.mta.toggleserverapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.mta.toggleserverapi.enums.ActionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "Events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @SequenceGenerator(
            name = "events_sequence",
            sequenceName = "events_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "events_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "toggle_id")
    private Toggle toggle;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    private Instance instance;

    private ActionType action;

    private LocalDateTime createdAt;
}
