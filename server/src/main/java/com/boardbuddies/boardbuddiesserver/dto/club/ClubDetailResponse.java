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
 * 동아리 상세 조회 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDetailResponse {
    
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
     * 예약 요일
     * SUNDAY, MONDAY, ..., SATURDAY
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
     * Club 엔티티로부터 DTO 생성
     * 
     * @param club 동아리 엔티티
     * @return 동아리 상세 응답 DTO
     */
    public static ClubDetailResponse from(Club club) {
        return ClubDetailResponse.builder()
            .clubId(club.getId())
            .name(club.getName())
            .univ(club.getUniv())
            .reservationDay(club.getReservationDay())
            .reservationTime(club.getReservationTime())
            .status(club.getStatus())
            .createdAt(club.getCreatedAt())
            .updatedAt(club.getUpdatedAt())
            .build();
    }
}

