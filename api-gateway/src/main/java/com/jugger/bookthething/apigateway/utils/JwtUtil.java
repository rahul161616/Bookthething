package com.jugger.bookthething.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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
    
    // in com.jugger.bookthething.apigateway.utils.JwtUtil
    public String getRoleString(String token) {
    Claims claims = parseClaims(token);
    // First try "role" (singular) as used by auth service
    Object role = claims.get("role");
    if (role != null) {
        return role.toString();
    }
    // Fallback to "roles" (plural) for compatibility
    Object roles = claims.get("roles");
    if (roles == null) return null;
    // if roles is a single string
    if (roles instanceof String) return (String) roles;
    // if roles is collection (List<String>) -> join first
    if (roles instanceof java.util.Collection) {
        Collection<?> c = (Collection<?>) roles;
        return c.stream().findFirst().map(Object::toString).orElse(null);
       }
    return roles.toString();
    }

}
