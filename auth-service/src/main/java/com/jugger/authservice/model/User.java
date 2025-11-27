package com.jugger.authservice.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
@Table(name = "users")
public class User {

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Boolean isVendor = false;
    private Boolean isAdmin = false;
}
