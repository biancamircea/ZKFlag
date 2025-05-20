package ro.mta.toggleserverapi.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.*;
import ro.mta.toggleserverapi.converters.UserConverter;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.ResourceNotFoundException;
import ro.mta.toggleserverapi.repositories.UserRepository;
import ro.mta.toggleserverapi.services.UserInstanceService;
import ro.mta.toggleserverapi.services.UserProjectService;
import ro.mta.toggleserverapi.services.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserConverter userConverter;
    private final UserProjectService userProjectService;
    private final UserInstanceService userInstanceService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllUsersWithRoles() {
        log.info("Fetching all users with roles");
        try {
            UsersResponseDTO usersResponseDTO = userService.getAllUsers();
            return ResponseEntity.ok(usersResponseDTO);
        } catch (Exception e) {
            log.error("Error fetching all users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        log.info("Fetching user with id {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            UserDTO userDTO = userService.getUser(user.getId());
            return ResponseEntity.ok(userDTO);
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error fetching user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO) {
        log.info("Creating new user with data: {}", userDTO);
        try {
            User user = userConverter.fromDTO(userDTO);
            UserDTO savedUser = userService.createUser(user);
            log.info("User created successfully: {}", savedUser.getId());
            return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(savedUser.getId())).toUri())
                    .body(savedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO,
                                        @PathVariable String userId) {
        log.info("Updating user with id {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            UserDTO updatedUser = userService.updateUser(userDTO, user.getId());
            log.info("User updated successfully: {}", userId);
            return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(updatedUser.getId())).toUri())
                    .body(updatedUser);
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error updating user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        log.info("Deleting user with id {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            userService.deleteUser(user.getId());
            log.info("User deleted successfully: {}", userId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/instance-admins")
    public ResponseEntity<?> getUsersWithInstanceAdminRole() {
        log.info("Fetching users with INSTANCE_ADMIN role");
        try {
            List<UserDTO> users = userService.getUsersWithInstanceAdminRole();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching instance admin users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/project-admins")
    public ResponseEntity<?> getUsersWithProjectAdminRole() {
        log.info("Fetching users with PROJECT_ADMIN role");
        try {
            List<UserDTO> users = userService.getUsersWithProjectAdminRole();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching project admin users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/system-admins")
    public ResponseEntity<?> getUsersWithSystemAdminRole() {
        log.info("Fetching users with SYSTEM_ADMIN role");
        try {
            List<UserDTO> users = userService.getUsersWithSystemAdminRole();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching system admin users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/emails")
    public ResponseEntity<?> getAllUserEmails() {
        log.info("Fetching all user emails");
        try {
            List<String> userEmails = userService.getAllUserEmails();
            return ResponseEntity.ok(userEmails);
        } catch (Exception e) {
            log.error("Error fetching user emails: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/roles")
    public ResponseEntity<?> getAllRoles() {
        log.info("Fetching all roles");
        try {
            List<String> roles = userService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.error("Error fetching roles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{userId}/user-projects")
    public ResponseEntity<?> getUserProjectsByUserId(@PathVariable String userId) {
        log.info("Fetching user-projects for user {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            List<UserProject> userProjects = userProjectService.getUserProjectsByUserId(user.getId());
            List<UserProjectDTO> userProjectDTOs = userProjects.stream()
                    .map(userProject -> {
                        UserProjectDTO dto = new UserProjectDTO();
                        dto.setId(userProject.getUser().getHashId());
                        dto.setUserName(userProject.getUser().getName());
                        dto.setProjectName(userProject.getProject().getName());
                        dto.setProjectId(userProject.getProject().getHashId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userProjectDTOs);
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error fetching user-projects for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{userId}/user-instances")
    public ResponseEntity<?> getUserInstancesByUserId(@PathVariable String userId) {
        log.info("Fetching user-instances for user {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            List<UserInstance> userInstances = userInstanceService.getUserInstancesByUserId(user.getId());
            List<UserInstanceDTO> userInstanceDTOs = userInstances.stream()
                    .map(userInstance -> {
                        UserInstanceDTO dto = new UserInstanceDTO();
                        dto.setId(userInstance.getInstance().getHashId());
                        dto.setUserName(userInstance.getUser().getName());
                        dto.setInstanceName(userInstance.getInstance().getName());
                        dto.setInstanceId(userInstance.getInstance().getHashId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userInstanceDTOs);
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error fetching user-instances for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{userId}/admin-projects")
    public ResponseEntity<?> getProjectsForProjectAdmin(@PathVariable String userId) {
        log.info("Fetching admin projects for user {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            List<ProjectDTO> projects = userService.getProjectsForProjectAdmin(user.getId());
            return ResponseEntity.ok(projects);
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error fetching admin projects for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    @GetMapping(path = "/{userId}/admin-instances")
    public ResponseEntity<?> getInstancesForInstanceAdmin(@PathVariable String userId) {
        log.info("Fetching admin instances for user {}", userId);
        try {
            User user = userRepository.findByHashId(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            List<InstanceDTO> instances = userService.getInstancesForInstanceAdmin(user.getId());
            return ResponseEntity.ok(instances);
        } catch (NoSuchElementException e) {
            log.warn("User not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            log.error("Error fetching admin instances for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}
