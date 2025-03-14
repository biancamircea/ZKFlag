package com.example.concedii.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Departament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departamentId;

    @OneToOne
    @JoinColumn(name = "supervisor_id",nullable = false)
    private Employee supervisor;

    @Column(nullable = false)
    private String departamentName;

}
