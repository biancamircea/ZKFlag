package com.example.concedii.service;

import com.example.concedii.model.ApprovedRequest;
import com.example.concedii.model.Employee;
import com.example.concedii.model.RejectedRequest;
import com.example.concedii.model.SubmittedRequest;
import com.example.concedii.repository.ApprovedRequestRepository;
import com.example.concedii.repository.EmployeeRepository;
import com.example.concedii.repository.RejectedRequestRepository;
import com.example.concedii.repository.SubmittedRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ro.mta.sdk.ToggleSystemClient;
import ro.mta.sdk.ToggleSystemContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApprovedRequestService {
    private final ApprovedRequestRepository approvedRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final SubmittedRequestRepository submittedRequestRepository;
    private final EmployeeService employeeService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final RejectedRequestRepository rejectedRequestRepository;

    @Autowired
    private ToggleSystemClient toggleSystemClient;

    @Autowired
    public ApprovedRequestService(ApprovedRequestRepository approvedRequestRepository,
                                  EmployeeRepository employeeRepository,
                                  SubmittedRequestRepository submittedRequestRepository,
                                  EmployeeService employeeService,
                                  EmailService emailService,
                                  NotificationService notificationService,
                                  RejectedRequestRepository rejectedRequestRepository) {
        this.approvedRequestRepository = approvedRequestRepository;
        this.employeeRepository = employeeRepository;
        this.submittedRequestRepository = submittedRequestRepository;
        this.employeeService = employeeService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.rejectedRequestRepository = rejectedRequestRepository;
    }

    public List<ApprovedRequest> getApprovedRequests() {
        return approvedRequestRepository.findAll();
    }

    public ApprovedRequest getApprovedRequestById(Long id) {
        return approvedRequestRepository.findById(id).orElse(null);
    }

    @Transactional
    public ApprovedRequest approveRequest(Long submittedRequestId) {
        SubmittedRequest submittedRequest = submittedRequestRepository.findById(submittedRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        Employee employee = submittedRequest.getEmployee();
        List<ApprovedRequest> existingApprovedRequests = approvedRequestRepository.findBySubmittedRequestEmployee(employee);

        LocalDate newStartDate = submittedRequest.getStartDate();
        LocalDate newEndDate = submittedRequest.getEndDate();

        boolean isOverlapping = existingApprovedRequests.stream().anyMatch(existingRequest ->
                areDatesOverlapping(
                        existingRequest.getSubmittedRequest().getStartDate(),
                        existingRequest.getSubmittedRequest().getEndDate(),
                        newStartDate,
                        newEndDate
                )
        );

        if (isOverlapping) {
            throw new IllegalArgumentException("The requested period overlaps with an existing approved request.");
        }

        long daysRequested = ChronoUnit.DAYS.between(newStartDate, newEndDate) + 1;

        if (employee.getRemainingVacationDays() < daysRequested) {
            throw new IllegalArgumentException("Not enough vacation days remaining.");
        }

        employee.setRemainingVacationDays(employee.getRemainingVacationDays() - (int) daysRequested);
        employeeRepository.save(employee);

        ApprovedRequest approvedRequest = new ApprovedRequest();
        approvedRequest.setSubmittedRequest(submittedRequest);
        approvedRequest.setApprovalDate(LocalDate.now());
        ApprovedRequest savedApprovedRequest = approvedRequestRepository.save(approvedRequest);

        ToggleSystemContext context = ToggleSystemContext.builder()
                .addProperty("port", "3000")
                .build();


        boolean isEnabled=toggleSystemClient.isEnabled("send_email_and_notifications",context);

        System.out.println("isEnabled cu context: "+isEnabled);

        if (isEnabled) {
            String message = String.format("Your request for %s from %s to %s has been approved.",
                    submittedRequest.getReason(), newStartDate, newEndDate);
            notificationService.createNotification(message, "requests", employee.getEmployeeId());

            sendApprovalEmail(employee, submittedRequest);
        }

        return savedApprovedRequest;
    }

    private void sendApprovalEmail(Employee employee, SubmittedRequest submittedRequest) {
        String employeeEmail = employee.getEmail();
        String emailSubject = "Your leave request has been approved";
        String emailText = String.format(
                "Dear %s %s,\n\n" +
                        "Your leave request has been approved:\n" +
                        "From: %s\n" +
                        "To: %s\n" +
                        "Reason: %s\n\n" +
                        "Best regards,\n" +
                        "Your HR Team",
                employee.getFirstName(),
                employee.getLastName(),
                submittedRequest.getStartDate(),
                submittedRequest.getEndDate(),
                submittedRequest.getReason()
        );

        emailService.sendEmail(employeeEmail, emailSubject, emailText);
    }

    private boolean areDatesOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    public void deleteApprovedRequest(Long id) {

            ApprovedRequest approvedRequest = getApprovedRequestById(id);
            if (approvedRequest != null) {
                RejectedRequest rejectedRequest = new RejectedRequest();
                rejectedRequest.setSubmittedRequest(approvedRequest.getSubmittedRequest());
                rejectedRequest.setRejectionReason("Request cancelled by supervisor");
                rejectedRequest.setRejectionDate(LocalDate.now());

                rejectedRequestRepository.save(rejectedRequest);

                Employee employee = approvedRequest.getSubmittedRequest().getEmployee();
                long daysOff = ChronoUnit.DAYS.between(approvedRequest.getSubmittedRequest().getStartDate(), approvedRequest.getSubmittedRequest().getEndDate());

                employee.setRemainingVacationDays(employee.getRemainingVacationDays() + (int) daysOff + 1);
                employeeRepository.save(employee);

                approvedRequestRepository.delete(approvedRequest);
            }
    }



    public List<ApprovedRequest> getApprovedRequestsByEmployeeId(Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<SubmittedRequest> submittedRequests = submittedRequestRepository.findByEmployee(employee);

        List<ApprovedRequest> approvedRequests = approvedRequestRepository.findAll();

        return approvedRequests.stream()
                .filter(approvedRequest -> submittedRequests.stream()
                        .anyMatch(submittedRequest -> submittedRequest.getRequestId().equals(approvedRequest.getSubmittedRequest().getRequestId()))
                ).collect(Collectors.toList());
    }

    public List<ApprovedRequest> getSignedRequestsBySupervisor(Long supervisorId) {
        List<Employee> employees=employeeService.getEmployeesBySupervisor(supervisorId);
        List<ApprovedRequest> allApprovedRequests = new ArrayList<>();

        for (Employee employee : employees) {
            List<ApprovedRequest> approvedRequests = approvedRequestRepository.findBySubmittedRequestEmployee(employee);
            allApprovedRequests.addAll(approvedRequests);
        }

        return allApprovedRequests;
    }
}
