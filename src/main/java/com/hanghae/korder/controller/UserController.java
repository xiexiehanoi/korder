package com.hanghae.korder.controller;

import com.hanghae.korder.dto.auth.LoginRequestDto;
import com.hanghae.korder.dto.SignUpRequestDto;
import com.hanghae.korder.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(SignUpRequestDto requestDto){
        userService.signup(requestDto);
        return "redirect:/user/login-page";
    }


}