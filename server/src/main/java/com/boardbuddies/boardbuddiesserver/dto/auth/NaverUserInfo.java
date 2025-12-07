package com.boardbuddies.boardbuddiesserver.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 네이버 사용자 정보 응답
 */
@Getter
@NoArgsConstructor
public class NaverUserInfo {
    
    @JsonProperty("resultcode")
    private String resultCode;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("response")
    private NaverResponse response;
    
    @Getter
    @NoArgsConstructor
    public static class NaverResponse {
        
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("nickname")
        private String nickname;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("profile_image")
        private String profileImage;
    }
    
    /**
     * 사용자 고유 ID 반환
     */
    public String getSocialId() {
        return response != null ? response.getId() : null;
    }
    
    /**
     * 닉네임 반환
     */
    public String getNickname() {
        return response != null ? response.getNickname() : null;
    }
    
    /**
     * 이메일 반환
     */
    public String getEmail() {
        return response != null ? response.getEmail() : null;
    }
    
    /**
     * 프로필 이미지 URL 반환
     */
    public String getProfileImageUrl() {
        return response != null ? response.getProfileImage() : null;
    }
}

