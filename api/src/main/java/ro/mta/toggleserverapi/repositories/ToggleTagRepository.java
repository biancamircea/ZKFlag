package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleTag;
import ro.mta.toggleserverapi.entities.ToggleTagKey;

import java.util.List;

@Repository
public interface ToggleTagRepository extends JpaRepository<ToggleTag, ToggleTagKey> {
    void deleteByToggleAndTag(Toggle toggle, Tag tag);

    List<ToggleTag> findAllByToggle(Toggle toggle);
}
