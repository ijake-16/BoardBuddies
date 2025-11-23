package com.boardbuddies.boardbuddiesserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 임시 토큰 응답 (소셜 로그인 성공, 추가 정보 입력 필요)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempTokenResponse {
    
    /**
     * 인증 타입 (Signup)
     */
    private AuthType type;
    
    /**
     * 임시 액세스 토큰 (추가 정보 입력용)
     */
    private String tempAccessToken;
    
    /**
     * 소셜 제공자
     */
    private SocialProvider provider;
    
    /**
     * 소셜 이메일
     */
    private String email;
}

