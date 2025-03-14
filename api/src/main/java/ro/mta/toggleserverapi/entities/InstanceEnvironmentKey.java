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
public class InstanceEnvironmentKey implements Serializable {
    @Column(name = "instance_id")
    private Long instanceId;

    @Column(name = "environment_id")
    private Long environmentId;

    @Override
    public int hashCode() {
        return Objects.hash(this.instanceId,this.environmentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof InstanceEnvironmentKey)) return false;

        InstanceEnvironmentKey other = (InstanceEnvironmentKey) obj;

        return Objects.equals(instanceId, other.instanceId) && Objects.equals(environmentId, other.environmentId);
    }
}
