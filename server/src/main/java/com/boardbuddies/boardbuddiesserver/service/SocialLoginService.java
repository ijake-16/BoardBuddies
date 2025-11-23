package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.dto.auth.KakaoUserInfo;
import com.boardbuddies.boardbuddiesserver.dto.auth.NaverUserInfo;
import com.boardbuddies.boardbuddiesserver.dto.auth.SocialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 소셜 로그인 서비스
 * 카카오, 네이버 API를 호출하여 사용자 정보를 가져옵니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {
    
    private final WebClient webClient;
    
    /**
     * 소셜 제공자로부터 사용자 정보 조회
     * 
     * @param provider 소셜 제공자 (KAKAO, NAVER)
     * @param accessToken 소셜 액세스 토큰
     * @return 사용자 정보 (socialId, nickname, email, profileImageUrl)
     * @throws RuntimeException 사용자 정보 조회 실패 시
     */
    public SocialUserInfo getUserInfo(SocialProvider provider, String accessToken) {
        try {
            switch (provider) {
                case KAKAO:
                    return getKakaoUserInfo(accessToken);
                case NAVER:
                    return getNaverUserInfo(accessToken);
                default:
                    throw new IllegalArgumentException("지원하지 않는 소셜 제공자입니다: " + provider);
            }
        } catch (WebClientResponseException.Unauthorized e) {
            log.error("만료된 소셜 토큰입니다. provider: {}", provider, e);
            throw new RuntimeException("만료된 소셜 토큰입니다.");
        } catch (WebClientResponseException e) {
            log.error("소셜로그인 회원 정보 조회 실패. provider: {}", provider, e);
            throw new RuntimeException("소셜로그인 회원 정보 조회 실패");
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 예외 발생. provider: {}", provider, e);
            throw new RuntimeException("회원 정보 조회 실패");
        }
    }
    
    /**
     * 카카오 사용자 정보 조회
     */
    private SocialUserInfo getKakaoUserInfo(String accessToken) {
        KakaoUserInfo kakaoUserInfo = webClient.get()
            .uri(SocialProvider.KAKAO.getUserInfoUrl())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(KakaoUserInfo.class)
            .block();
        
        if (kakaoUserInfo == null) {
            throw new RuntimeException("카카오 사용자 정보를 가져올 수 없습니다.");
        }
        
        return SocialUserInfo.builder()
            .socialId(kakaoUserInfo.getSocialId())
            .nickname(kakaoUserInfo.getNickname())
            .email(kakaoUserInfo.getEmail())
            .profileImageUrl(kakaoUserInfo.getProfileImageUrl())
            .provider(SocialProvider.KAKAO)
            .build();
    }
    
    /**
     * 네이버 사용자 정보 조회
     */
    private SocialUserInfo getNaverUserInfo(String accessToken) {
        NaverUserInfo naverUserInfo = webClient.get()
            .uri(SocialProvider.NAVER.getUserInfoUrl())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(NaverUserInfo.class)
            .block();
        
        if (naverUserInfo == null || !"00".equals(naverUserInfo.getResultCode())) {
            throw new RuntimeException("네이버 사용자 정보를 가져올 수 없습니다.");
        }
        
        return SocialUserInfo.builder()
            .socialId(naverUserInfo.getSocialId())
            .nickname(naverUserInfo.getNickname())
            .email(naverUserInfo.getEmail())
            .profileImageUrl(naverUserInfo.getProfileImageUrl())
            .provider(SocialProvider.NAVER)
            .build();
    }
    
    /**
     * 소셜 사용자 정보 DTO
     */
    @lombok.Getter
    @lombok.Builder
    public static class SocialUserInfo {
        private String socialId;
        private String nickname;
        private String email;
        private String profileImageUrl;
        private SocialProvider provider;
    }
}

