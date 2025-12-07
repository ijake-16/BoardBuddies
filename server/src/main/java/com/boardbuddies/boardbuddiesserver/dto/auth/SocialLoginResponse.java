package com.boardbuddies.boardbuddiesserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인 응답 데이터
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    
    /**
     * 인증 타입 (Signup / Login)
     */
    private AuthType type;
    
    /**
     * 액세스 토큰
     */
    private String accessToken;
    
    /**
     * 리프레시 토큰
     */
    private String refreshToken;
}

