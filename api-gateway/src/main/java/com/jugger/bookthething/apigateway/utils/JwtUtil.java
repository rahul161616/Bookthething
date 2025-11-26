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

    public Claims parse(String token) {
        return Jwts.parser()
                .decryptWith(getSigningKey())
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)  // standard for HS256
                .getPayload();
    }
      // Extract roles (if present)
    public Object extractRoles(String token) {
        Claims claims = parse(token);
        return claims.get("roles");
    }


    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }
     // Check if expired
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getUsername(String token) {
        return parse(token).getSubject();
    }
}
