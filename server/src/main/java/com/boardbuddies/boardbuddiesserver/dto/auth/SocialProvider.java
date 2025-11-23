package com.boardbuddies.boardbuddiesserver.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 소셜 로그인 제공자
 */
@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    KAKAO("kakao", "https://kapi.kakao.com/v2/user/me"),
    NAVER("naver", "https://openapi.naver.com/v1/nid/me");
    
    private final String name;
    private final String userInfoUrl;
}

