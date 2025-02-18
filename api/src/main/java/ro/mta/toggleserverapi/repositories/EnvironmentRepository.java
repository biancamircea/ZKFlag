package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Environment;

import java.util.List;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    List<Environment> findAllByIsEnabledTrue();
    @Query("SELECT e FROM Environment e WHERE e.isEnabled = true")
    List<Environment> findByEnabledTrue();
}
