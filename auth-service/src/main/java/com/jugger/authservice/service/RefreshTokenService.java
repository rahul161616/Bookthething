package com.jugger.authservice.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

// import org.springframework.security.crypto.bcrypt.BCrypt;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jugger.authservice.model.RefreshToken;
import com.jugger.authservice.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Utility to hash refresh tokens using SHA-256
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    public String createRefreshToken(Long userId, String clientInfo) {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setTokenHash(hashToken(rawToken));
        token.setIssuedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        token.setClientInfo(clientInfo);
        token.setRevoked(false);

        refreshTokenRepository.save(token);
        return rawToken; // send this to client ONLY once
    }

    public boolean validateRefreshToken(String rawToken) {
        return refreshTokenRepository.findAll().stream()
                .filter(t -> !t.getRevoked())
                .anyMatch(t -> hashToken(rawToken).equals(t.getTokenHash()));
    }

    public void revokeRefreshToken(String rawToken) {
        refreshTokenRepository.findAll().stream()
                .filter(t -> hashToken(rawToken).equals(t.getTokenHash()))
                .forEach(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }

    public Long getUserIdFromToken(String rawToken) {
        return refreshTokenRepository.findAll().stream()
                .filter(t -> !t.getRevoked())
                .filter(t -> hashToken(rawToken).equals(t.getTokenHash()))
                .map(RefreshToken::getUserId)
                .findFirst()
                .orElse(null);
    }

}
