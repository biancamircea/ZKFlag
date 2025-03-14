package com.example.concedii.service;

import com.example.concedii.model.Employee;
import com.example.concedii.model.RejectedRequest;
import com.example.concedii.model.SubmittedRequest;
import com.example.concedii.repository.RejectedRequestRepository;
import com.example.concedii.repository.SubmittedRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RejectedRequestService {
    private final RejectedRequestRepository rejectedRequestRepository;
    private final EmployeeService employeeService;
    private final SubmittedRequestRepository submittedRequestRepository;

    @Autowired
    public RejectedRequestService(RejectedRequestRepository rejectedRequestRepository
    , EmployeeService employeeService
    , SubmittedRequestRepository submittedRequestRepository) {
        this.rejectedRequestRepository = rejectedRequestRepository;
        this.employeeService = employeeService;
        this.submittedRequestRepository = submittedRequestRepository;
    }

    public List<RejectedRequest> getAllRejectedRequests() {
        return rejectedRequestRepository.findAll();
    }

    public RejectedRequest getRejectedRequestById(Long id) {
        return rejectedRequestRepository.findById(id).orElse(null);
    }

    public RejectedRequest createRejectedRequest(RejectedRequest rejectedRequest) {
        return rejectedRequestRepository.save(rejectedRequest);
    }

    public void deleteRejectedRequest(Long id){
       RejectedRequest rejectedRequest = getRejectedRequestById(id);
       if(rejectedRequest != null){
           rejectedRequestRepository.delete(rejectedRequest);
       }
    }


    public List<RejectedRequest> getRejectedRequestsByEmployeeId(Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<SubmittedRequest> submittedRequests = submittedRequestRepository.findByEmployee(employee);

        List<RejectedRequest> rejectedRequests = rejectedRequestRepository.findAll();

        return rejectedRequests.stream()
                .filter(rejectedRequest -> submittedRequests.stream()
                        .anyMatch(submittedRequest -> submittedRequest.getRequestId().equals(rejectedRequest.getSubmittedRequest().getRequestId()))
                ).collect(Collectors.toList());
    }
}
