package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.Instance;

import java.util.List;
import java.util.Optional;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    List<Instance> findAllByProjectId(Long projectId);
    Optional<Instance> findByHashId(String hashId);
}
