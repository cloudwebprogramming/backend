package com.example.demo.members.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Encode password
     */
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
    
    /**
     * Verify password
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
