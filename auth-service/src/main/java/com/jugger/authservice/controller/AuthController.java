package com.jugger.authservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.authservice.dto.AuthRequest;
import com.jugger.authservice.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public String register(@RequestBody AuthRequest authRequest){
        return authService.register(authRequest.getUsername(), authRequest.getPassword());
    }
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        String token = authService.login(authRequest.getUsername(), authRequest.getPassword());
        return token == null? "Invalid credentials": token;
    }
    
}
