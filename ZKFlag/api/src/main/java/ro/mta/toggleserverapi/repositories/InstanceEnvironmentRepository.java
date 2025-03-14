package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Instance;
import ro.mta.toggleserverapi.entities.InstanceEnvironment;
import ro.mta.toggleserverapi.entities.InstanceEnvironmentKey;

import java.util.List;
import java.util.Optional;

public interface InstanceEnvironmentRepository extends JpaRepository<InstanceEnvironment, InstanceEnvironmentKey> {

    void deleteAllByEnvironment(Environment environment);
    List<InstanceEnvironment> findAllByInstanceId(Long instance_id);

    List<InstanceEnvironment> findAllByInstanceIdAndActiveTrue(Long instance_id);

    Optional<InstanceEnvironment> findByInstanceIdAndEnvironmentId(Long instance_id, Long environment_id);

    @Query("SELECT ie FROM InstanceEnvironment ie WHERE ie.instance.id = :instanceId AND ie.active = true")
    List<InstanceEnvironment> findActiveInstanceEnvironmentsByInstanceId(@Param("instanceId") Long instanceId);

    @Query("SELECT ie FROM InstanceEnvironment ie WHERE ie.environment.id = :environmentId")
    List<InstanceEnvironment> findAllByEnvironmentId(@Param("environmentId") Long environmentId);

    @Query("SELECT ie FROM InstanceEnvironment ie WHERE ie.instance.id = :instanceId")
    List<InstanceEnvironment> findByInstanceId(@Param("instanceId") Long instanceId);

    List<InstanceEnvironment> findByInstanceIdAndActiveTrue(Long instanceId);
}
