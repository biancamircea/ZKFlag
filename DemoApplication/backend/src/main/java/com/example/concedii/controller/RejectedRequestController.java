package com.example.concedii.controller;

import com.example.concedii.model.Employee;
import com.example.concedii.model.RejectedRequest;
import com.example.concedii.model.SubmittedRequest;
import com.example.concedii.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.sdk.ToggleSystemClient;
import ro.mta.sdk.ToggleSystemContext;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rejected")
public class RejectedRequestController {
    private final RejectedRequestService rejectedRequestService;
    private final SubmittedRequestService submittedRequestService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    private ToggleSystemClient toggleSystemClient;

    @Value("${project_name}")
    private String project_name;

    @Autowired
    public RejectedRequestController(RejectedRequestService rejectedRequestService,
                                     SubmittedRequestService submittedRequestService,
                                     EmailService emailService,
                                     NotificationService notificationService) {
        this.rejectedRequestService = rejectedRequestService;
        this.submittedRequestService = submittedRequestService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public List<RejectedRequest> getRejectedRequests() {
        return rejectedRequestService.getAllRejectedRequests();
    }

    @GetMapping("/{id}")
    public RejectedRequest getRejectedRequestById(@PathVariable("id") Long id) {
        return rejectedRequestService.getRejectedRequestById(id);
    }

    @PostMapping("/")
    public ResponseEntity<RejectedRequest> createRejectedRequest(@RequestBody RejectedRequest rejectedRequest) {
        if (rejectedRequest.getSubmittedRequest() == null || rejectedRequest.getSubmittedRequest().getRequestId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        SubmittedRequest submittedRequest = submittedRequestService.getSubmittedRequestById(rejectedRequest.getSubmittedRequest().getRequestId());
        if (submittedRequest == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        rejectedRequest.setSubmittedRequest(submittedRequest);
        rejectedRequest.setRejectionDate(LocalDate.now());

        RejectedRequest createdRequest = rejectedRequestService.createRejectedRequest(rejectedRequest);

        ToggleSystemContext context = ToggleSystemContext.builder()
                .addContext("port", "3000")
                .build();
        boolean isEnabled=toggleSystemClient.isEnabled("feature_test",context);


        if(isEnabled) {
            //sendRejectionEmail(submittedRequest.getEmployee(), rejectedRequest);

            String message = String.format("Your request for %s from %s to %s has been rejected.",
                    submittedRequest.getReason(), submittedRequest.getStartDate(), submittedRequest.getEndDate());
            notificationService.createNotification(message, "requests", submittedRequest.getEmployee().getEmployeeId());
        }

        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    private void sendRejectionEmail(Employee employee, RejectedRequest rejectedRequest) {
        String employeeEmail = employee.getEmail();
        String emailSubject = "Your leave request has been rejected";
        String emailText = String.format(
                "Dear %s %s,\n\n" +
                        "Your leave request has been rejected:\n" +
                        "From: %s\n" +
                        "To: %s\n" +
                        "Reason: %s\n\n" +
                        "Best regards,\n" +
                        "Your HR Team",
                employee.getFirstName(),
                employee.getLastName(),
                rejectedRequest.getSubmittedRequest().getStartDate(),
                rejectedRequest.getSubmittedRequest().getEndDate(),
                rejectedRequest.getRejectionReason()
        );

        emailService.sendEmail(employeeEmail, emailSubject, emailText);
    }



    @DeleteMapping("{id}")
    public void deleteRejectedRequest(@PathVariable("id") Long id) {
        rejectedRequestService.deleteRejectedRequest(id);
    }

    @PostMapping("/employee/{employeeId}")
    public List<RejectedRequest> getRejectedRequestsByEmployeeId(@PathVariable("employeeId") Long employeeId) {
        return rejectedRequestService.getRejectedRequestsByEmployeeId(employeeId);
    }
}
