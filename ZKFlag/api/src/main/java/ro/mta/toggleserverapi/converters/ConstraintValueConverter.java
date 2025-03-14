package ro.mta.toggleserverapi.converters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ConstraintValueDTO;
import ro.mta.toggleserverapi.entities.ConstraintValue;

@AllArgsConstructor
@Component
public class ConstraintValueConverter {
    public static ConstraintValueDTO toDTO(ConstraintValue constraintValue) {
        ConstraintValueDTO dto = new ConstraintValueDTO();
        dto.setId(constraintValue.getId());
        dto.setValue(constraintValue.getValue());

        if (constraintValue.getToggleEnvironment() != null) {
            dto.setToggle_environment_id(constraintValue.getToggleEnvironment().getId());
        } else {
            dto.setToggle_environment_id(null);
        }

        dto.setConstraint_id(constraintValue.getConstraint().getId());
        return dto;
    }
}
