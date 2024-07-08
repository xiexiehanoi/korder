package com.hanghae.apigateway.filter;

import com.hanghae.apigateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // /auth/login 경로는 필터링하지 않음
        if (path.equals("/auth/login")) {
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        logger.debug("Extracted token: {}", token);

        if (token != null) {
            boolean isValid = jwtUtil.validateToken(token);
            logger.debug("Token validation result: {}", isValid);

            if (isValid) {
                String username = jwtUtil.getUsernameFromToken(token);
                logger.debug("Username from token: {}", username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                logger.debug("Created authentication object: {}", authentication);

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                        .doOnSuccess(v -> logger.debug("Filter chain completed successfully"))
                        .doOnError(e -> logger.error("Error in filter chain", e));
            }
        }

        logger.debug("No token or invalid token format");
        return onError(exchange, "Invalid token or missing token", HttpStatus.UNAUTHORIZED);
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        return Mono.fromRunnable(() -> {
            exchange.getResponse().setStatusCode(httpStatus);
        });
    }
}
