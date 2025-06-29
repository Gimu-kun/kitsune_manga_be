package com.kitsune.kitsune.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encoderPassword(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }

    public boolean checkPassword(String hashedPassword, String rawPassword){
        return passwordEncoder.matches(rawPassword,hashedPassword);
    }
}
