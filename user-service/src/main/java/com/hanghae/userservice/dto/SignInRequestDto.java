package com.hanghae.userservice.dto;

import lombok.Data;

@Data
public class SignInRequestDto {
    String email;
    String password;
}
