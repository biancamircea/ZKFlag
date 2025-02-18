package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ConstraintDTO;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.ConstraintValue;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.exceptions.ConstraintNotFoundException;
import ro.mta.toggleserverapi.repositories.ConstraintRepository;
import ro.mta.toggleserverapi.repositories.ConstraintValueRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ConstraintService {
    private final ConstraintRepository constraintRepository;
    private final ConstraintValueRepository valueRepository;
    private final ContextFieldService contextFieldService;

    public Constraint fromDTO(ConstraintDTO constraintDTO, Long projectId){
        Constraint constraint = new Constraint();

        constraint.setContextField(contextFieldService.fetchByProjectIdAndName(constraintDTO.getContextName(), projectId));
        constraint.setOperator(constraintDTO.getOperator());
        List<ConstraintValue> values = constraintDTO.getValues()
                .stream()
                .map(s -> {
                    ConstraintValue constraintValue = new ConstraintValue();
                    constraintValue.setValue(s);
                    constraintValue.setConstraint(constraint);
                    return constraintValue;
                })
                .toList();

        constraint.setValues(values);
        return constraint;
    }

//    public Constraint fetchConstraintFromToggleEnv(Long constraintId, Long toggleId, Long environmentId) {
//        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvId(toggleId, environmentId);
//        return constraintRepository.findByIdAndToggleEnvironment(constraintId, toggleEnvironment)
//                .orElseThrow(() -> new ConstraintNotFoundException(constraintId, toggleEnvironment.getId()));
//    }

//    public Constraint createConstraintInToggleEnv(Constraint constraint, Long toggleId, Long environmentId) {
//        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvId(toggleId, environmentId);
//        constraint.setToggleEnvironment(toggleEnvironment);
//        return constraintRepository.save(constraint);
//    }


    //posibil pb aici
    public Constraint fetchConstraintFromToggle(Long constraintId, Long toggleId) {
        //Toggle toggle = toggleService.fetchToggleById(toggleId);
        return constraintRepository.findById(constraintId)
                .orElseThrow(() -> new ConstraintNotFoundException(constraintId));
    }

    public Constraint createConstraintInToggle(Constraint constraint, Toggle toggle){
        //Toggle toggle = toggleService.fetchToggleById(toggleId);
        constraint.setToggle(toggle);
        return constraintRepository.save(constraint);
    }

    @Transactional
    public void deleteConstraintFromToggle(Long constraintId, Long toggleId) {
        //Toggle toggle = toggleService.fetchToggleById(toggleId);
        constraintRepository.deleteByIdAndToggleId(constraintId, toggleId);
    }

    @Transactional
    public void deleteAllConstraintsFromToggle(Toggle toggle) {
        //Toggle toggle = toggleService.fetchToggleById(toggleId);
        constraintRepository.deleteAllByToggle(toggle);
    }

    @Transactional
    public Constraint updateConstraintInToggle(Constraint constraint, Toggle toggle, Long constraintId) {
       // Toggle toggle = toggleService.fetchToggleById(toggleId);
        return constraintRepository.findById(constraintId)
                .map(foundConstraint -> {
                    foundConstraint.setOperator(constraint.getOperator());
                    foundConstraint.setContextField(constraint.getContextField());

//                    will remove old list
                    valueRepository.deleteAllByConstraint(foundConstraint);

                    List<ConstraintValue> newValues = constraint.getValues()
                            .stream()
                            .map(constraintValue -> {
                                constraintValue.setConstraint(foundConstraint);
                                return constraintValue;
                            })
                            .collect(Collectors.toList());
                    foundConstraint.setValues(newValues);

                    return constraintRepository.save(foundConstraint);
                })
                .orElseGet(() -> createConstraintInToggle(constraint, toggle));
    }
}
