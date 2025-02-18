package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Event;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Toggle;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByProjectIdOrderByCreatedAtDesc(Long id);
    List<Event> findAllByToggleIdOrderByCreatedAtDesc(Long id);
    List<Event> findAllByInstanceIdOrderByCreatedAtDesc(Long id);
}
