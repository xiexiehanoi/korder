package com.hanghae.korder.auth.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidityInMilliseconds;

    private final RedisUtil redisUtil;

    // Refresh Token을 Redis에 저장
    public void storeRefreshToken(String email, String refreshToken) {
        redisUtil.set(email, refreshToken, (int) (refreshTokenValidityInMilliseconds / 1000 / 60)); // 수정:
    }

    // Refresh Token 검증
    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedToken = (String) redisUtil.get(email);
        return refreshToken.equals(storedToken);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String email) {
        redisUtil.delete(email);
    }

    // Access Token 블랙리스트에 추가
    public void blacklistAccessToken(String token) {
        long expiration = getExpirationFromToken(token);
        redisUtil.setBlackList(token, "blacklisted", (int) (expiration / 1000 / 60)); // 수정:
    }

    // 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return redisUtil.hasKeyBlackList(token);
    }

    // 토큰에서 만료 시간 추출
    private long getExpirationFromToken(String token) {
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

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
//            cookie.setHttpOnly(true);
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

    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Refresh-Token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}