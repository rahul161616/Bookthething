package main.java.com.jugger.userservice.service;

import main.java.com.jugger.userservice.model.UserProfile;

@Service
public class UserProfileService {

    private final ProfileRepository repo;

    public UserProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

    public UserProfile createOrUpdate(Long userId, UserProfile profile) {
        profile.setUserId(userId);
        return repo.save(profile);
    }

    public UserProfile getProfile(Long userId) {
        return repo.findById(userId).orElse(null);
    }
    
}
