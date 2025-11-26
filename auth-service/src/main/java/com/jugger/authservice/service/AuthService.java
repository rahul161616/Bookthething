package com.jugger.authservice.service;

import com.jugger.authservice.model.User;
// import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jugger.authservice.config.JwtUtil;
import com.jugger.authservice.dto.AuthResponse;
import com.jugger.authservice.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileService profileService;
    
    public AuthService(UserRepository repo, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder, ProfileService profileService) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }   
    //REGISTER METHODS
    public AuthResponse register(String username, String password) {
        if (repo.findByUsername(username) != null)
            return AuthResponse.fail("Username already exists");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        repo.save(user);
        
        // Create user profile automatically
        Long userId = user.getId();
        String defaultEmail = username + "@example.com"; // You can modify this to accept email in registration
        profileService.createUserProfile(userId, username, defaultEmail);
        
        String accessToken = jwtUtil.generateToken(username, userId);
        return AuthResponse.success(accessToken, null);
    }
    
    //LOGIN METHODS - return access token
    public AuthResponse login(String username, String password) {
        User usr = repo.findByUsername(username);
        if (usr == null) return AuthResponse.fail("User not found");
        if (!passwordEncoder.matches(password, usr.getPassword())) {
            return AuthResponse.fail("Invalid credentials");
        }
        String accessToken = jwtUtil.generateToken(username, usr.getId());
        return AuthResponse.success(accessToken, null);
    }
    
    // AuthService.java
    public Long getUserId(String username) {
     User user = repo.findByUsername(username);
        return user != null ? user.getId() : null;
    }

    public String generateAccessToken(Long userId) {
    User user = repo.findById(userId).orElse(null);
    if (user == null) return null;
    return jwtUtil.generateToken(user.getUsername());
}

}

    
    