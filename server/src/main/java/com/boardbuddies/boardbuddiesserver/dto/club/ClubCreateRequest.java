package com.boardbuddies.boardbuddiesserver.dto.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 동아리 생성 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubCreateRequest {
    
    /**
     * 동아리명
     */
    @NotBlank(message = "동아리명은 필수입니다.")
    @JsonProperty("clubName")
    private String clubName;
    
    /**
     * 동아리 PIN (4자리 숫자)
     */
    @NotNull(message = "동아리 PIN은 필수입니다.")
    @Min(value = 0, message = "동아리 PIN은 0000 이상이어야 합니다.")
    @Max(value = 9999, message = "동아리 PIN은 9999 이하여야 합니다.")
    @JsonProperty("clubPIN")
    private Integer clubPIN;
    
    /**
     * 대학교명
     */
    @NotBlank(message = "대학교명은 필수입니다.")
    @JsonProperty("univ")
    private String univ;
    
    /**
     * 예약 요일 (SUNDAY, MONDAY, ..., SATURDAY, 선택사항)
     */
    @Pattern(regexp = "^(SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY)$",
             message = "예약 요일은 SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY 중 하나여야 합니다.")
    @JsonProperty("reservation_day")
    private String reservationDay;
    
    /**
     * 예약 시간 (HH:MM:SS, 선택사항)
     */
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", 
             message = "예약 시간 형식이 올바르지 않습니다. (HH:MM:SS)")
    @JsonProperty("reservation_time")
    private String reservationTime;
    
    /**
     * 운영진 학번 리스트
     */
    @JsonProperty("manager_list")
    private Set<String> managerList;
}

