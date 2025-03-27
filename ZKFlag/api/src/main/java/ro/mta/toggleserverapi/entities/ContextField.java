package ro.mta.toggleserverapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Context_fields")
public class ContextField {

    @Id
    @SequenceGenerator(
            name = "context_field_sequence",
            sequenceName = "context_field_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "context_field_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String hashId;

    @NotNull
    @NotEmpty
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private Long isConfidential;

    @OneToMany(mappedBy = "contextField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Constraint> constraints;
}
