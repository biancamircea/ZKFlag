package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.InstanceDTO;
import ro.mta.toggleserverapi.DTOs.ProjectDTO;
import ro.mta.toggleserverapi.DTOs.UserDTO;
import ro.mta.toggleserverapi.DTOs.UsersResponseDTO;
import ro.mta.toggleserverapi.converters.UserConverter;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.enums.OperatorType;
import ro.mta.toggleserverapi.exceptions.ResourceNotFoundException;
import ro.mta.toggleserverapi.exceptions.UserNotFoundException;
import ro.mta.toggleserverapi.repositories.*;
import ro.mta.toggleserverapi.exceptions.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInstanceRepository userInstanceRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectRepository projectRepository;

    private List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    public User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserDTO getUser(Long userId) {
        User user = fetchUserById(userId);
        return userConverter.toDTO(user);
    }

    public UsersResponseDTO getAllUsers() {
        List<User> users = fetchAllUsers();
        return userConverter.toListOfDTO(users);
    }

    private User saveUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser=userRepository.save(user);

        if (savedUser.getHashId() == null || savedUser.getHashId().isEmpty()) {
            Hashids hashids = new Hashids("user-salt", 8);
            savedUser.setHashId(hashids.encode(savedUser.getId()));

            savedUser =userRepository.save(savedUser);
        }

        return savedUser;
    }

    public UserDTO createUser(User user) {
        User savedUser = saveUser(user);
        return userConverter.toDTO(savedUser);
    }

    public void deleteUser(Long userId) {
        User user = fetchUserById(userId);

        if (user.getRole().getRoleType().name().equals("InstanceAdmin")) {
            List<UserInstance> userInstances = userInstanceRepository.findByUserId(userId);
            if(userInstances.size()>0){
                userInstanceRepository.deleteAll(userInstances);
            }
        }else if(user.getRole().getRoleType().name().equals("ProjectAdmin")){
            List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
           userProjectRepository.deleteAll(userProjects);
        }else{
            List<UserDTO> sysAdmins= getUsersWithSystemAdminRole();
            if(sysAdmins.size()==1){
                throw new IllegalArgumentException("Cannot delete the only system admin");
            }
        }
        userRepository.deleteById(userId);
    }


    public UserDTO updateUser(UserDTO user, Long userId) {
        User updatedUser2 = userRepository.findById(userId).orElseThrow();

        if(user.getEmail()!=null){
            updatedUser2.setEmail(user.getEmail());
        }
        if(user.getName()!=null){
            updatedUser2.setName(user.getName());
        }
        if(user.getPassword()!=null){
            updatedUser2.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(updatedUser2);

        return userConverter.toDTO(updatedUser2);
    }

    public List<UserDTO> getUsersWithInstanceAdminRole() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.getRole().getRoleType().name().equals("InstanceAdmin"))
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersWithProjectAdminRole() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.getRole().getRoleType().name().equals("ProjectAdmin"))
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersWithSystemAdminRole() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.getRole().getRoleType().name().equals("SystemAdmin"))
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllUserEmails(){
        List<User> users = fetchAllUsers();
        return users.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    public List<String> getAllRoles(){
        return roleRepository.findAll()
                .stream()
                .map(role -> role.getRoleType().name())
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsForProjectAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().getRoleType().name().equals("ProjectAdmin")) {
            throw new AccessDeniedException("User is not a project admin");
        }

        List<UserProject> userProjects = userProjectRepository.findByUserId(userId);
        List<Project> projects = userProjects.stream()
                .map(UserProject::getProject)
                .collect(Collectors.toList());

        return projects.stream()
                .map(project -> {
                    ProjectDTO dto = new ProjectDTO();
                    dto.setId(project.getHashId());
                    dto.setName(project.getName());
                    dto.setDescription(project.getDescription());
                    dto.setMemberCount(project.getUserProjectRole().stream().count());
                    dto.setToggleCount(project.getToggleList().stream().count());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<InstanceDTO> getInstancesForInstanceAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().getRoleType().name().equals("InstanceAdmin")) {
            throw new AccessDeniedException("User is not an instance admin");
        }

        List<UserInstance> userInstances = userInstanceRepository.findByUserId(userId);
        List<Instance> instances = userInstances.stream()
                .map(UserInstance::getInstance)
                .collect(Collectors.toList());


        return instances.stream()
                .map(instance -> {
                    InstanceDTO dto = new InstanceDTO();
                    dto.setId(instance.getHashId());
                    dto.setName(instance.getName());
                    dto.setStartedAt(instance.getStartedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
