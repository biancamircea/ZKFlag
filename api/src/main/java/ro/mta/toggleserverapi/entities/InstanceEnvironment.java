package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "instance_environment")
public class InstanceEnvironment {
    @EmbeddedId
    private InstanceEnvironmentKey id;

    @ManyToOne
    @MapsId("instanceId")
    @JoinColumn(name = "instance_id")
    @JsonBackReference(value = "instanceEnvironmentList")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Instance instance;

    @ManyToOne
    @MapsId("environmentId")
    @JoinColumn(name = "environment_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Environment environment;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean active;
}