package com.hanghae.korder.controller.auth;

import com.hanghae.korder.dto.auth.LoginRequestDto;
import com.hanghae.korder.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
