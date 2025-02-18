package ro.mta.toggleserverapi.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Toggles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Toggle {
    @Id
    @SequenceGenerator(
            name = "toggles_sequence",
            sequenceName = "toggles_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "toggles_sequence"
    )
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonBackReference(value = "toggleList")
    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    private Project project;

    @OneToMany(mappedBy = "toggle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "toggleEnvironmentList")
    private List<ToggleEnvironment> toggleEnvironmentList;

    @OneToMany(mappedBy = "toggle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ToggleTag> toggleTags;

    @OneToMany(mappedBy = "toggle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    @OneToMany(mappedBy = "toggle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Constraint> constraints;
}
