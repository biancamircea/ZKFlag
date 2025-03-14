package com.example.concedii.repository;

import com.example.concedii.model.Employee;
import com.example.concedii.model.SubmittedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmittedRequestRepository extends JpaRepository<SubmittedRequest, Long> {
    public List<SubmittedRequest> findByEmployee(Employee employee);
    public void deleteByEmployee(Employee employee);

    @Query("SELECT sr FROM SubmittedRequest sr WHERE sr.employee.supervisor = :employee")
    List<SubmittedRequest> findByEmployeeSupervisor(@Param("employee") Employee employee);

    @Query("SELECT sr FROM SubmittedRequest sr WHERE sr.employee.supervisor IS NULL")
    public List<SubmittedRequest> findAllBySupervisorIsNull();

    @Query("SELECT sr FROM SubmittedRequest sr WHERE sr.employee.supervisor.employeeId = :supervisorId")
    List<SubmittedRequest> findAllBySupervisorId(@Param("supervisorId") Long supervisorId);

}
