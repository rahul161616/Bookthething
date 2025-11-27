package com.jugger.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // same as auth-service user ID

    @Column(nullable = false)
    private Long userId;  // FK to AuthService User ID
    @Column(nullable=false)
    private String username;
    @Column(nullable=false)
    private String email;
    private String phone;
    private String address;
    
}
