package com.hanghae.korder.user.controller;

import com.hanghae.korder.auth.jwt.JwtUtil;
import com.hanghae.korder.user.dto.MyPageDto;
import com.hanghae.korder.auth.dto.LoginRequestDto;
import com.hanghae.korder.user.dto.SignUpRequestDto;
import com.hanghae.korder.auth.dto.LoginResponseDto;
import com.hanghae.korder.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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
    public String signup(SignUpRequestDto requestDto) {
        userService.signup(requestDto);
        return "redirect:/user/login-page";
    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody LoginRequestDto requestDto, HttpServletResponse res) {
        try {
            LoginResponseDto responseDto = userService.login(requestDto, res);
            jwtUtil.addAccessTokenToCookie(responseDto.getAccessToken(), res);
            jwtUtil.addRefreshTokenToCookie(responseDto.getRefreshToken(), res);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/mypage")
    @ResponseBody
    public ResponseEntity<?> myPage() {
        try {
            MyPageDto user = userService.getUser();
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("접근이 거부되었습니다: " + e.getMessage());
        }
    }
}
