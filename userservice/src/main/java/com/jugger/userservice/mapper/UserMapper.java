package com.jugger.userservice.mapper;

import com.jugger.userservice.dto.UserDto;
import com.jugger.userservice.model.UserProfile;

public class UserMapper {

    public static UserDto toDto(UserProfile userProfile) {
        UserDto dto = new UserDto();
        dto.setId(userProfile.getUserId());
        dto.setUsername(userProfile.getUserame());
        dto.setEmail(userProfile.getEmail());
        return dto;
    }
    
}
