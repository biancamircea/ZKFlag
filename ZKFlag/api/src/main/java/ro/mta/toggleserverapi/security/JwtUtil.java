package ro.mta.toggleserverapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured properly.");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public String generateToken(Long id, String email, String role) {
        return generateAccessToken(id, email, role);
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

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expired: " + token);
            throw new AuthenticationCredentialsNotFoundException("JWT expired", e);
        } catch (JwtException e) {
            System.err.println("Invalid JWT: " + token);
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT", e);
        }
    }

    public String generateAccessToken(Long id, String email, String role) {
        return buildToken(id, email, role, accessTokenExpirationMs);
    }

    public String generateRefreshToken(Long id, String email, String role) {
        return buildToken(id, email, role, refreshTokenExpirationMs);
    }

    private String buildToken(Long id, String email, String role, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public void addAccessTokenToResponse(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenExpirationMs / 1000)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void addRefreshTokenToResponse(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(refreshTokenExpirationMs / 1000)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearTokens(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .maxAge(0)
                .path("/")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/auth/refresh")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenExpirationMs / 1000)
                .sameSite("None")
                .build();
    }
}