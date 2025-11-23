package com.boardbuddies.boardbuddiesserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인 요청 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    
    /**
     * 소셜 제공자 (kakao, naver)
     */
    private SocialProvider provider;
    
    /**
     * 소셜 액세스 토큰 (Authorization 헤더에서 추출)
     */
    private String socialAccessToken;
}

