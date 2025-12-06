package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.boardbuddies.boardbuddiesserver.domain.Application;
import com.boardbuddies.boardbuddiesserver.domain.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 크루 가입 신청 목록 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewApplicationResponse {

    /**
     * 신청 ID
     */
    private Long applicationId;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자 이름
     */
    private String userName;

    /**
     * 학번
     */
    private String studentId;

    /**
     * 신청 상태
     */
    private MemberStatus status;

    /**
     * Application 엔티티로부터 DTO 생성
     */
    public static CrewApplicationResponse from(Application application) {
        return CrewApplicationResponse.builder()
                .applicationId(application.getId())
                .userId(application.getUser().getId())
                .userName(application.getUser().getName())
                .studentId(application.getUser().getStudentId())
                .status(application.getStatus())
                .build();
    }
}
