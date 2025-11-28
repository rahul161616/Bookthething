package com.jugger.authservice.service;

import com.jugger.authservice.model.User;
// import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jugger.authservice.config.JwtUtil;
import com.jugger.authservice.dto.AuthResponse;
import com.jugger.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileService profileService;
      
    //REGISTER METHODS
    public AuthResponse register(String username, String password, String email, String roleStr) {
        if (repo.findByUsername(username) != null)
            return AuthResponse.fail("Username already exists");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        
        // Set role based on request, default to USER
        com.jugger.authservice.model.Role role = com.jugger.authservice.model.Role.USER;
        System.out.println(">>> Register called with role: " + roleStr);
        if (roleStr != null && !roleStr.trim().isEmpty()) {
            try {
                role = com.jugger.authservice.model.Role.valueOf(roleStr.toUpperCase());
                System.out.println(">>> Role set to: " + role);
            } catch (IllegalArgumentException e) {
                System.out.println(">>> Invalid role provided: " + roleStr + ", defaulting to USER");
                // Invalid role, stick with USER
            }
        }
        user.setRole(role);
        repo.save(user);
        
        // Create user profile automatically
         // Create user profile
        String emailToUse = email != null ? email : username + "@example.com";
        profileService.createUserProfile(user.getId(), username, emailToUse);
        
        String accessToken = jwtUtil.generateToken(user);
        return AuthResponse.success(accessToken, null);
    }
    
    // Keep backward compatibility
    public AuthResponse register(String username, String password) {
        return register(username, password, null, null);
    }
    
    //LOGIN METHODS - return access token
    public AuthResponse login(String username, String password) {
        User usr = repo.findByUsername(username);
        if (usr == null) return AuthResponse.fail("User not found");
        if (!passwordEncoder.matches(password, usr.getPassword())) {
            return AuthResponse.fail("Invalid credentials");
        }
        String accessToken = jwtUtil.generateToken(usr);
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
    return jwtUtil.generateToken(user);
}

}

    
    