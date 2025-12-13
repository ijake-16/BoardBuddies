package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 크루 정보 수정 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewUpdateRequest {

    /**
     * 크루명 (선택사항)
     */
    @Size(max = 50, message = "크루명은 50자를 초과할 수 없습니다.")
    @JsonProperty("crewName")
    private String crewName;

    /**
     * 운영진 학번 리스트 (선택사항)
     */
    @JsonProperty("manager_list")
    private Set<String> managerList;

    /**
     * 크루 PIN (선택사항)
     */
    @Min(value = 0, message = "크루 PIN은 0000 이상이어야 합니다.")
    @Max(value = 9999, message = "크루 PIN은 9999 이하여야 합니다.")
    @JsonProperty("crewPIN")
    private Integer crewPIN;

    /**
     * 예약 요일 (선택사항)
     * SUNDAY, MONDAY, ..., SATURDAY
     */
    @Pattern(regexp = "^(SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY)$", message = "예약 요일은 SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY 중 하나여야 합니다.")
    @JsonProperty("reservation_day")
    private String reservationDay;

    /**
     * 예약 시간 (선택사항)
     * HH:MM
     */
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "예약 시간 형식이 올바르지 않습니다. (HH:MM)")
    @JsonProperty("reservation_time")
    private String reservationTime;

    /**
     * 일별 수용 인원 (선택사항)
     */
    @Min(value = 1, message = "일별 수용 인원은 1명 이상이어야 합니다.")
    @JsonProperty("dailyCapacity")
    private Integer dailyCapacity;

    /**
     * 시즌방 제한 여부 (선택사항)
     * true: 제한함, false: 제한 안함 (무제한)
     */
    @JsonProperty("isCapacityLimited")
    private Boolean isCapacityLimited;
}
