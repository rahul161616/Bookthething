package com.jugger.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jugger.authservice.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserIdAndRevokedFalse(Long userId);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
}
