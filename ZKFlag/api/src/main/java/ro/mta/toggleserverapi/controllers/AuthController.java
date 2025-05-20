package ro.mta.toggleserverapi.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.mta.toggleserverapi.DTOs.LoginRequestDTO;
import ro.mta.toggleserverapi.entities.User;
import ro.mta.toggleserverapi.repositories.UserRepository;
import ro.mta.toggleserverapi.security.JwtUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        try {
        log.info("Login attempt for user {}", loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        log.info("User {} authenticated successfully", user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleType().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getRoleType().name()
        );


        jwtUtil.addAccessTokenToResponse(response, accessToken);
        jwtUtil.addRefreshTokenToResponse(response, refreshToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", user.getId());
        responseData.put("email", user.getEmail());
        responseData.put("role", user.getRole().getRoleType().name());

        return ResponseEntity.ok(responseData);

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for user {}", loginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        jwtUtil.clearTokens(response);
        log.info("User logged out");
        return ResponseEntity.ok("Logged out successfully!");
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "accessToken", required = false) String token, HttpServletResponse response) {

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(username).orElseThrow();

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getHashId());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().getRoleType().name());
        userData.put("name", user.getName());
        log.info("Fetching current user info for token belonging to {}", username);

        return ResponseEntity.ok(userData);
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("Attempting to refresh access token");

            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                log.warn("Refresh token missing from request");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No cookies found");
            }

            String refreshToken = Arrays.stream(cookies)
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing"));

            if (!jwtUtil.validateToken(refreshToken)) {
                jwtUtil.clearTokens(response);
                log.warn("Invalid refresh token provided");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }

            Claims claims = jwtUtil.extractClaims(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(
                    claims.get("id", Long.class),
                    claims.getSubject(),
                    claims.get("role", String.class)
            );

            jwtUtil.addAccessTokenToResponse(response, newAccessToken);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);

            log.info("Access token refreshed for user {}", claims.getSubject());

            return ResponseEntity.ok().build();

        } catch (JwtException ex) {
            jwtUtil.clearTokens(response);
            log.error("Token refresh failed", ex);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token processing failed", ex);
        }
    }
}

