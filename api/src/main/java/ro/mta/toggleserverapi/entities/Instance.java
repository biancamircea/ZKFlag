package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "instances")
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonBackReference(value = "instanceList")
    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    private Project project;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiToken> apiTokens;

    @JsonManagedReference(value = "instanceEnvironmentList")
    @OneToMany(mappedBy = "instance",cascade = CascadeType.ALL)
    private List<InstanceEnvironment> instanceEnvironmentList;

    @JsonManagedReference(value = "toggleEnvironmentsList")
    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL)
    private List<ToggleEnvironment> toggleEnvironmentsList;
}