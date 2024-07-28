package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.dto.UserDto;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserServiceImpl userService;
    private static final String ERROR_ATTRIBUTE = "error";

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("signuprequest", new UserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("signupUser") UserDto userDto, Model model, HttpSession session) {
        session.setAttribute("userDto", userDto);
        boolean result = userService.addUser(userDto);
        if (!result && userService.getFlag() == 1) {
            model.addAttribute(ERROR_ATTRIBUTE, "Username or email already exists");
            return "/signup";
        } else if (!result && userService.getFlag() == 0) {
            return "/signup";
        } else {
            model.addAttribute("userDto", userDto);
            return "otp-login";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = ERROR_ATTRIBUTE, required = false) String error,
                                Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (error != null) {
                model.addAttribute(ERROR_ATTRIBUTE, "Invalid Username or password");
            }
            return "login";
        } else {
            return "redirect:/";
        }
    }


    @PostMapping("/verify")
    public String verifyUser(@RequestParam(name = "otp") String otp, Model model, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("userDto");
        userDto.setOtp(otp);
        boolean result = userService.verifyAccount(userDto);
        if (result) {
            return "redirect:/login";
        } else {
            model.addAttribute(ERROR_ATTRIBUTE, "otp incorrect");
            return "otp-login";
        }
    }
}