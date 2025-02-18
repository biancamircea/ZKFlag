package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import ro.mta.toggleserverapi.enums.EnvironmentType;

import java.util.List;

@Entity
@Table(name = "Environments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Environment {
    @Id
    @SequenceGenerator(
            name = "environments_sequence",
            sequenceName = "environments_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "environments_sequence"
    )
    private Long id;

    @Column(name = "enabled", nullable = false)
    @ColumnDefault("false")
    private Boolean isEnabled = Boolean.FALSE;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EnvironmentType type;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL)
//    private List<ProjectEnvironment> projectEnvironmentList;

    @JsonManagedReference
    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL)
    private List<InstanceEnvironment> instanceEnvironmentList;

    @JsonManagedReference
    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL)
    private List<ToggleEnvironment> toggleEnvironmentList;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiToken> apiTokens;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    public ToggleEnvironment getToggleEnvironment( Long  toggleId) {
        for (ToggleEnvironment toggleEnvironment : toggleEnvironmentList) {
            if (toggleEnvironment.getToggle().getId().equals(toggleId)) {
                return toggleEnvironment;
            }
        }
        return null;
    }
}
