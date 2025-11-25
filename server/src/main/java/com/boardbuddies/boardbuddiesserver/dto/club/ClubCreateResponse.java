package com.boardbuddies.boardbuddiesserver.dto.club;

import com.boardbuddies.boardbuddiesserver.domain.Club;
import com.boardbuddies.boardbuddiesserver.domain.DayOfWeek;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

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
    @JsonProperty("clubName")
    private String clubName;
    
    /**
     * 대학교명
     */
    @JsonProperty("univ")
    private String univ;

    /**
     * 예약 요일
     * 일(0) ~ 토(6)
     */
    @JsonProperty("reservation_day")
    private DayOfWeek reservationDay;

    /**
     * 예약 시간
     */
    @JsonProperty("reservation_time")
    private LocalTime reservationTime;

    /**
     * 동아리 PIN
     */
    @JsonProperty("clubPIN")
    private Integer clubPIN;

    /**
     * 동아리 운영진 (회장 + 매니저)
     */
    @JsonProperty("manager_list")
    private Set<ManagerSummaryDTO> managerList;

    /**
     * 동아리 상태
     */
    @JsonProperty("clubStatus")
    private String clubStatus;
    
    /**
     * Club 엔티티와 운영진 목록으로부터 DTO 생성
     * 
     * @param club 동아리 엔티티
     * @param managers 운영진 목록 (회장 + 매니저)
     * @return 동아리 생성 응답 DTO
     */
    public static ClubCreateResponse from(Club club, Set<User> managers) {
        Set<ManagerSummaryDTO> managerDTOs = managers.stream()
            .map(ManagerSummaryDTO::from)
            .collect(Collectors.toSet());
        
        return ClubCreateResponse.builder()
            .clubId(club.getId())
            .clubName(club.getName())
            .univ(club.getUniv())
            .reservationDay(club.getReservationDay())
            .reservationTime(club.getReservationTime())
            .clubPIN(club.getClubPIN())
            .managerList(managerDTOs)
            .clubStatus(club.getStatus())
            .build();
    }
}

