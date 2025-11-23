package com.jugger.authservice.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    // getters & setters
}
