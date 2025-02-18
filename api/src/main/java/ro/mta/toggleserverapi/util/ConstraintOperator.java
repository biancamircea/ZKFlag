package ro.mta.toggleserverapi.util;

import ro.mta.toggleserverapi.DTOs.ClientToggleEvaluationRequestDTO;
import ro.mta.toggleserverapi.entities.Constraint;

import java.util.List;

public interface ConstraintOperator {
    boolean evaluate(Constraint constraint, List<ClientToggleEvaluationRequestDTO.ContextFromClientDTO> contextFields);
}
