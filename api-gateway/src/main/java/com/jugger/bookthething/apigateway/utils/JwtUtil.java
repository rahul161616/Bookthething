package com.jugger.bookthething.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    @Value("${jwt.secret:a-string-secret-at-least-256-bits-long}")
    private String SECRET_KEY;
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)  // standard for HS256
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }

    public String getUserId(String token) {
        return extractAllClaims(token).getSubject();
    }
}
