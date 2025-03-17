package ro.mta.toggleserverapi.util;

import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.ConstraintValue;

import java.util.List;
import java.util.Optional;

public class BasicConstraintOperator implements ConstraintOperator{
    @Override
    public boolean evaluate(Constraint constraint, List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields) {
        List<String> values = constraint.getValues()
                .stream()
                .map(ConstraintValue::getValue)
                .toList();

        Optional<String> contextValue = ClientToggleEvaluationRequestDTO.getValueByName(contextFields ,constraint.getContextField().getName());
        return switch (constraint.getOperator()) {
            case IN -> isIn(values, contextValue);
            case NOT_IN -> !isIn(values, contextValue);
            case GREATER_THAN -> isGreaterThan(values, contextValue);
            case LESS_THAN -> isLessThan(values, contextValue);
            default -> false;
        };
    }

    private boolean isIn(List<String> values, Optional<String> value) {
        return value.map(v ->
                        values.stream()
                                .anyMatch(v2 -> v2.equals(v)))
                .orElse(false);
    }

    private boolean isGreaterThan(List<String> values, Optional<String> value) {
        return value.filter(v -> !Double.isNaN(tryParseDouble(v)))
                .map(v -> values.stream()
                        .map(this::tryParseDouble)
                        .filter(d -> !Double.isNaN(d))
                        .anyMatch(d -> d < tryParseDouble(v)))
                .orElse(false);
    }

    private boolean isLessThan(List<String> values, Optional<String> value) {
        return value.filter(v -> !Double.isNaN(tryParseDouble(v)))
                .map(v -> values.stream()
                        .map(this::tryParseDouble)
                        .filter(d -> !Double.isNaN(d))
                        .anyMatch(d -> d > tryParseDouble(v)))
                .orElse(false);
    }

    private double tryParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }


}
