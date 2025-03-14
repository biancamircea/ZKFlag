package ro.mta.toggleserverapi.entities;

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
@Table(name = "Toggle_Tag")
public class ToggleTag {
    @EmbeddedId
    private ToggleTagKey id;

    @ManyToOne
    @MapsId("toggleId")
    @JoinColumn(name = "toggle_id")
    private Toggle toggle;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
