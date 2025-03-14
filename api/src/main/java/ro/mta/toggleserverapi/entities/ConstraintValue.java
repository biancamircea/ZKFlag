package ro.mta.toggleserverapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Constraint_values")
public class ConstraintValue {
    @Id
    @SequenceGenerator(
            name = "constraint_value_sequence",
            sequenceName = "constraint_value_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "constraint_value_sequence"
    )
    private Long id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "constraint_id")
    @NotNull
    private Constraint constraint;

    @ManyToOne
    @JoinColumn(name = "toggle_environment_id")
    private ToggleEnvironment toggleEnvironment;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ConstraintValue)) {
            return false;
        }

        ConstraintValue other = (ConstraintValue) obj;

        if (this.value == null && other.value == null) {
            return true;
        } else if (this.value == null || other.value == null) {
            return false;
        } else {
            return this.value.equals(other.value);
        }
    }
}
