package com.jugger.userservice.service;

import org.springframework.stereotype.Service;

import com.jugger.userservice.model.UserProfile;
import com.jugger.userservice.repository.ProfileRepository;
import com.jugger.userservice.dto.UserDto;
import com.jugger.userservice.mapper.UserMapper;
// import com.jugger.userservice.model.UserProfile;

@Service
public class UserProfileService {

    private final ProfileRepository repo;

    public UserProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

   public UserDto getUserById(Long id) {
        return repo.findById(id)
                .map(UserMapper::toDto)
                .orElse(null);
    }   

    public UserDto getUserByUsername(String username) {
        return repo.findByUserame(username)
                .map(UserMapper::toDto)
                .orElse(null);
    }
    
    public boolean createProfile(Long userId, String username, String email) {
        try {
            // Check if profile already exists
            if (repo.findById(userId).isPresent()) {
                return false; // Profile already exists
            }
            
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setUserame(username); // Note: using 'userame' due to typo in entity
            profile.setEmail(email);
            // Set default values
            profile.setPhone("");
            profile.setAddress("");
            
            repo.save(profile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
