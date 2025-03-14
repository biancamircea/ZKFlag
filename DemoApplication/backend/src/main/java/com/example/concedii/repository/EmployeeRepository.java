package com.example.concedii.repository;

import com.example.concedii.model.Employee;
import com.example.concedii.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public Employee findByEmployeeId(Long employeeId);
    public Employee findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.supervisor IS NULL")
    List<Employee> findCommanders();

    public List<Employee> findBySupervisor(Employee supervisor);

    public List<Employee> findByRole(Role role);
}
