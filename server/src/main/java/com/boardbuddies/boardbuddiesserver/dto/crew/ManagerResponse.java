package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.boardbuddies.boardbuddiesserver.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 운영진/부원 정보 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerResponse {

    /**
     * 사용자 ID
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 이름
     */
    @JsonProperty("name")
    private String name;

    /**
     * 학번
     */
    @JsonProperty("student_id")
    private String studentId;

    /**
     * 역할 (PRESIDENT, MANAGER, MEMBER)
     */
    @JsonProperty("role")
    private String role;

    public static ManagerResponse from(User user) {
        return ManagerResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .studentId(user.getStudentId())
                .role(user.getRole().name())
                .build();
    }
}
