package com.hanghae.userservice.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    String name;
    String email;
    String password;
    int points;
}
