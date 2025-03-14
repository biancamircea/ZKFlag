package ro.mta.toggleserverapi.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ro.mta.toggleserverapi.DTOs.LoginRequestDTO;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.repositories.UserRepository;
import ro.mta.toggleserverapi.security.JwtUtil;
import ro.mta.toggleserverapi.services.AuthService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getRoleType().name());
        jwtUtil.addTokenToResponse(response, token);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().getRoleType().name());


        return ResponseEntity.ok(userData);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully!");
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "jwt", required = false) String token, HttpServletResponse response) {

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(username).orElseThrow();

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getHashId());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().getRoleType().name());
        userData.put("name", user.getName());

        return ResponseEntity.ok(userData);
    }

}

