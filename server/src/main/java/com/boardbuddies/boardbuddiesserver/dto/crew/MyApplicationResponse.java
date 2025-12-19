package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.boardbuddies.boardbuddiesserver.domain.Application;
import com.boardbuddies.boardbuddiesserver.domain.MemberStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 마이페이지용 크루 가입 신청 상태 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyApplicationResponse {

    /**
     * 신청 ID
     */
    @JsonProperty("application_id")
    private Long applicationId;

    /**
     * 크루 ID
     */
    @JsonProperty("crew_id")
    private Long crewId;

    /**
     * 크루 이름
     */
    @JsonProperty("crew_name")
    private String crewName;

    /**
     * 신청 상태
     */
    @JsonProperty("status")
    private MemberStatus status;

    /**
     * 신청 시간
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * 처리 시간 (승인/거절 시간)
     */
    @JsonProperty("processed_at")
    private LocalDateTime processedAt;

    /**
     * Application 엔티티로부터 DTO 생성
     */
    public static MyApplicationResponse from(Application application) {
        return MyApplicationResponse.builder()
                .applicationId(application.getId())
                .crewId(application.getCrew().getId())
                .crewName(application.getCrew().getName())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .processedAt(application.getProcessedAt())
                .build();
    }
}

