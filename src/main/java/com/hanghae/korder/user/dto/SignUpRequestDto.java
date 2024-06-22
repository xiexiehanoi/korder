package com.hanghae.korder.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

    private String name;
    private String email;
    private String password;

}
