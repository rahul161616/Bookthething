package main.java.com.jugger.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserProfile {

    @Id
    private Long userId; // same as auth-service user ID

    private String fullName;
    private String email;
    private String phone;
    private String address;
}
