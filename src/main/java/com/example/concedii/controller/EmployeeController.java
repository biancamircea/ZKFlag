package com.example.concedii.controller;

import com.example.concedii.model.Departament;
import com.example.concedii.model.Employee;
import com.example.concedii.model.Role;
import com.example.concedii.repository.DepartamentRepository;
import com.example.concedii.repository.EmployeeRepository;
import com.example.concedii.service.DepartamentService;
import com.example.concedii.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final DepartamentService departamentService;
    private final DepartamentRepository departamentRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              DepartamentService departamentService,
                              DepartamentRepository departamentRepository, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.departamentService = departamentService;
        this.departamentRepository = departamentRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }


    @GetMapping("/commanders")
    public List<Employee> getCommanders() {
        return employeeService.getCommanders();
    }

    @PostMapping("/")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        this.employeeService.deleteEmployee(id);
    }

    @GetMapping("/email/{email}")
    public Employee getEmployeeByEmail(@PathVariable String email) {
        return this.employeeService.getEmployeeByEmail(email);
    }

    @PutMapping("/{id}/change-department")
    public ResponseEntity<Employee> changeDepartment(
            @PathVariable Long id,
            @RequestBody Long newSupervisorId) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null || !employee.getRole().equals(Role.ROLE_EMPLOYEE)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Employee newSupervisor = employeeService.getEmployeeById(newSupervisorId);
        if (newSupervisor == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        employee.setSupervisor(newSupervisor);
        employeeRepository.save(employee);

        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping("/by-supervisor/{supervisorId}")
    public ResponseEntity<List<Employee>> getEmployeesBySupervisor(@PathVariable Long supervisorId) {
        List<Employee> employees = employeeService.getEmployeesBySupervisor(supervisorId);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/update-commander")
    public ResponseEntity<String> updateCommander(@RequestParam Long newCommanderId) {
        Employee oldCommander = employeeService.getCommanders().get(0);

        Employee newCommander = employeeService.getEmployeeById(newCommanderId);

        List<Employee> depEmp=employeeService.getEmployeesBySupervisor(newCommander.getEmployeeId());
        for (Employee employee : depEmp) {
            employee.setSupervisor(oldCommander);
            employeeRepository.save(employee);
        }

        Departament departament=departamentService.getDepartamentBySupervisor(newCommander);
        departament.setSupervisor(oldCommander);
        departamentRepository.save(departament);


        newCommander.setSupervisor(null);
        employeeRepository.save(newCommander);

        oldCommander.setSupervisor(newCommander);
        employeeRepository.save(oldCommander);

        List<Departament> departments = departamentService.getAllDepartaments();
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

        for (Employee employee : heads) {
            employee.setSupervisor(newCommander);
            employeeRepository.save(employee);
        }

        Role role=Role.ROLE_ADMIN;
        List<Employee> admins = employeeService.getEmployeesByRole(role);
        for (Employee admin : admins) {
            admin.setSupervisor(newCommander);
            employeeRepository.save(admin);
        }

        return ResponseEntity.ok("Commander updated successfully.");
    }

    @GetMapping("/get-departament-name/{userId}")
    public String getDepartamentName(@PathVariable Long userId){
        return employeeService.getDepartamentName(userId);
    }

    @GetMapping("/show-requests-to-sign-button/{userId}")
    public boolean showRequestsToSignButton(@PathVariable Long userId, @RequestHeader("Authorization") String authHeader){
        return employeeService.showRequestsToSignButton(userId,authHeader);
    }

}
