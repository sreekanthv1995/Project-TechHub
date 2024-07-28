package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.dto.UserDto;
import com.tech_hub.techhub.entity.PasswordResetToken;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.repository.TokenRepository;
import com.tech_hub.techhub.repository.UserRepository;
import com.tech_hub.techhub.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
public class ResetPasswordController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    TokenService tokenService;


    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }


    @PostMapping("/forgot-password")
    public String forgotPasswordProcess(@ModelAttribute UserDto userDto) {
        String output = "";
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            output = tokenService.sentEmail(user);
        }
        if (output.equals("success")) {
            return "redirect:/forgot-password?success";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/resetPassword/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        PasswordResetToken reset = tokenRepository.findByToken(token);
        if (reset != null && tokenService.hasExpired(reset.getExpiryDateTime())) {
            model.addAttribute("email", reset.getUser().getEmail());
            return "resetPassword";
        }
        return "redirect:/forgot-password?error";
    }

    @PostMapping("/resetPassword")
    public String passwordResetProcess(@ModelAttribute UserDto userDto,Model model) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userRepository.save(user);
        }
        model.addAttribute("message","Your password has been changed. You can now log in.");
        return "login";
    }
}

