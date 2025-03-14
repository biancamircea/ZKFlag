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
public class UserProjectKey implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "project_id")
    private Long projectId;

    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof UserProjectKey)) {
            return false;
        }
        UserProjectKey other = (UserProjectKey) obj;
        return Objects.equals(userId, other.userId) && Objects.equals(projectId, other.projectId);
    }
}
