package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.ContextField;

import java.util.List;
import java.util.Optional;

public interface ContextFieldRepository extends JpaRepository<ContextField, Long> {
    List<ContextField> findAllByProjectId(Long projectId);
    Optional<ContextField> findByIdAndProjectId(Long id, Long projectId);
    Optional<ContextField> findByNameAndProjectId(String name, Long projectId);
    void deleteByIdAndProjectId(Long id, Long projectId);

    Optional<ContextField> findByHashId(String hashId);
}
