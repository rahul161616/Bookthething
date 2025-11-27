package com.jugger.bookthething.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:a-string-secret-at-least-256-bits-long}")
    private String SECRET_KEY;
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
    // Parse and validate JWT
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
     public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            System.out.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration() != null && claims.getExpiration().before(new Date());
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public Object getRoles(String token) {
        return parseClaims(token).get("roles");
    }  
}
