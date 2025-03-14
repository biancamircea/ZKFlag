package ro.mta.toggleserverapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserInstanceKey {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "instance_id")
    private Long instanceId;

    @Override
    public int hashCode() {
        return Objects.hash(userId, instanceId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof UserInstanceKey)) {
            return false;
        }
        UserInstanceKey other = (UserInstanceKey) obj;
        return Objects.equals(userId, other.userId) && Objects.equals(instanceId, other.instanceId);
    }
}
