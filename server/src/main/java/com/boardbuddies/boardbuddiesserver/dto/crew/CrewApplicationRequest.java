package com.boardbuddies.boardbuddiesserver.dto.crew;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 크루 가입 신청 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewApplicationRequest {

    /**
     * 크루 PIN
     */
    @NotNull(message = "크루 PIN은 필수입니다.")
    private Integer crewPIN;
}
