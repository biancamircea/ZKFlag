package com.example.concedii.service;

import com.example.concedii.model.ApprovedRequest;
import com.example.concedii.model.Employee;
import com.example.concedii.model.RejectedRequest;
import com.example.concedii.model.SubmittedRequest;
import com.example.concedii.repository.ApprovedRequestRepository;
import com.example.concedii.repository.RejectedRequestRepository;
import com.example.concedii.repository.SubmittedRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.mta.sdk.ToggleSystemClient;
import ro.mta.sdk.ToggleSystemContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmittedRequestService {
    private final SubmittedRequestRepository submittedRequestRepository;
    private final EmployeeService employeeService;
    private final ApprovedRequestRepository approvedRequestRepository;
    private final RejectedRequestRepository rejectedRequestRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    private ToggleSystemClient toggleSystemClient;

    @Value("${project_name}")
    private String project_name;

    @Autowired
    public SubmittedRequestService(SubmittedRequestRepository submittedRequestRepository, EmployeeService employeeService,
                                   ApprovedRequestRepository approvedRequestRepository,
                                   RejectedRequestRepository rejectedRequestRepository,
                                   EmailService emailService,
                                   NotificationService notificationService) {
        this.submittedRequestRepository = submittedRequestRepository;
        this.employeeService = employeeService;
        this.approvedRequestRepository = approvedRequestRepository;
        this.rejectedRequestRepository = rejectedRequestRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public List<SubmittedRequest> getAllSubmittedRequests() {
        return submittedRequestRepository.findAll();
    }

    public SubmittedRequest getSubmittedRequestById(Long id) {
        return submittedRequestRepository.findById(id).orElse(null);
    }

    @Transactional
    public SubmittedRequest createSubmittedRequest(SubmittedRequest newRequest) {
        Employee employee = newRequest.getEmployee();

        long daysRequested = ChronoUnit.DAYS.between(newRequest.getStartDate(), newRequest.getEndDate()) + 1;

        if (employee.getRemainingVacationDays() < daysRequested) {
            throw new IllegalArgumentException("Not enough vacation days remaining.");
        }

        List<ApprovedRequest> acceptedRequests = approvedRequestRepository.findBySubmittedRequestEmployee(employee);
        LocalDate newStartDate = newRequest.getStartDate();
        LocalDate newEndDate = newRequest.getEndDate();

        boolean isOverlapping = acceptedRequests.stream().anyMatch(acceptedRequest ->
                areDatesOverlapping(
                        acceptedRequest.getSubmittedRequest().getStartDate(),
                        acceptedRequest.getSubmittedRequest().getEndDate(),
                        newStartDate,
                        newEndDate
                )
        );

        if (isOverlapping) {
            throw new IllegalArgumentException("The requested period overlaps with an existing approved request.");
        }


        newRequest = submittedRequestRepository.save(newRequest);

        ToggleSystemContext context = ToggleSystemContext.builder()
                .addProperty("port", "3000")
                .build();
        boolean isEnabled=toggleSystemClient.isEnabled("send_email_and_notifications",context);

        if(isEnabled) {
            if (employee.getSupervisor() != null) {
                Employee supervisor = employee.getSupervisor();
                String supervisorEmail = supervisor.getEmail();
                String emailSubject = "You have a new request to sign";
                String emailText = String.format(
                        "You have a new request to sign:\n" +
                                "Employee: %s %s\n" +
                                "From date: %s\n" +
                                "To date: %s\n" +
                                "Reason: %s\n" +
                                "Please log in to the platform to manage this request.",
                        employee.getLastName(), employee.getFirstName(),
                        newRequest.getStartDate(), newRequest.getEndDate(), newRequest.getReason()
                );

                emailService.sendEmail(supervisorEmail, emailSubject, emailText);
            }

            if (employee.getSupervisor() != null) {
                Employee supervisor2 = employee.getSupervisor();
                String message = String.format("You have a new request to sign from: %s %s",
                        employee.getLastName(), employee.getFirstName());
                notificationService.createNotification(message, "request_for_sign/" + newRequest.getRequestId(), supervisor2.getEmployeeId());
            } else {
                String message = String.format("You have a new request to sign from: %s %s",
                        employee.getLastName(), employee.getFirstName());
                notificationService.createNotification(message, "request_for_sign/" + newRequest.getRequestId(), employee.getEmployeeId());
            }


        }

        return newRequest;
    }

    private boolean areDatesOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    public void deleteSubmittedRequest(Long id){
        SubmittedRequest submittedRequest = getSubmittedRequestById(id);
        if(submittedRequest != null){
            submittedRequestRepository.delete(submittedRequest);
        }
    }

    public List<SubmittedRequest> getPendingRequestsByEmployeeId(Long employeeId) {
        Employee employee=this.employeeService.getEmployeeById(employeeId);
        return submittedRequestRepository.findByEmployee(employee);
    }

    public List<SubmittedRequest> filterPendingRequests(List<SubmittedRequest> submittedRequests) {
        List<ApprovedRequest> approvedRequests = approvedRequestRepository.findAll();
        List<RejectedRequest> rejectedRequests = rejectedRequestRepository.findAll();

        return submittedRequests.stream()
                .filter(submittedRequest ->
                        approvedRequests.stream()
                                .noneMatch(approvedRequest -> approvedRequest.getSubmittedRequest().getRequestId().equals(submittedRequest.getRequestId())) &&
                                rejectedRequests.stream()
                                        .noneMatch(rejectedRequest -> rejectedRequest.getSubmittedRequest().getRequestId().equals(submittedRequest.getRequestId()))
                )
                .collect(Collectors.toList());
    }
}
