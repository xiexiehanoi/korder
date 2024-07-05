package com.hanghae.apigateway.service;

import com.hanghae.apigateway.dto.TokenPair;
import com.hanghae.apigateway.util.JwtUtil;
import com.hanghae.apigateway.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public Mono<TokenPair> refreshToken(String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromToken(refreshToken);

            return redisUtil.getValue(username)
                    .flatMap(storedRefreshToken -> {
                        if (storedRefreshToken.equals(refreshToken)) {
                            String newAccessToken = jwtUtil.createAccessToken(username);
                            String newRefreshToken = jwtUtil.createRefreshToken(username);

                            return redisUtil.setValue(username, newRefreshToken, Duration.ofDays(7))
                                    .thenReturn(new TokenPair(newAccessToken, newRefreshToken));
                        } else {
                            return Mono.error(new RuntimeException("Invalid refresh token"));
                        }
                    });
        }
        return Mono.error(new RuntimeException("Invalid refresh token"));
    }

    public Mono<Void> logout(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        return redisUtil.deleteValue(username).then();
    }
}