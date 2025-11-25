package com.boardbuddies.boardbuddiesserver.dto.club;

import com.boardbuddies.boardbuddiesserver.domain.Club;
import com.boardbuddies.boardbuddiesserver.domain.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 동아리 목록 조회 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubListResponse {
    
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
     * 회장 ID
     */
    @JsonProperty("president_id")
    private Long presidentId;
    
    /**
     * 예약 요일
     * SUNDAY, MONDAY, TUESDAY, ..., SATURDAY
     */
    @JsonProperty("reservation_day")
    private DayOfWeek reservationDay;
    
    /**
     * 예약 시간 (HH:MM:SS)
     */
    @JsonProperty("reservation_time")
    private LocalTime reservationTime;
    
    /**
     * 동아리 상태
     */
    @JsonProperty("status")
    private String status;
    
    /**
     * 생성일시
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Club 엔티티와 회장 ID로 DTO 생성
     * 
     * @param club 동아리 엔티티
     * @param presidentId 회장 ID (nullable)
     * @return 동아리 목록 응답 DTO
     */
    public static ClubListResponse from(Club club, Long presidentId) {
        return ClubListResponse.builder()
            .clubId(club.getId())
            .name(club.getName())
            .univ(club.getUniv())
            .presidentId(presidentId)
            .reservationDay(club.getReservationDay())
            .reservationTime(club.getReservationTime())
            .status(club.getStatus())
            .createdAt(club.getCreatedAt())
            .updatedAt(club.getUpdatedAt())
            .build();
    }
}

