package com.jugger.userservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jugger.userservice.model.UserProfile;
import com.jugger.userservice.repository.ProfileRepository;
import com.jugger.userservice.dto.CreateUserProfileRequest;
import com.jugger.userservice.dto.UserProfileResponse;

@Service
public class UserProfileService {

    private final ProfileRepository repo;

    public UserProfileService(ProfileRepository repo) {
        this.repo = repo;
    }
     public UserProfileResponse createUserProfile(CreateUserProfileRequest request) {
        UserProfile profile = new UserProfile();
        profile.setUserId(request.getUserId());
        profile.setUsername(request.getUsername());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        repo.save(profile);

        return toResponse(profile);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        Optional<UserProfile> profile = repo.findByUserId(userId);
        return profile.map(this::toResponse).orElse(null);
    } 

    public UserProfileResponse updateUserProfile(Long userId, UserProfile updatedProfile) {
        return repo.findByUserId(userId).map(profile -> {
            profile.setUsername(updatedProfile.getUsername());
            profile.setEmail(updatedProfile.getEmail());
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            repo.save(profile);
            return toResponse(profile);
        }).orElse(null);
    }
      private UserProfileResponse toResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setUsername(profile.getUsername());
        response.setEmail(profile.getEmail());
        response.setPhone(profile.getPhone());
        response.setAddress(profile.getAddress());
        return response;
    }
}
