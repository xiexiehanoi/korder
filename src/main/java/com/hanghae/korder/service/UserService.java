package com.hanghae.korder.service;

import com.hanghae.korder.dto.auth.LoginRequestDto;
import com.hanghae.korder.dto.SignUpRequestDto;
import com.hanghae.korder.dto.auth.LoginResponseDto;
import com.hanghae.korder.entity.UserEntity;
import com.hanghae.korder.jwt.JwtUtil;
import com.hanghae.korder.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void signup(SignUpRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // email 중복확인
        Optional<UserEntity> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 등록
        UserEntity user = new UserEntity(requestDto.getName(), password, email, 0);
        userRepository.save(user);
    }

    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse res) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 사용자 확인
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String accessToken = jwtUtil.createAccessToken(user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken();

        // JWT를 쿠키에 추가
        jwtUtil.addAccessTokenToCookie(accessToken, res);
        jwtUtil.addRefreshTokenToCookie(refreshToken, res);

        return new LoginResponseDto(accessToken, refreshToken);

    }
}