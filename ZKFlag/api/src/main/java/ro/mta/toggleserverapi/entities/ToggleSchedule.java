package ro.mta.toggleserverapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ro.mta.toggleserverapi.enums.ScheduleType;

import java.time.Instant;

@Entity
@Table(name = "toggle_schedule")
@Getter
@Setter
public class ToggleSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "toggle_id")
    private Toggle toggle;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    private Instance instance;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private Instant activateAt;
    private Instant deactivateAt;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private Integer recurrenceCount;
}

