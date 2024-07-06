package com.hanghae.userservice.controller;

import com.hanghae.userservice.dto.*;
import com.hanghae.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/signIn")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto requestDto) {
        SignInResponseDto responseDto = userService.signIn(requestDto);
        return ResponseEntity.ok(responseDto);
    }

//    @GetMapping("/mypage")
//    public ResponseEntity<MyPageDto> myPage(@RequestHeader("Authorization") String token) {
//        String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
//        MyPageDto myPageDto = userService.getUser(email);
//        return ResponseEntity.ok(myPageDto);
//    }
}