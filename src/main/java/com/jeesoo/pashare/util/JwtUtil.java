package com.jeesoo.pashare.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 보안을 위해 실제 배포 시에는 환경 변수나 설정 파일로 관리해야 합니다
    private static final String SECRET_KEY = "PaShare_Turnstile_JWT_Secret_Key_Must_Be_At_Least_32_Bytes_Long";
    
    // 30분 유효 (밀리초 단위)
    private static final long JWT_EXPIRATION = 30 * 60 * 1000;
    
    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // JWT 토큰 생성
    public String generateToken() {
        return Jwts.builder()
                .setSubject("captcha_verified")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // JWT 토큰에서 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    // 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // 토큰에서 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    // 토큰에서 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
} 