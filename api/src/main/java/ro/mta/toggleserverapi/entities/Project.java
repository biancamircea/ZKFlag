package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hashids.Hashids;

import java.util.List;

@Entity
@Table(name = "Projects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Project {
    @Id
    @SequenceGenerator(
            name = "projects_sequence",
            sequenceName = "projects_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "projects_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String hashId;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Toggle> toggleList;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Instance> instanceList;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Tag> tagList;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ContextField> contextFields;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserProject> userProjectRole;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

}
