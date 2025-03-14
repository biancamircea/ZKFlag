package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ConstraintDTO;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.ConstraintValue;
import ro.mta.toggleserverapi.services.ContextFieldService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ConstraintConverter {

    public static ConstraintDTO toDTO(Constraint constraint){
        ConstraintDTO constraintDTO = new ConstraintDTO();
        constraintDTO.setId(constraint.getId());
        constraintDTO.setContextName(constraint.getContextField().getName());
        constraintDTO.setOperator(constraint.getOperator());
        constraintDTO.setValues(
                constraint.getValues()
                        .stream()
                        .map(ConstraintValue::getValue)
                        .collect(Collectors.toList())
        );
        return constraintDTO;
    }
}
