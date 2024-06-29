package com.hanghae.korder.auth.controller;

import com.hanghae.korder.auth.dto.LoginRequestDto;
import com.hanghae.korder.auth.jwt.JwtUtil;
import com.hanghae.korder.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(LoginRequestDto requestDto, HttpServletResponse res) {
        try {
            userService.login(requestDto, res);
            return "Login successful";
        } catch (Exception e) {
            return "Login failed: " + e.getMessage();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        String token = header.substring(7);
        try {
            userService.logout(token);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromRequest(request);
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        if (!jwtUtil.validateRefreshToken(email, refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token mismatch");
        }

        String newAccessToken = jwtUtil.createAccessToken(email);
        String newRefreshToken = jwtUtil.createRefreshToken();
        jwtUtil.storeRefreshToken(email, newRefreshToken);
        jwtUtil.addAccessTokenToCookie(newAccessToken, response);
        jwtUtil.addRefreshTokenToCookie(newRefreshToken, response);

        return ResponseEntity.ok("Token refreshed successfully");
    }
}
