package com.hanghae.apigateway.dto;

import lombok.Data;

@Data
public class SignInResponseDto {
    private Long id;
    private String email;
    private String name;
}
