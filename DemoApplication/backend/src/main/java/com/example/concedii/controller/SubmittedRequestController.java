package com.example.concedii.controller;

import com.example.concedii.model.Employee;
import com.example.concedii.model.SubmittedRequest;
import com.example.concedii.repository.EmployeeRepository;
import com.example.concedii.repository.SubmittedRequestRepository;
import com.example.concedii.service.DepartamentService;
import com.example.concedii.service.EmployeeService;
import com.example.concedii.service.SubmittedRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/submitted")
public class SubmittedRequestController {
    private final SubmittedRequestService submittedRequestService;
    private final EmployeeService employeeService;
    private final DepartamentService departamentService;

    @Autowired
    public SubmittedRequestController(SubmittedRequestService submittedRequestService,
                                      EmployeeService employeeService,
                                      DepartamentService departamentService) {
        this.submittedRequestService = submittedRequestService;
        this.employeeService = employeeService;
        this.departamentService = departamentService;
    }

    @GetMapping("/")
    public List<SubmittedRequest> getSubmittedRequests() {
        return submittedRequestService.getAllSubmittedRequests();
    }

    @GetMapping("/{id}")
    public SubmittedRequest getSubmittedRequestById(@PathVariable("id") Long id) {
        return submittedRequestService.getSubmittedRequestById(id);
    }

    @PostMapping("/")
    public ResponseEntity<SubmittedRequest> createSubmittedRequest(@RequestBody SubmittedRequest submittedRequest) {
        if (submittedRequest.getEmployee() == null || submittedRequest.getEmployee().getEmployeeId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Employee employee = employeeService.getEmployeeById(submittedRequest.getEmployee().getEmployeeId());
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        submittedRequest.setEmployee(employee);
        try {
            SubmittedRequest createdRequest = submittedRequestService.createSubmittedRequest(submittedRequest);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }


    @DeleteMapping("{id}")
    public void deleteSubmittedRequest(@PathVariable("id") Long id) {
        submittedRequestService.deleteSubmittedRequest(id);
    }


    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SubmittedRequest>> getEmployeeRequests(@PathVariable Long employeeId) {
        List<SubmittedRequest> pendingRequests = submittedRequestService.getPendingRequestsByEmployeeId(employeeId);
        return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
    }

    @GetMapping("/requests-for-sign")
    public List<SubmittedRequest> getRequestsForSign(@RequestParam Long employeeId) {
        Employee employee =employeeService.getEmployeeById(employeeId);

        List<SubmittedRequest> requests = new ArrayList<>();

        if (employee.getSupervisor() == null) {

            List<SubmittedRequest> hisRequests = submittedRequestService.getPendingRequestsByEmployeeId(employeeId);

            List<Employee> subordinated=employeeService.getEmployeesBySupervisor(employeeId);

            for(Employee e:subordinated) {
                requests.addAll(submittedRequestService.getPendingRequestsByEmployeeId(e.getEmployeeId()));
            }
            requests.addAll(hisRequests);

        } else if (departamentService.isDepartmentHead(employeeId)) {

            List<Employee> subordinated=employeeService.getEmployeesBySupervisor(employeeId);

            for(Employee e:subordinated) {
                requests.addAll(submittedRequestService.getPendingRequestsByEmployeeId(e.getEmployeeId()));
            }

        } else {
            throw new RuntimeException("You don’t have rights to sign requests.");
        }

        return submittedRequestService.filterPendingRequests(requests);
    }
}
