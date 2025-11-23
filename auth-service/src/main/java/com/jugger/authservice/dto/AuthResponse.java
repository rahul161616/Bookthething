package com.jugger.authservice.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
    }

    public static AuthResponse success(String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken, "Success");
    }

    public static AuthResponse fail(String message) {
        return new AuthResponse(null, null, message);
    }
    
    public boolean isSuccess() {
        return accessToken != null;
    }
}
