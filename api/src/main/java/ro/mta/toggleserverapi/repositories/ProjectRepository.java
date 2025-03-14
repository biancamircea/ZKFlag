package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Project;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByHashId(String hashId);
    Optional<Project> findByName(String name);
}
