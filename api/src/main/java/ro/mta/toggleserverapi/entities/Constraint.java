package ro.mta.toggleserverapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.mta.toggleserverapi.enums.OperatorType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Constraints")
public class Constraint {
    @Id
    @SequenceGenerator(
            name = "constraint_sequence",
            sequenceName = "constraint_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "constraint_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "context_field_id")
    private ContextField contextField;

    @ManyToOne
    @JoinColumn(name = "toggle_id")
    private Toggle toggle;

    @Enumerated(EnumType.STRING)
    private OperatorType operator;

    @OneToMany(mappedBy = "constraint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ConstraintValue> values;
}
