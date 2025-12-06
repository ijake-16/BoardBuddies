package com.boardbuddies.boardbuddiesserver.dto.crew;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 크루 가입 신청 승인/거절 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDecisionRequest {

    /**
     * 결정 (0: 거절, 1: 승인)
     */
    @NotNull(message = "결정값은 필수입니다.")
    @Min(value = 0, message = "결정값은 0 또는 1이어야 합니다.")
    @Max(value = 1, message = "결정값은 0 또는 1이어야 합니다.")
    private Integer decision;

    /**
     * 승인 여부 확인
     */
    public boolean isApproved() {
        return decision == 1;
    }
}
