package com.example.concedii.controller;

import com.example.concedii.model.Departament;
import com.example.concedii.model.Employee;
import com.example.concedii.service.DepartamentService;
import com.example.concedii.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departaments")
public class DepartamentController {
    private final DepartamentService departamentService;
    private final EmployeeService employeeService;

    @Autowired
    public DepartamentController(DepartamentService departamentService, EmployeeService employeeService) {
        this.departamentService = departamentService;
        this.employeeService=employeeService;
    }

    @GetMapping("/")
    public List<Departament> getAllDepartaments() {
        return departamentService.getAllDepartaments();
    }

    @GetMapping("/{id}")
    public Departament getDepartamentById(@PathVariable("id") Long id) {
        return departamentService.getDepartamentById(id);
    }

    @PostMapping("/")
    public ResponseEntity<Departament> createDepartament(@RequestBody Departament departament) {

        Employee supervisor = employeeService.getEmployeeById(departament.getSupervisor().getEmployeeId());
        if (supervisor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Employee> commanders = employeeService.getCommanders();

        if (commanders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        supervisor.setSupervisor(commanders.get(0));

        departament.setSupervisor(supervisor);

        Departament createdDepartament = departamentService.createDepartament(departament);
        return new ResponseEntity<>(createdDepartament, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Departament updateDepartament(@PathVariable("id") Long id, @RequestBody Departament departament) {
        return departamentService.updateDepartament(departament, id);
    }

    @GetMapping("/supervisor/{idDep}")
    public Employee getSupervisor(@PathVariable("idDep") Long idDep) {
        return departamentService.getSupervisorById(idDep);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartament(@PathVariable("id") Long id) {
        departamentService.deleteDepartamentById(id);
    }

    @PutMapping("/{departmentId}/updateHead")
    public ResponseEntity<String> updateDepartmentHead(
            @PathVariable Long departmentId,
            @RequestParam Long newHeadId) {

        try {
            departamentService.updateDepartmentHead(departmentId, newHeadId);
            return ResponseEntity.ok("Department head updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating department head: " + e.getMessage());
        }
    }

    @GetMapping("/heads")
    public ResponseEntity<List<Employee>> getDepartmentFHeads() {

        List<Employee> heads = new ArrayList<>();
        heads=departamentService.getDepartmensHeads();
        return ResponseEntity.ok(heads);
    }
}
