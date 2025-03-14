package com.example.concedii.service;

import com.example.concedii.model.Employee;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VacationResetService {

    @Autowired
    private EmployeeService employeeService;

    @Transactional
    @Scheduled(cron = "0 0 0 1 1 *")
    public void resetVacationDaysForAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee employee : employees) {
            employee.setRemainingVacationDays(35);
        }
        employeeService.saveEmployees();
    }
}
