package ro.mta.toggleserverapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import ro.mta.toggleserverapi.enums.UserRoleType;

@Getter
@Setter
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured properly.");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Long id, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT is incorrect or malformed", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractId(String token) {
        return extractClaims(token).get("id", String.class);
    }

    public boolean validateToken(String token) {
        try {

            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                System.err.println("JWT is expired: " + token);
                throw new AuthenticationCredentialsNotFoundException("JWT is expired");
            }

            return true;

        } catch (ExpiredJwtException e) {
            System.err.println("JWT is expired: " + token);
            throw new AuthenticationCredentialsNotFoundException("JWT is expired", e);
        } catch (JwtException e) {
            System.err.println("JWT is incorrect or malformed: " + token);
            throw new AuthenticationCredentialsNotFoundException("JWT is incorrect or malformed", e);
        } catch (Exception e) {
            System.err.println("JWT is invalid: " + token);
            throw new AuthenticationCredentialsNotFoundException("JWT is invalid", e);
        }
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Key getKey() {
        return key;
    }

    public void addTokenToResponse(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 3); // 3 ore

        response.addCookie(cookie);
    }
}
