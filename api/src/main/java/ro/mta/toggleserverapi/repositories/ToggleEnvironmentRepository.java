package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.Environment;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleEnvironment;
import ro.mta.toggleserverapi.entities.Instance;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToggleEnvironmentRepository extends JpaRepository<ToggleEnvironment, Long> {
    void deleteAllByEnvironment(Environment environment);


    List<ToggleEnvironment> findAllByToggle(Toggle toggle);

    List<ToggleEnvironment> findAllByToggleId(Long toggleId);

    @Query("SELECT COUNT(te) FROM ToggleEnvironment te WHERE te.environment = :environment AND te.instance = :instance AND te.enabled = true")
    Long countByEnvironmentAndInstanceAndEnabledTrue(@Param("environment") Environment environment, @Param("instance") Instance instance);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.toggle = :toggle AND te.environment = :environment AND te.instance = :instance")
    ToggleEnvironment findByToggleAndEnvironmentAndInstance(@Param("toggle") Toggle toggle,
                                                                      @Param("environment") Environment environment,
                                                                      @Param("instance") Instance instance);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.toggle.id = :toggleId AND te.environment.id = :envId AND te.instance.id = :instanceId")
    Optional<ToggleEnvironment> findByToggleIdAndEnvIdAndInstanceId(@Param("toggleId") Long toggleId,
                                                                    @Param("envId") Long envId,
                                                                    @Param("instanceId") Long instanceId);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.toggle.id = :toggleId AND te.environment.name = :environmentName AND te.instance.id = :instanceId")
    Optional<ToggleEnvironment> findByToggleIdEnvNameAndInstanceId(@Param("toggleId") Long toggleId,
                                                                   @Param("environmentName") String environmentName,
                                                                   @Param("instanceId") Long instanceId);


    @Query("SELECT te FROM ToggleEnvironment te WHERE te.toggle.project = :project AND te.environment.id = :envId AND te.instance.id = :instanceId")
    List<ToggleEnvironment> findByProjectAndEnvironmentAndInstance(@Param("project") Project project,
                                                                   @Param("envId") Long envId,
                                                                   @Param("instanceId") Long instanceId);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.toggle.id = :toggleId AND te.environment.name = :envName AND te.instance.id = :instanceId")
    Optional<ToggleEnvironment> findByToggleIdAndEnvironmentNameAndInstanceId(@Param("toggleId") Long toggleId,
                                                                              @Param("envName") String envName,
                                                                              @Param("instanceId") Long instanceId);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.environment = :environment AND te.instance = :instance AND te.enabled = true")
    List<ToggleEnvironment> findAllByEnvironmentAndInstanceAndEnabledTrue(@Param("environment") Environment environment,
                                                                          @Param("instance") Instance instance);

    @Query("DELETE FROM ToggleEnvironment te WHERE te.toggle.project = :project AND te.environment = :environment AND te.instance.id = :instanceId")
    void deleteAllByToggle_ProjectAndEnvironmentAndInstance(@Param("project") Project project,
                                                            @Param("environment") Environment environment,
                                                            @Param("instanceId") Long instanceId);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.toggle.id = :toggleId AND te.instance.id = :instanceId")
    List<ToggleEnvironment> findAllByToggleIdAndInstanceId(@Param("toggleId") Long toggleId,
                                                           @Param("instanceId") Long instanceId);

    void deleteByEnvironmentId(Long environmentId);

    @Query("SELECT te FROM ToggleEnvironment te WHERE te.instance.id = :instanceId AND te.toggle.id = :toggleId")
    List<ToggleEnvironment> findByInstanceIdAndToggleId(@Param("instanceId") Long instanceId, @Param("toggleId") Long toggleId);

}
