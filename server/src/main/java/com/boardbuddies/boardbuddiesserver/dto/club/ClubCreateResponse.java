package com.boardbuddies.boardbuddiesserver.dto.club;

import com.boardbuddies.boardbuddiesserver.domain.Club;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 동아리 생성 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateResponse {
    
    /**
     * 동아리 ID
     */
    @JsonProperty("club_id")
    private Long clubId;
    
    /**
     * 동아리명
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * 대학교명
     */
    @JsonProperty("univ")
    private String univ;
    
    /**
     * 동아리 상태
     */
    @JsonProperty("status")
    private String status;
    
    /**
     * Club 엔티티로부터 DTO 생성
     */
    public static ClubCreateResponse from(Club club) {
        return ClubCreateResponse.builder()
            .clubId(club.getId())
            .name(club.getName())
            .univ(club.getUniv())
            .status(club.getStatus())
            .build();
    }
}

