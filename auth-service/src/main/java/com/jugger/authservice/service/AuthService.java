package com.jugger.authservice.service;

import com.jugger.authservice.model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jugger.authservice.config.JwtUtil;
import com.jugger.authservice.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository repo, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }   
    public String register(String username,String password){
        if(repo.findByUsername(username)!=null){
            return "Username already exists";
        }else{
            User usr = new User();
            usr.setUsername(username);
            usr.setPassword(passwordEncoder.encode(password));
            repo.save(usr);
            return "User registered successfully";
        }
    }
    public String login(String username,String password){
        User usr = repo.findByUsername(username);
        if(usr==null){
            return "User not found";
        }else{
            if(passwordEncoder.matches(password, usr.getPassword())){
                return jwtUtil.generateToken(username);
            }else{
                return "Invalid credentials";
            }
        }
    }
    
    
}
