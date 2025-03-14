package ro.mta.toggleserverapi.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<?> getAllUsersWithRoles(){
        UsersResponseDTO usersResponseDTO = userService.getAllUsers();
        return ResponseEntity.ok(usersResponseDTO);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId){
        User user=userRepository.findByHashId(userId).orElseThrow();
        UserDTO userDTO = userService.getUser(user.getId());
        return ResponseEntity.ok(userDTO);

    }
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO){
        User user = userConverter.fromDTO(userDTO);
        UserDTO savedUser = userService.createUser(user);
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(savedUser.getId())).toUri())
                .body(savedUser);

    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO,
                                        @PathVariable String userId){
        User user=userRepository.findByHashId(userId).orElseThrow();
        UserDTO updatedUser = userService.updateUser(userDTO, user.getId());
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(updatedUser.getId())).toUri())
                .body(updatedUser);

    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId){
        User user=userRepository.findByHashId(userId).orElseThrow();
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();

    }

    @GetMapping(path = "/instance-admins")
    public ResponseEntity<?> getUsersWithInstanceAdminRole() {
        List<UserDTO> users = userService.getUsersWithInstanceAdminRole();
        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/project-admins")
    public ResponseEntity<?> getUsersWithProjectAdminRole() {
        List<UserDTO> users = userService.getUsersWithProjectAdminRole();
        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/system-admins")
    public ResponseEntity<?> getUsersWithSystemAdminRole() {
        List<UserDTO> users = userService.getUsersWithSystemAdminRole();
        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/emails")
    public ResponseEntity<?> getAllUserEmails(){
        List<String> userEmails = userService.getAllUserEmails();
        return ResponseEntity.ok(userEmails);
    }

    @GetMapping(path = "/roles")
    public ResponseEntity<?> getAllRoles(){
        List<String> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping(path = "/{userId}/user-projects")
    public ResponseEntity<?> getUserProjectsByUserId(@PathVariable String userId) {
        User user=userRepository.findByHashId(userId).orElseThrow();
        List<UserProject> userProjects = userProjectService.getUserProjectsByUserId(user.getId());
        List<UserProjectDTO> userProjectDTOs = userProjects.stream()
                .map(userProject -> {
                    UserProjectDTO dto = new UserProjectDTO();
                    dto.setId(userProject.getUser().getHashId()); // Assuming you want to set the userId
                    dto.setUserName(userProject.getUser().getName());
                    dto.setProjectName(userProject.getProject().getName());
                    dto.setProjectId(userProject.getProject().getHashId());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(userProjectDTOs);
    }

    @GetMapping(path = "/{userId}/user-instances")
    public ResponseEntity<?> getUserInstancesByUserId(@PathVariable String userId) {
        User user=userRepository.findByHashId(userId).orElseThrow();
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
    }

    @GetMapping(path = "/{userId}/admin-projects")
    public ResponseEntity<?> getProjectsForProjectAdmin(@PathVariable String userId) {
        User user=userRepository.findByHashId(userId).orElseThrow();
        List<ProjectDTO> projects = userService.getProjectsForProjectAdmin(user.getId());
        return ResponseEntity.ok(projects);
    }

    @GetMapping(path = "/{userId}/admin-instances")
    public ResponseEntity<?> getInstancesForInstanceAdmin(@PathVariable String userId) {
        User user=userRepository.findByHashId(userId).orElseThrow();
        List<InstanceDTO> instances = userService.getInstancesForInstanceAdmin(user.getId());
        return ResponseEntity.ok(instances);
    }
}
