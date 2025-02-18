package ro.mta.toggleserverapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.UserDTO;
import ro.mta.toggleserverapi.DTOs.UsersResponseDTO;
import ro.mta.toggleserverapi.converters.UserConverter;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.exceptions.UserNotFoundException;
import ro.mta.toggleserverapi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;

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
        return userRepository.save(user);
    }

    public UserDTO createUser(User user) {
        User savedUser = saveUser(user);
        return userConverter.toDTO(savedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    public UserDTO updateUser(User user, Long userId) {
        User updatedUser = userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setEmail(user.getEmail());
                    existingUser.setName(user.getName());
                    existingUser.setRole(user.getRole());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> saveUser(user));
        return userConverter.toDTO(updatedUser);
    }


//    public UserDTO authenticateUser(String email, String password) {
//        Optional<User> userOptional = userRepository.findByEmailAndPassword(email, password);
//        if (userOptional.isPresent()) {
//            return userConverter.toDTO(userOptional.get());
//        } else {
//            return null;
//
//        }
//    }
}
