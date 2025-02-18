package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.ConstraintValue;

public interface ConstraintValueRepository extends JpaRepository<ConstraintValue, Long> {
    void deleteAllByConstraint(Constraint constraint);
}
