package com.example.concedii.service;

import com.example.concedii.model.Departament;
import com.example.concedii.model.Employee;
import com.example.concedii.repository.DepartamentRepository;
import com.example.concedii.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartamentService {
    private final DepartamentRepository departamentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    @Autowired
    public DepartamentService(DepartamentRepository departamentRepository,
                              EmployeeRepository employeeRepository,
                              EmployeeService employeeService) {
        this.departamentRepository = departamentRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    public List<Departament> getAllDepartaments() {
        return departamentRepository.findAll();
    }

    public Departament getDepartamentById(Long id) {
        return departamentRepository.findById(id).orElseThrow(() -> new RuntimeException("Departament not found"));
    }

    public Departament createDepartament(Departament departament) {
        return departamentRepository.save(departament);
    }

    public Departament updateDepartament(Departament departament, Long id) {
        Departament departament1 = departamentRepository.findById(id).orElseThrow(() -> new RuntimeException("Departament not found"));
        departament1.setSupervisor(departament.getSupervisor());
        return departamentRepository.save(departament);
    }

    public Employee getSupervisorById(Long id) {
        return departamentRepository.findById(id).get().getSupervisor();
    }

    public void deleteDepartamentById(Long id) {
        Departament departament = departamentRepository.findById(id).get();
        if (departament != null) {
            departamentRepository.delete(departament);
        }
    }

    public void updateDepartmentHead(Long departmentId, Long newHeadId) {
        Departament department = departamentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found."));

        Employee currentHead = employeeRepository.findById(department.getSupervisor().getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Current head not found."));

        Employee newHead = employeeRepository.findById(newHeadId)
                .orElseThrow(() -> new IllegalArgumentException("New head not found."));

        Employee commander = employeeService.getCommanders().get(0);

        List<Employee> departmentEmployees = employeeService.getEmployeesBySupervisor(currentHead.getEmployeeId());

        departmentEmployees.forEach(emp -> emp.setSupervisor(newHead));
        employeeRepository.saveAll(departmentEmployees);

        currentHead.setSupervisor(newHead);
        newHead.setSupervisor(commander);
        employeeRepository.save(currentHead);
        employeeRepository.save(newHead);

        department.setSupervisor(newHead);
        departamentRepository.save(department);
    }

    public Departament getDepartamentBySupervisor(Employee supervisor) {
        List<Departament> departaments = departamentRepository.findAll();

        for (Departament department : departaments) {
            if (department.getSupervisor().equals(supervisor)) {
                return department;
            }
        }

        return null;
    }

    public boolean isDepartmentHead(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).get();
        return departamentRepository.existsBySupervisor(employee);
    }


    public List<Employee> getDepartmensHeads() {
        List<Departament> departments = getAllDepartaments();
        List<Employee> heads = new ArrayList<>();

        for (Departament department : departments) {
            Long supervisorId = department.getSupervisor().getEmployeeId();
            if (supervisorId != null) {
                Employee head = employeeService.getEmployeeById(supervisorId);
                if (head != null) {
                    heads.add(head);
                }
            }
        }
        return heads;
    }
}
