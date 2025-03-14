package com.example.concedii.repository;

import com.example.concedii.model.Departament;
import com.example.concedii.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentRepository extends JpaRepository<Departament, Long> {
    public boolean existsBySupervisor(Employee supervisor);
}
