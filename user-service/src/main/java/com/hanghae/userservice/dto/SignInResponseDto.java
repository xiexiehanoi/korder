package com.hanghae.userservice.dto;

import lombok.Data;

@Data
public class SignInResponseDto {

    Long id;
    String email;
    String name;

    public SignInResponseDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
