package com.example.concedii.controller;

import com.example.concedii.model.AuthResponse;
import com.example.concedii.model.LoginRequest;
import com.example.concedii.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = employeeService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}