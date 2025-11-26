package com.jugger.userservice.dto;

import lombok.Data;

@Data
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
}
