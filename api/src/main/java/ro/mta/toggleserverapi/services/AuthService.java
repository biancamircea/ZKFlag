package ro.mta.toggleserverapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.repositories.UserRepository;
import ro.mta.toggleserverapi.security.JwtUtil;
import ro.mta.toggleserverapi.entities.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            return new String(org.springframework.security.crypto.codec.Hex.encode(hashedBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hashedPassword = hashPassword(password);
            if (user.getPassword().equals(hashedPassword)) {

                return jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getRoleType().name());
            }
        }
        return null;
    }

}
