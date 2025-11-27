package com.jugger.userservice.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String address;  
}
