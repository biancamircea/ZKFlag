package ro.mta.toggleserverapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Toggle_Environment")
public class ToggleEnvironment {
    @Id
    @SequenceGenerator(
            name = "toggle_environment_sequence",
            sequenceName = "toggle_environment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "toggle_environment_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "toggle_id")
    @JsonBackReference(value = "toggleEnvironmentList")
    private Toggle toggle;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    @JsonBackReference
    private Environment environment;

    @ManyToOne
    @JoinColumn(name = "instance_id", nullable = false)
    @JsonBackReference(value = "toggleEnvironmentsList")
    private Instance instance;

    private Boolean enabled = Boolean.FALSE;

    @OneToMany(mappedBy = "toggleEnvironment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ConstraintValue> constraintValues;


    //PAYLOAD
    private String enabledValue;
    private String disabledValue;


    private LocalTime startOn = null;
    private LocalTime startOff = null;
    private LocalDate startDate=null;
    private LocalDate endDate=null;

    //STATISTICS
    private Integer evaluated_true_count=0;
    private Integer evaluated_false_count=0;
}
