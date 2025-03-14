package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.*;

import java.util.List;
import java.util.Optional;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectKey> {

    List<UserProject> findAllByProjectId(Long projectId);

    Optional<UserProject> findByProjectAndUserId(Project project, Long userId);

    void deleteByProjectAndUserId(Project project, Long userId);

    List<UserProject> findByUserId(Long userId);
}
