package com.hanghae.apigateway.controller;

import com.hanghae.apigateway.dto.SignInRequestDto;
import com.hanghae.apigateway.dto.TokenPair;
import com.hanghae.apigateway.exception.InvalidTokenException;
import com.hanghae.apigateway.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody SignInRequestDto requestDto) {
        return authService.login(requestDto)
                .map(ResponseEntity::ok)
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

    @PostMapping("/event")
    public Mono<ResponseEntity<String>> createEvent(@RequestBody Map<String, Object> eventData, @RequestHeader("Authorization") String token) {
        return authService.createEvent(eventData, token)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof InvalidTokenException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()));
                    }
                    return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                });
    }
}
