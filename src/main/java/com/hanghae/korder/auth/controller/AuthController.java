package com.hanghae.korder.auth.controller;

import com.hanghae.korder.auth.dto.LoginRequestDto;
import com.hanghae.korder.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(LoginRequestDto requestDto, HttpServletResponse res) {
        try {
            userService.login(requestDto, res);
            return "Login successful";
        } catch (Exception e) {
            return "Login failed: " + e.getMessage();
        }
    }
}
