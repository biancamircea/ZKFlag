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
public class ToggleTagKey implements Serializable {
    @Column(name = "toggle_id")
    private Long toggleId;

    @Column(name = "tag_id")
    private Long tagId;

    @Override
    public int hashCode() {
        return Objects.hash(this.toggleId, this.tagId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ToggleTagKey)) {
            return false;
        }
        ToggleTagKey other = (ToggleTagKey) obj;
        return Objects.equals(toggleId, other.toggleId) && Objects.equals(tagId, other.tagId);
    }
}
