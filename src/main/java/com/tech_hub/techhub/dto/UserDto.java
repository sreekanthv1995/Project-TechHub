package com.tech_hub.techhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

        private Long id;
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private Long phoneNumber;
        private String password;
        private String confirmPassword;
        private String roles;
        private String otp;
        private boolean verified;

    }

