package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.ToggleSchedule;

@Repository
public interface ToggleScheduleRepository extends JpaRepository<ToggleSchedule, Long> {
}

