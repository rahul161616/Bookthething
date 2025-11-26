package com.jugger.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProfileService {
    
    private final RestTemplate restTemplate;
    
    @Value("${user-service.url:http://localhost:8083}")
    private String userServiceUrl;
    
    public ProfileService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public boolean createUserProfile(Long userId, String username, String email) {
        try {
            CreateProfileRequest request = new CreateProfileRequest();
            request.setUserId(userId);
            request.setUsername(username);
            request.setEmail(email);
            
            String url = userServiceUrl + "/api/v1/profile/create";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // Log error but don't fail the registration if profile creation fails
            System.err.println("Failed to create user profile: " + e.getMessage());
            return false;
        }
    }
    
    // Inner class for the request
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