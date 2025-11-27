package com.jugger.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.userservice.dto.CreateUserProfileRequest;
// import com.jugger.userservice.dto.UserDto;
import com.jugger.userservice.dto.UserProfileResponse;
import com.jugger.userservice.model.UserProfile;
import com.jugger.userservice.service.UserProfileService;

// import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

@Data
@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController
{
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }
    @PostMapping("/create")
    public ResponseEntity<UserProfileResponse> createProfile(@RequestBody CreateUserProfileRequest request) {
        return ResponseEntity.ok(service.createUserProfile(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {
        UserProfileResponse response = service.getUserProfile(userId);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Long userId, @RequestBody UserProfile updatedProfile) {
        UserProfileResponse response = service.updateUserProfile(userId, updatedProfile);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }
}