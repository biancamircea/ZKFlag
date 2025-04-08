package ro.mta.toggleserverapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.mta.toggleserverapi.entities.ToggleSchedule;

import java.util.List;

@Repository
public interface ToggleScheduleRepository extends JpaRepository<ToggleSchedule, Long> {

    @Query("SELECT t FROM ToggleSchedule t " +
            "WHERE t.environment.id = :environmentId " +
            "AND t.instance.id = :instanceId " +
            "AND t.toggle.id = :toggleId " +
            "ORDER BY t.activateAt DESC")
    List<ToggleSchedule> findHistoryByEnvironmentInstanceAndToggle(
            @Param("environmentId") Long environmentId,
            @Param("instanceId") Long instanceId,
            @Param("toggleId") Long toggleId);
}

