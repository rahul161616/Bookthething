package com.jugger.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserProfile {

    @Id
    private Long userId; // same as auth-service user ID

    @Column(nullable=false, unique =true)
    private String userame;
    @Column(nullable=false, unique =true)
    private String email;
    private String phone;
    private String address;
}
