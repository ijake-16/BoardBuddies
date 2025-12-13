package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 부원 시즌방 사용 통계 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUsageResponse {

    /**
     * 사용자 ID
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 사용자 이름
     */
    @JsonProperty("name")
    private String name;

    /**
     * 시즌방 이용 횟수 (confirmed 상태)
     */
    @JsonProperty("usage_count")
    private Long usageCount;
}
