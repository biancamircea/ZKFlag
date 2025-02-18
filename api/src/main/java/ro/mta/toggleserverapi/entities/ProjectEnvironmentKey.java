package ro.mta.toggleserverapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjectEnvironmentKey implements Serializable {
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "environment_id")
    private Long environmentId;

    @Override
    public int hashCode() {
        return Objects.hash(this.projectId,this.environmentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ProjectEnvironmentKey)) return false;

        ProjectEnvironmentKey other = (ProjectEnvironmentKey) obj;

        return Objects.equals(projectId, other.projectId) && Objects.equals(environmentId, other.environmentId);
    }
}
