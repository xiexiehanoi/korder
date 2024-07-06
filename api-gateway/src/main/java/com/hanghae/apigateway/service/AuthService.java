package com.hanghae.apigateway.service;

import com.hanghae.apigateway.dto.SignInRequestDto;
import com.hanghae.apigateway.dto.SignInResponseDto;
import com.hanghae.apigateway.dto.TokenPair;
import com.hanghae.apigateway.exception.InvalidTokenException;
import com.hanghae.apigateway.util.JwtUtil;
import com.hanghae.apigateway.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final WebClient.Builder webClientBuilder;

    public Mono<Map<String, Object>> login(SignInRequestDto requestDto) {
        return webClientBuilder.build().post()
                .uri("http://user-service/user/signIn")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(SignInResponseDto.class)
                .flatMap(userDto -> {
                    String accessToken = jwtUtil.createAccessToken(userDto.getEmail());
                    String refreshToken = jwtUtil.createRefreshToken(userDto.getEmail());

                    return redisUtil.setValue(userDto.getEmail(), refreshToken, Duration.ofDays(7))
                            .thenReturn(Map.of(
                                    "user", userDto,
                                    "accessToken", accessToken,
                                    "refreshToken", refreshToken
                            ));
                });
    }

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

    public Mono<String> createEvent(Map<String, Object> eventData, String token) {
        return redisUtil.getValue(token)
                .flatMap(storedToken -> {
                    if (storedToken != null && storedToken.equals(token)) {
                        String userId = jwtUtil.getUsernameFromToken(token);
                        return webClientBuilder.build().post()
                                .uri("http://event-service/event")
                                .header("X-User-id", userId)
                                .bodyValue(eventData)
                                .retrieve()
                                .bodyToMono(String.class);
                    } else {
                        return Mono.error(new InvalidTokenException("Invalid token"));
                    }
                });
    }
}