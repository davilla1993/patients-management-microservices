package com.follysitou.auth_service.service;

import com.follysitou.auth_service.dto.LoginRequestDto;
import com.follysitou.auth_service.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public Optional<String> authenticate(LoginRequestDto loginRequestDto) {

        return userService.findByEmail(loginRequestDto.getEmail())
                  .filter(u -> passwordEncoder.matches(loginRequestDto.getPassword(), u.getPassword()))
                  .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }
}
