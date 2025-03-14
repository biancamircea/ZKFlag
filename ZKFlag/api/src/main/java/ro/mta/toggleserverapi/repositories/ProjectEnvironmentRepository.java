package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;
import ro.mta.toggleserverapi.entities.ProjectEnvironmentKey;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectEnvironmentRepository extends JpaRepository<ProjectEnvironment, ProjectEnvironmentKey> {
    void deleteAllByEnvironment(Environment environment);
    List<ProjectEnvironment> findAllByProjectId(Long project_id);

    List<ProjectEnvironment> findAllByProjectIdAndActiveTrue(Long project_id);

    Optional<ProjectEnvironment> findByProjectIdAndEnvironmentId(Long project_id, Long environment_id);
}
