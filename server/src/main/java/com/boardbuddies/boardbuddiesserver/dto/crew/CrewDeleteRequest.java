package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 크루 삭제 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrewDeleteRequest {

    /**
     * 크루 PIN (확인용)
     */
    @NotNull(message = "크루 PIN은 필수입니다.")
    @Min(value = 0, message = "크루 PIN은 0000 이상이어야 합니다.")
    @Max(value = 9999, message = "크루 PIN은 9999 이하여야 합니다.")
    @JsonProperty("crewPIN")
    private Integer crewPIN;
}
