package com.hanghae.userservice.service;

import com.hanghae.userservice.dto.SignInRequestDto;
import com.hanghae.userservice.dto.SignInResponseDto;
import com.hanghae.userservice.dto.SignUpRequestDto;

public interface UserService {

    void signUp(SignUpRequestDto requestDto);

    SignInResponseDto signIn(SignInRequestDto requestDto);

    void getUserById(Long id);
}
