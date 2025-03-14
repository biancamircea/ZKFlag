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
public class RejectedRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rejectedRequestId;

    @ManyToOne
    @JoinColumn(name = "request_id",nullable = false)
    private SubmittedRequest submittedRequest;

    @Column(nullable = false)
    private LocalDate rejectionDate;
    private String rejectionReason;
}

