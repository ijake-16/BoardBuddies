package com.boardbuddies.boardbuddiesserver.dto.club;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 동아리 가입 신청 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubApplicationRequest {
    
    /**
     * 동아리 PIN
     */
    @NotNull(message = "동아리 PIN은 필수입니다.")
    private Integer clubPIN;
}

