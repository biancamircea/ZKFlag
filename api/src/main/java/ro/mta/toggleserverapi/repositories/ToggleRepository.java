package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Toggle;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToggleRepository extends JpaRepository<Toggle, Long> {
    List<Toggle> findAllByProjectId(Long project_id);

    Optional<Toggle> findByIdAndProjectId(Long id, Long project_id);
    Optional<Toggle> findByNameAndProject(String name, Project project);

    Optional<Toggle> findByNameAndProjectAndToggleType(String name, Project project, Integer type);

    Optional<Toggle> findByHashId(String hashId);
}
