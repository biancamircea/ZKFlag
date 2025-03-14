package com.example.concedii.service;

import com.example.concedii.model.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

import com.example.concedii.repository.EmployeeRepository;
import com.example.concedii.repository.ApprovedRequestRepository;
import com.example.concedii.repository.SubmittedRequestRepository;
import com.example.concedii.repository.RejectedRequestRepository;
import com.example.concedii.repository.DepartamentRepository;
import com.example.concedii.config.WebClientConfig;
import com.example.concedii.model.Employee;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApprovedRequestRepository approvedRequestRepository;
    private final SubmittedRequestRepository submittedRequestRepository;
    private final RejectedRequestRepository rejectedRequestRepository;
    private final DepartamentRepository departamentRepository;

    @Value("${project_name}")
    private String project_name;

    @Value("${SECRET_KEY}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 864_000_000;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           ApprovedRequestRepository approvedRequestRepository,
                           SubmittedRequestRepository submittedRequestRepository,
                           RejectedRequestRepository rejectedRequestRepository,
                           DepartamentRepository departamentRepository) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.approvedRequestRepository = approvedRequestRepository;
        this.submittedRequestRepository = submittedRequestRepository;
        this.rejectedRequestRepository = rejectedRequestRepository;
        this.departamentRepository = departamentRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(()->new RuntimeException("Employee not found"));
    }

    public Employee saveEmployee(Employee employee) {
        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        return this.employeeRepository.save(employee);
    }

    public List<Employee> saveEmployees() {
        return this.employeeRepository.saveAll(getAllEmployees());
    }

    public Employee createEmployee(Employee employee) {
        if (employee.getSupervisor() != null && employee.getSupervisor().getEmployeeId() != null) {
            Employee supervisor = getEmployeeById(employee.getSupervisor().getEmployeeId());
            employee.setSupervisor(supervisor);
        }

        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        this.employeeRepository.save(employee);

        return employee;
    }

    public List<Employee> getCommanders() {
        return employeeRepository.findCommanders();
    }


    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        if (employee != null) {
            List<SubmittedRequest> submittedRequests = submittedRequestRepository.findByEmployee(employee);


            for (SubmittedRequest submittedRequest : submittedRequests) {
                List<ApprovedRequest> approvedRequests = approvedRequestRepository.findBySubmittedRequest(submittedRequest);
                for (ApprovedRequest approvedRequest : approvedRequests) {
                    approvedRequestRepository.delete(approvedRequest);
                }
            }

            for (SubmittedRequest submittedRequest : submittedRequests) {
                List<RejectedRequest> rejectedRequests = rejectedRequestRepository.findBySubmittedRequest(submittedRequest);
                for (RejectedRequest rejectedRequest : rejectedRequests) {
                    rejectedRequestRepository.delete(rejectedRequest);
                }
            }

            for (SubmittedRequest submittedRequest : submittedRequests) {
                submittedRequestRepository.delete(submittedRequest);
            }

            employeeRepository.delete(employee);
        }
    }



    public Employee getEmployeeByEmail(String email) {
        Employee employee = this.employeeRepository.findByEmail(email);
        if(employee == null) {
            throw new RuntimeException("Employee not found");
        }
        return employee;
    }


    public AuthResponse login(String email, String password) {

        Employee employee = employeeRepository.findByEmail(email);
        if (employee != null && passwordEncoder.matches(password, employee.getPassword())) {
            String token= generateToken(employee);
            return new AuthResponse(token, employee.getRole(), employee.getEmployeeId());
        }
        throw new RuntimeException("Invalid email or password");
    }

    private String generateToken(Employee employee) {
        String role = employee.getRole().name();

        return Jwts.builder()
                .setSubject(employee.getEmail())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public List<Employee> getEmployeesBySupervisor(Long supervisorId) {
        Employee employee=employeeRepository.findByEmployeeId(supervisorId);
        return employeeRepository.findBySupervisor(employee);
    }

    public List<Employee> getEmployeesByRole(Role role) {
        return employeeRepository.findByRole(role);
    }

    public String getDepartamentName(Long userId){
        Employee employee=employeeRepository.findByEmployeeId(userId);

        if(employee.getSupervisor()==null)
        {
            return "Comandament";
        }

        Employee supervisor=employeeRepository.findByEmployeeId(employee.getSupervisor().getEmployeeId());

        Departament departament=null;
        Employee commander=this.getCommanders().get(0);

        if(employee.getSupervisor().getEmployeeId()==commander.getEmployeeId()){

            List<Departament> departaments = departamentRepository.findAll();

            for (Departament department1 : departaments) {
                if (department1.getSupervisor().equals(employee)) {
                    departament=department1;
                }
            }
            if(departament==null){
                return "Comandament"; //este admin
            }
        }else{
            List<Departament> departaments = departamentRepository.findAll();

            for (Departament department1 : departaments) {
                if (department1.getSupervisor().equals(supervisor)) {
                    departament=department1;
                }
            }
        }
        return departament.getDepartamentName();
    }

    public boolean showRequestsToSignButton(Long userId, String authHeader) {
        String role = null;
        Employee employee = getEmployeeById(userId);
        if (employee.getSupervisor() == null) {
            role = "commander";
        } else {
            Employee commander = this.getCommanders().get(0);
            if (employee.getSupervisor().getEmployeeId() == commander.getEmployeeId()) {
                role = "head";
            } else {
                role = "employee";
            }
        }


       return  true;
    }
}
