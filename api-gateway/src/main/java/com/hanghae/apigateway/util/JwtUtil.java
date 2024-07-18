package com.hanghae.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accesstoken.expiration}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refreshtoken.expiration}")
    private long refreshTokenExpirationTime;

    private Key key;


    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("The secret key must be at least 256 bits.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String username, Long userId) {
        return createToken(username, userId,accessTokenExpirationTime);
    }

    public String createRefreshToken(String username, Long userId) {
        return createToken(username, userId,refreshTokenExpirationTime);
    }

    private String createToken(String username, Long userId,long expirationTime) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId",userId);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            logger.debug("Token validation failed", e);
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return Long.parseLong(claims.get("userId").toString());
    }
}
