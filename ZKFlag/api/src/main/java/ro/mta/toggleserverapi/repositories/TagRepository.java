package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByProjectId(Long project_id);
    Optional<Tag> findByIdAndProjectId(Long id, Long project_id);

    void deleteByIdAndProjectId(Long id, Long project_id);

    Optional<Tag> findByHashId(String hashId);
}
