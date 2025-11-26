package main.java.com.jugger.userservice.controller;

import main.java.com.jugger.userservice.model.UserProfile;
import main.java.com.jugger.userservice.service.UserProfileService;

@RestController
@RequestMapping("/api/v1/auth/profile")
public class UserProfileController
{
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping
    public UserProfile getProfile(@RequestHeader("X-User-Id") Long userId) {
        return service.getProfile(userId);
    }

    @PostMapping
    public UserProfile updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserProfile profile
    ) {
        return service.createOrUpdate(userId, profile);
    }
}
