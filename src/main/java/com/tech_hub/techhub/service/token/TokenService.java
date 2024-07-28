package com.tech_hub.techhub.service.token;

import com.tech_hub.techhub.entity.UserEntity;

import java.time.LocalDateTime;

public interface TokenService {


    String forgotPassword(String email);
    String setPassword(String email, String newPassword);
    String sentEmail(UserEntity user);
    boolean hasExpired(LocalDateTime expiryDateTime);
}
