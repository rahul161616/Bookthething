package com.jugger.authservice.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class RefreshToken {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String tokenHash;

    private Instant issuedAt;

    private Instant expiresAt;

    private Boolean revoked = false;

    private String clientInfo;

    // getters and setters
    
}
