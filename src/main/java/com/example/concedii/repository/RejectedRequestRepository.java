package com.example.concedii.repository;

import com.example.concedii.model.RejectedRequest;
import com.example.concedii.model.SubmittedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RejectedRequestRepository extends JpaRepository<RejectedRequest, Long> {
   public List<RejectedRequest> findBySubmittedRequest(SubmittedRequest submittedRequest);
}
