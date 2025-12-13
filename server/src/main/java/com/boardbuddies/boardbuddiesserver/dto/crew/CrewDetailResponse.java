package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
import com.boardbuddies.boardbuddiesserver.domain.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 크루 상세 조회 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewDetailResponse {

    /**
     * 크루 ID
     */
    @JsonProperty("crew_id")
    private Long crewId;

    /**
     * 크루명
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonProperty("reservation_time")
    private LocalTime reservationTime;

    /**
     * 일별 수용 인원
     */
    @JsonProperty("dailyCapacity")
    private Integer dailyCapacity;

    /**
     * 크루 상태
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
     * 회장 이름
     */
    @JsonProperty("president_name")
    private String presidentName;

    /**
     * 현재 부원 수 (회장 포함)
     */
    @JsonProperty("member_count")
    private Integer memberCount;

    /**
     * Crew 엔티티로부터 DTO 생성
     * 
     * @param crew          크루 엔티티
     * @param presidentName 회장 이름
     * @param memberCount   부원 수
     * @return 크루 상세 응답 DTO
     */
    public static CrewDetailResponse from(Crew crew, String presidentName, Integer memberCount) {
        return CrewDetailResponse.builder()
                .crewId(crew.getId())
                .name(crew.getName())
                .univ(crew.getUniv())
                .reservationDay(crew.getReservationDay())
                .reservationTime(crew.getReservationTime())
                .dailyCapacity(crew.getDailyCapacity())
                .status(crew.getStatus())
                .createdAt(crew.getCreatedAt())
                .updatedAt(crew.getUpdatedAt())
                .presidentName(presidentName)
                .memberCount(memberCount)
                .build();
    }
}
