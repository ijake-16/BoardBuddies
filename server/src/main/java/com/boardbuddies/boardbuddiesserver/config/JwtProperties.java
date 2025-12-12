package com.boardbuddies.boardbuddiesserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 프로퍼티
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명 비밀키
     */
    private String secret;

    /**
     * 액세스 토큰 만료 시간 (밀리초) - 기본값 1시간
     */
    private Long accessTokenExpiration = 3600000L;

    /**
     * 리프레시 토큰 만료 시간 (밀리초) - 기본값 14일
     */
    private Long refreshTokenExpiration = 1209600000L;
}
