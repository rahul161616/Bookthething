package com.jugger.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.userservice.dto.UserDto;
import com.jugger.userservice.service.UserProfileService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController
{
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }
// Profile endpoint: GET /api/v1/users/me
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {

        String username = request.getHeader("X-Username");

        if (username == null) {
            return ResponseEntity.status(401).body("Missing X-Username header (gateway misconfigured)");
        }

        UserDto dto = service.getUserByUsername(username);

        if (dto == null) return ResponseEntity.status(404).body("User not found");

        return ResponseEntity.ok(dto);
    }

    // GET /api/v1/users/id/{id}
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        UserDto dto = service.getUserById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
    
    // POST /api/v1/profile/create - Internal endpoint for profile creation
    @PostMapping("/create")
    public ResponseEntity<?> createProfile(@RequestBody CreateProfileRequest request) {
        try {
            boolean created = service.createProfile(
                request.getUserId(), 
                request.getUsername(), 
                request.getEmail()
            );
            
            if (created) {
                return ResponseEntity.ok("Profile created successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to create profile");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // Inner class for create profile request
    public static class CreateProfileRequest {
        private Long userId;
        private String username;
        private String email;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}