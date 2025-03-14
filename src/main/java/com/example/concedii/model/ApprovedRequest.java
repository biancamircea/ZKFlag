package com.example.concedii.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvedRequestId;

    @ManyToOne
    @JoinColumn(name = "request_id",nullable = false)
    private SubmittedRequest submittedRequest;

    @Column
    private LocalDate approvalDate;
}

