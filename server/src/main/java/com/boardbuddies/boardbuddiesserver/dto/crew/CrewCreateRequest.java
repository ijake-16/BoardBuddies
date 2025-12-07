package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 크루 생성 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewCreateRequest {

    /**
     * 크루명
     */
    @NotBlank(message = "크루명은 필수입니다.")
    @JsonProperty("crewName")
    private String crewName;

    /**
     * 크루 PIN (4자리 숫자)
     */
    @NotNull(message = "크루 PIN은 필수입니다.")
    @Min(value = 0, message = "크루 PIN은 0000 이상이어야 합니다.")
    @Max(value = 9999, message = "크루 PIN은 9999 이하여야 합니다.")
    @JsonProperty("crewPIN")
    private Integer crewPIN;

    /**
     * 대학교명
     */
    @NotBlank(message = "대학교명은 필수입니다.")
    @JsonProperty("univ")
    private String univ;

    /**
     * 예약 요일 (SUNDAY, MONDAY, ..., SATURDAY, 선택사항)
     */
    @Pattern(regexp = "^(SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY)$", message = "예약 요일은 SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY 중 하나여야 합니다.")
    @JsonProperty("reservation_day")
    private String reservationDay;

    /**
     * 예약 시간 (HH:MM, 선택사항)
     */
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "예약 시간 형식이 올바르지 않습니다. (HH:MM)")
    @JsonProperty("reservation_time")
    private String reservationTime;

    /**
     * 운영진 학번 리스트
     */
    @JsonProperty("manager_list")
    private Set<String> managerList;

    /**
     * 일별 수용 인원 (기본값 20)
     */
    @Min(value = 1, message = "일별 수용 인원은 1명 이상이어야 합니다.")
    @JsonProperty("dailyCapacity")
    private Integer dailyCapacity;
}
