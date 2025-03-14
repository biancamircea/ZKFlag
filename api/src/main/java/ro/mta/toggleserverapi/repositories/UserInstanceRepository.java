package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mta.toggleserverapi.entities.UserInstance;
import ro.mta.toggleserverapi.entities.Instance;

import java.util.List;
import java.util.Optional;

public interface UserInstanceRepository extends JpaRepository<UserInstance, Long> {
    List<UserInstance> findAllByInstanceId(Long instanceId);
    Optional<UserInstance> findByInstanceAndUserId(Instance instance, Long userId);
    void deleteByInstanceAndUserId(Instance instance, Long userId);
    List<UserInstance> findByUserId(Long userId);
}
