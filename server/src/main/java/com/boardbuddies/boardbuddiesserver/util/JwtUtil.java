package com.boardbuddies.boardbuddiesserver.util;

import com.boardbuddies.boardbuddiesserver.config.JwtProperties;
import com.boardbuddies.boardbuddiesserver.exception.JwtTokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * SecretKey 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 액세스 토큰 생성
     * 
     * @param userId 사용자 ID
     * @return 액세스 토큰
     */
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     * 
     * @param userId 사용자 ID
     * @return 리프레시 토큰
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 임시 액세스 토큰 생성 (회원가입 중)
     * 
     * @param socialId 소셜 ID
     * @param provider 소셜 제공자
     * @param email    이메일
     * @return 임시 액세스 토큰 (30분 유효)
     */
    public String generateTempAccessToken(String socialId, String provider, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1800000); // 30분

        return Jwts.builder()
                .subject("TEMP_USER")
                .claim("type", "temp")
                .claim("socialId", socialId)
                .claim("provider", provider)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰에서 소셜 정보 추출
     */
    public Claims getClaimsFromToken(String token) {
        return parseToken(token);
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰 만료 시간 조회
     */
    public Long getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime();
    }

    /**
     * 토큰 파싱
     * 
     * @param token JWT 토큰
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.", e);
            throw new JwtTokenExpiredException("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.", e);
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰입니다.", e);
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        } catch (SecurityException e) {
            log.error("JWT 서명 검증에 실패했습니다.", e);
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다.", e);
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    /**
     * 토큰 검증
     * 
     * @param token JWT 토큰
     * @return 유효한 토큰이면 true
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtTokenExpiredException e) {
            // 만료 토큰은 상위에서 처리할 수 있도록 그대로 전달
            throw e;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 토큰 타입 확인
     * 
     * @param token JWT 토큰
     * @return 토큰 타입 (access, refresh, temp)
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }
}
