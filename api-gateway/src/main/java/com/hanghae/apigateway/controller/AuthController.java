package com.hanghae.apigateway.controller;

import com.hanghae.apigateway.dto.SignInRequestDto;
import com.hanghae.apigateway.dto.SignInResponseDto;
import com.hanghae.apigateway.dto.TokenPair;
import com.hanghae.apigateway.service.AuthService;
import com.hanghae.apigateway.util.JwtUtil;
import com.hanghae.apigateway.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final WebClient.Builder webClientBuilder;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody SignInRequestDto requestDto) {
        return webClientBuilder.build().post()
                .uri("http://user-service/user/signIn")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(SignInResponseDto.class)
                .flatMap(userDto -> {
                    String accessToken = jwtUtil.createAccessToken(userDto.getEmail());
                    String refreshToken = jwtUtil.createRefreshToken(userDto.getEmail());

                    return redisUtil.setValue(userDto.getEmail(), refreshToken, Duration.ofDays(7))
                            .thenReturn(ResponseEntity.ok()
                                    .header("Authorization", "Bearer " + accessToken)
                                    .header("Refresh-Token", refreshToken)
                                    .body(Map.of(
                                            "user", userDto,
                                            "accessToken", accessToken,
                                            "refreshToken", refreshToken
                                    )));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"))));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenPair>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return authService.refreshToken(refreshToken)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestHeader("Authorization") String token) {
        return authService.logout(token)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
