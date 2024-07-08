package com.hanghae.userservice.service.impl;

import com.hanghae.userservice.dto.SignInRequestDto;
import com.hanghae.userservice.dto.SignInResponseDto;
import com.hanghae.userservice.repository.UserRepository;
import com.hanghae.userservice.dto.SignUpRequestDto;
import com.hanghae.userservice.entity.UserEntity;
import com.hanghae.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(SignUpRequestDto requestDto) {
        if (requestDto.getPassword() == null) {
            throw new IllegalStateException("requestDto.getPassword() is null");
        }
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .points(requestDto.getPoints())
                .build();
        userRepository.save(userEntity);
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("등록된 사용자가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return new SignInResponseDto(user.getId(), user.getEmail(), user.getName());
    }

    @Override
    public void getUserById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("없는 유저입니다."));
    }
}