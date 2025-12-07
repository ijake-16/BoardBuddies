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
 * 크루 목록 조회 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewListResponse {

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonProperty("reservation_time")
    private LocalTime reservationTime;

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
     * Crew 엔티티와 회장 ID로 DTO 생성
     * 
     * @param crew        크루 엔티티
     * @param presidentId 회장 ID (nullable)
     * @return 크루 목록 응답 DTO
     */
    public static CrewListResponse from(Crew crew, Long presidentId) {
        return CrewListResponse.builder()
                .crewId(crew.getId())
                .name(crew.getName())
                .univ(crew.getUniv())
                .presidentId(presidentId)
                .reservationDay(crew.getReservationDay())
                .reservationTime(crew.getReservationTime())
                .status(crew.getStatus())
                .createdAt(crew.getCreatedAt())
                .updatedAt(crew.getUpdatedAt())
                .build();
    }
}
