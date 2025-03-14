package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.ConstraintValue;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;

import java.util.List;
import java.util.Optional;

public interface ConstraintValueRepository extends JpaRepository<ConstraintValue, Long> {
    void deleteAllByConstraint(Constraint constraint);

    List<ConstraintValue> findAllByConstraintIdAndToggleEnvironmentId(Long constraintId, Long toggleEnvironmentId);

    void deleteAllByConstraintAndToggleEnvironmentIsNull(Constraint constraint);
    void deleteAllByConstraintAndToggleEnvironment(Constraint constraint, ToggleEnvironment toggleEnvironment);

    List<ConstraintValue> findByIdAndToggleEnvironmentIsNull(Long constraintId);

    List<ConstraintValue> findAllByConstraintId(Long constraintId);

}
