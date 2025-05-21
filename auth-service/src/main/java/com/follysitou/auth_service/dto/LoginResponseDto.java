package com.follysitou.auth_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {

    private final String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
