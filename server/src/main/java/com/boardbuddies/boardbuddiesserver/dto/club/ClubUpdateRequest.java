package com.boardbuddies.boardbuddiesserver.dto.club;

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
 * 동아리 정보 수정 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubUpdateRequest {
    
    /**
     * 동아리명 (선택사항)
     */
    @Size(max = 50, message = "동아리명은 50자를 초과할 수 없습니다.")
    @JsonProperty("clubName")
    private String clubName;
    
    /**
     * 운영진 학번 리스트 (선택사항)
     */
    @JsonProperty("manager_list")
    private Set<String> managerList;
    
    /**
     * 동아리 PIN (선택사항)
     */
    @Min(value = 0, message = "동아리 PIN은 0000 이상이어야 합니다.")
    @Max(value = 9999, message = "동아리 PIN은 9999 이하여야 합니다.")
    @JsonProperty("clubPIN")
    private Integer clubPIN;
    
    /**
     * 예약 요일 (선택사항)
     * SUNDAY, MONDAY, ..., SATURDAY
     */
    @Pattern(regexp = "^(SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY)$",
             message = "예약 요일은 SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY 중 하나여야 합니다.")
    @JsonProperty("reservation_day")
    private String reservationDay;
    
    /**
     * 예약 시간 (선택사항)
     * HH:MM:SS
     */
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$",
             message = "예약 시간 형식이 올바르지 않습니다. (HH:MM:SS)")
    @JsonProperty("reservation_time")
    private String reservationTime;
}

