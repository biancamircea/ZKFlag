package com.example.concedii.model;

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
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Employee supervisor;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private int remainingVacationDays = 35;


    public long getSupervisorId() {
        if (this.supervisor == null) {
            return -1;
        }
        return supervisor.getEmployeeId();
    }

    public void deductVacationDays(int days) {
        if (remainingVacationDays >= days) {
            this.remainingVacationDays -= days;
        } else {
            throw new IllegalArgumentException("Not enough vacation days remaining.");
        }
    }
}
