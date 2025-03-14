package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ConstraintDTO;
import ro.mta.toggleserverapi.DTOs.ConstraintValueUpdateDTO;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.ConstraintValue;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.exceptions.ConstraintNotFoundException;
import ro.mta.toggleserverapi.repositories.ConstraintRepository;
import ro.mta.toggleserverapi.repositories.ConstraintValueRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ConstraintService {
    private final ConstraintRepository constraintRepository;
    private final ConstraintValueRepository valueRepository;
    private final ContextFieldService contextFieldService;
    private final ToggleEnvironmentService toggleEnvironmentService;

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
    public Constraint fromDTO(ConstraintDTO constraintDTO, Long projectId, Long toggleId,Long instanceId, Long environmentId) {
        Constraint constraint = new Constraint();
        constraint.setContextField(contextFieldService.fetchByProjectIdAndName(constraintDTO.getContextName(), projectId));
        constraint.setOperator(constraintDTO.getOperator());

        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId,instanceId);

        List<ConstraintValue> values = constraintDTO.getValues()
                .stream()
                .map(s -> {
                    ConstraintValue constraintValue = new ConstraintValue();
                    constraintValue.setValue(s);
                    constraintValue.setConstraint(constraint);
                    constraintValue.setToggleEnvironment(toggleEnvironment);
                    return constraintValue;
                })
                .toList();

        constraint.setValues(values);
        return constraint;
    }


    public Constraint fetchConstraintFromToggle(Long constraintId, Long toggleId) {
        return constraintRepository.findById(constraintId)
                .map(constraint -> {
                    List<ConstraintValue> defaultValues = constraint.getValues().stream()
                            .filter(value -> value.getToggleEnvironment() == null)
                            .collect(Collectors.toList());

                    constraint.setValues(defaultValues);
                    return constraint;
                })
                .orElseThrow(() -> new ConstraintNotFoundException(constraintId));
    }



    public Constraint fetchConstraintFromToggleEnv(Long constraintId, Long toggleId,Long instanceId, Long environmentId) {
        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggleId, environmentId,instanceId);
        List<ConstraintValue> constraintValues = valueRepository.findAllByConstraintIdAndToggleEnvironmentId(constraintId, toggleEnvironment.getId());

        if(constraintValues.isEmpty()){
           return constraintRepository.findById(constraintId)
                    .orElseThrow(() -> new ConstraintNotFoundException(constraintId));
        }else{
            Constraint constraint = constraintRepository.findById(constraintId)
                    .orElseThrow(() -> new ConstraintNotFoundException(constraintId));
            constraint.setValues(constraintValues);
            return constraint;
        }
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
        return constraintRepository.findById(constraintId)
                .map(foundConstraint -> {
                    foundConstraint.setOperator(constraint.getOperator());
                    foundConstraint.setContextField(constraint.getContextField());

                    valueRepository.deleteAllByConstraintAndToggleEnvironmentIsNull(foundConstraint);

                    List<ConstraintValue> newValues = constraint.getValues()
                            .stream()
                            .filter(cv -> cv.getToggleEnvironment() == null) // Doar cele fără toggle_environment_id
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


    @Transactional
    public void updateConstraintValuesForToggleEnvironment(
            Long toggleId, Long environmentId, Long instanceId, Long constraintId, ConstraintValueUpdateDTO newValues) {

        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggleId,environmentId,instanceId);
        Constraint constraint = constraintRepository.findById(constraintId)
                .orElseThrow(() -> new ConstraintNotFoundException(constraintId));

        valueRepository.deleteAllByConstraintAndToggleEnvironment(constraint, toggleEnvironment);

        List<ConstraintValue> updatedValues = newValues.getValues().stream()
                .map(value -> {
                    ConstraintValue constraintValue = new ConstraintValue();
                    constraintValue.setValue(value);
                    constraintValue.setConstraint(constraint);
                    constraintValue.setToggleEnvironment(toggleEnvironment);
                    return constraintValue;
                })
                .collect(Collectors.toList());

        valueRepository.saveAll(updatedValues);
    }


    @Transactional
    public void resetConstraintValuesToDefault(Long toggleId, Long environmentId, Long instanceId, Long constraintId) {
        ToggleEnvironment toggleEnvironment = toggleEnvironmentService.fetchByToggleIdAndEnvIdAndInstanceId(toggleId,environmentId,instanceId);

        Constraint constraint = constraintRepository.findById(constraintId)
                .orElseThrow(() -> new ConstraintNotFoundException(constraintId));
        valueRepository.deleteAllByConstraintAndToggleEnvironment(constraint, toggleEnvironment);
    }

    public Constraint fetchConstraint(Long constraintId){
        return constraintRepository.findById(constraintId)
                .orElseThrow(() -> new ConstraintNotFoundException(constraintId));
    }

}
