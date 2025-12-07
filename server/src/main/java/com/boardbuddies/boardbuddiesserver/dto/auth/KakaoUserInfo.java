package com.boardbuddies.boardbuddiesserver.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 사용자 정보 응답
 */
@Getter
@NoArgsConstructor
public class KakaoUserInfo {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
    
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        
        @JsonProperty("profile")
        private Profile profile;
        
        @JsonProperty("email")
        private String email;
        
        @Getter
        @NoArgsConstructor
        public static class Profile {
            
            @JsonProperty("nickname")
            private String nickname;
            
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }
    
    /**
     * 사용자 고유 ID 반환
     */
    public String getSocialId() {
        return String.valueOf(id);
    }
    
    /**
     * 닉네임 반환
     */
    public String getNickname() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null 
            ? kakaoAccount.getProfile().getNickname() 
            : null;
    }
    
    /**
     * 이메일 반환
     */
    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }
    
    /**
     * 프로필 이미지 URL 반환
     */
    public String getProfileImageUrl() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null 
            ? kakaoAccount.getProfile().getProfileImageUrl() 
            : null;
    }
}

