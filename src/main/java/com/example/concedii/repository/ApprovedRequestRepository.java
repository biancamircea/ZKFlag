package com.example.concedii.repository;

import com.example.concedii.model.ApprovedRequest;
import com.example.concedii.model.Employee;
import com.example.concedii.model.SubmittedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedRequestRepository extends JpaRepository<ApprovedRequest, Long> {
    public List<ApprovedRequest> findBySubmittedRequest(SubmittedRequest submittedRequest);
    public List<ApprovedRequest> findBySubmittedRequestEmployee(Employee employee);
}
