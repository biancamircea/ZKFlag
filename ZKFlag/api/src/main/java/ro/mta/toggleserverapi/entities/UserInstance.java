package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "User_Instance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInstance {
    @EmbeddedId
    private UserInstanceKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @MapsId("instanceId")
    @JoinColumn(name = "instance_id", nullable = false)
    @JsonBackReference
    private Instance instance;

    private LocalDateTime addedAt = LocalDateTime.now();
}