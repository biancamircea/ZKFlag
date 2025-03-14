package ro.mta.toggleserverapi.util;

import jakarta.annotation.Nullable;
import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.enums.OperatorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstraintUtil {
    private static final Map<OperatorType, ConstraintOperator> operators= new HashMap<>();
    static {
        operators.put(OperatorType.IN, new BasicConstraintOperator());
        operators.put(OperatorType.NOT_IN, new BasicConstraintOperator());
    }
    public static boolean validate(@Nullable List<Constraint> constraints,
                                   List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields) {
        if (constraints != null && constraints.size() > 0) {
            return constraints.stream().allMatch(c -> validateConstraint(c, contextFields));
        } else {
            return true;
        }
    }

    private static boolean validateConstraint(Constraint constraint,  List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields) {
        ConstraintOperator operator = operators.get(constraint.getOperator());
        if (operator == null) {
            return false;
        }
        return operator.evaluate(constraint, contextFields);
    }
}
