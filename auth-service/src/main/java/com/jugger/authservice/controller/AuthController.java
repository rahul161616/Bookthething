package com.jugger.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.authservice.dto.AuthRequest;
import com.jugger.authservice.dto.AuthResponse;
import com.jugger.authservice.service.AuthService;
import com.jugger.authservice.service.RefreshTokenService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest req) {
        AuthResponse response = authService.register(req.getUsername(), req.getPassword());

        if (!response.getMessage().equals("Success")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Long userId = authService.getUserId(req.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(userId, "web-client");
        response = new AuthResponse(response.getAccessToken(), refreshToken, "Success");

        return ResponseEntity.ok(response);
    }

    //Refresh Token Endpoint
   @PostMapping("/refresh")
public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
    if (!refreshTokenService.validateRefreshToken(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, null, "Invalid or expired refresh token"));
    }

    Long userId = refreshTokenService.getUserIdFromToken(refreshToken);
    String newAccessToken = authService.generateAccessToken(userId);

    // Revoke old refresh token and issue a new one
    refreshTokenService.revokeRefreshToken(refreshToken);
    String newRefreshToken = refreshTokenService.createRefreshToken(userId, "web-client");

    AuthResponse response = new AuthResponse(newAccessToken, newRefreshToken, "Success");
    return ResponseEntity.ok(response);
}

  //Login End pointt
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        AuthResponse response = authService.login(req.getUsername(), req.getPassword());

        if (!response.getMessage().equals("Success")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = authService.getUserId(req.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(userId, "web-client");
        response = new AuthResponse(response.getAccessToken(), refreshToken, "Success");

        return ResponseEntity.ok(response);
    }
 }
    
