package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
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
 * 크루 생성 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewCreateResponse {

    /**
     * 크루 ID
     */
    @JsonProperty("crew_id")
    private Long crewId;

    /**
     * 크루명
     */
    @JsonProperty("crewName")
    private String crewName;

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
     * 크루 PIN
     */
    @JsonProperty("crewPIN")
    private Integer crewPIN;

    /**
     * 크루 운영진 (회장 + 매니저)
     */
    @JsonProperty("manager_list")
    private Set<ManagerSummaryDTO> managerList;

    /**
     * 크루 상태
     */
    @JsonProperty("crewStatus")
    private String crewStatus;

    /**
     * Crew 엔티티와 운영진 목록으로부터 DTO 생성
     * 
     * @param crew     크루 엔티티
     * @param managers 운영진 목록 (회장 + 매니저)
     * @return 크루 생성 응답 DTO
     */
    public static CrewCreateResponse from(Crew crew, Set<User> managers) {
        Set<ManagerSummaryDTO> managerDTOs = managers.stream()
                .map(ManagerSummaryDTO::from)
                .collect(Collectors.toSet());

        return CrewCreateResponse.builder()
                .crewId(crew.getId())
                .crewName(crew.getName())
                .univ(crew.getUniv())
                .reservationDay(crew.getReservationDay())
                .reservationTime(crew.getReservationTime())
                .crewPIN(crew.getCrewPIN())
                .managerList(managerDTOs)
                .crewStatus(crew.getStatus())
                .build();
    }
}
