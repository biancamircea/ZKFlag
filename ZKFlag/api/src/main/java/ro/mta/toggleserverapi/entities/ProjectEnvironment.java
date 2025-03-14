package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Project_Environment")
public class ProjectEnvironment {
    @EmbeddedId
    private ProjectEnvironmentKey id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    @JsonBackReference(value = "projectEnvironmentList")
    private Project project;

    @ManyToOne
    @MapsId("environmentId")
    @JoinColumn(name = "environment_id")
    @JsonBackReference
    private Environment environment;

    private Boolean active;
}
