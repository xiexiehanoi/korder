package com.hanghae.korder.auth.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidityInMilliseconds;

    // Access Token 생성
    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT를 쿠키에 추가
    public void addJwtToCookie(String token, HttpServletResponse res, String cookieName, long maxAgeInMillis) {
        try {
            String encodedToken = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
            Cookie cookie = new Cookie(cookieName, encodedToken);
            cookie.setPath("/");
            cookie.setMaxAge((int) maxAgeInMillis / 1000);
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Access Token을 쿠키에 추가
    public void addAccessTokenToCookie(String token, HttpServletResponse res) {
        addJwtToCookie(token, res, "Authorization", accessTokenValidityInMilliseconds);
    }

    // Refresh Token을 쿠키에 추가
    public void addRefreshTokenToCookie(String token, HttpServletResponse res) {
        addJwtToCookie(token, res, "Refresh-Token", refreshTokenValidityInMilliseconds);
    }
}