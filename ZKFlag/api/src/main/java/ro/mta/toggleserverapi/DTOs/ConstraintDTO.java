package ro.mta.toggleserverapi.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ro.mta.toggleserverapi.entities.ContextField;
import ro.mta.toggleserverapi.enums.OperatorType;
import ro.mta.toggleserverapi.entities.Constraint;

import java.util.List;

@Data
public class ConstraintDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String contextName;

    @NotNull
    private OperatorType operator;

    private List<String> values;

    private Long isConfidential;
    private Long constrGroupId;

    public static ConstraintDTO toDTO(Constraint constraint) {
        ConstraintDTO constraintDTO = new ConstraintDTO();
        constraintDTO.setId(constraint.getId());
        constraintDTO.setContextName(
                constraint.getContextField() != null ? constraint.getContextField().getName() : null
        );
        constraintDTO.setOperator(constraint.getOperator());
        constraintDTO.setValues(
                constraint.getValues() != null
                        ? constraint.getValues().stream().map(value -> value.getValue()).toList()
                        : null
        );

        constraintDTO.setIsConfidential(constraint.getIsConfidential());
        constraintDTO.setConstrGroupId(constraint.getConstrGroupId());
        return constraintDTO;
    }
}
