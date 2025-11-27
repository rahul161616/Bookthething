package com.jugger.userservice.dto;

import lombok.Data;

@Data
public class CreateUserProfileRequest {

    private Long userId;
    private String username;
    private String email;
}
