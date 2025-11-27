package com.jugger.bookingorchestrator.dto;

import lombok.Data;

@Data
public class UserProfileResponse {

     private Long userId;
    private String username;
    private String email;
    // Optional: add more fields if your User Service has more info
    
}
