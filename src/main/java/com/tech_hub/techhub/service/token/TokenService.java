package com.tech_hub.techhub.service.token;

import com.tech_hub.techhub.entity.UserEntity;

import java.time.LocalDateTime;

public interface TokenService {

    String sentEmail(UserEntity user);
    boolean hasExpired(LocalDateTime expiryDateTime);
}
