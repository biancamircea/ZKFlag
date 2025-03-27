package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.Constraint;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;

import java.util.List;
import java.util.Optional;

public interface ConstraintRepository extends JpaRepository<Constraint, Long> {
    void deleteByIdAndToggleId(Long id, Long toggleId);
    void deleteAllByToggle(Toggle toggle);
    Optional<Constraint> findById(Long id);

    List<Constraint>  findAllByToggleId(Long toggleId);
    List<Constraint> findByContextFieldId(Long contextFieldId);
}
