package com.example.concedii.controller;

import com.example.concedii.model.ApprovedRequest;
import com.example.concedii.service.ApprovedRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/approved")
public class ApprovedRequestController {
    private final ApprovedRequestService approvedRequestService;

    @Autowired
    public ApprovedRequestController(ApprovedRequestService approvedRequestService ){
        this.approvedRequestService = approvedRequestService;
    }

    @GetMapping("/")
    public List<ApprovedRequest> getApprovedRequests() {
        return this.approvedRequestService.getApprovedRequests();
    }

    @GetMapping("/{id}")
    public ApprovedRequest getApprovedRequestById(@PathVariable("id") Long id) {
        return this.approvedRequestService.getApprovedRequestById(id);
    }

    @PostMapping("/")
    public ResponseEntity<ApprovedRequest> createApprovedRequest(@RequestParam Long idSubmitted) {
        ApprovedRequest createdRequest = this.approvedRequestService.approveRequest(idSubmitted);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteApprovedRequest(@PathVariable("id") Long id) {
        this.approvedRequestService.deleteApprovedRequest(id);
    }


    @PostMapping("/employee/{employeeId}")
    public List<ApprovedRequest> getApprovedRequestsByEmployeeId(@PathVariable("employeeId") Long employeeId) {
        return approvedRequestService.getApprovedRequestsByEmployeeId(employeeId);
    }

    @GetMapping("/signed/{userId}")
    public ResponseEntity<List<ApprovedRequest>> getSignedRequestsBySupervisor(@PathVariable Long userId) {
        List<ApprovedRequest> signedRequests = approvedRequestService.getSignedRequestsBySupervisor(userId);
        return ResponseEntity.ok(signedRequests);
    }

}
