package com.boardbuddies.boardbuddiesserver.dto.club;

import com.boardbuddies.boardbuddiesserver.domain.Role;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 동아리 운영진 간단 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerSummaryDTO {
    
    /**
     * 학번
     */
    @JsonProperty("student_id")
    private String studentId;
    
    /**
     * 이름
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * 역할
     */
    @JsonProperty("role")
    private Role role;
    
    /**
     * User 엔티티로부터 DTO 생성
     */
    public static ManagerSummaryDTO from(User user) {
        return ManagerSummaryDTO.builder()
            .studentId(user.getStudentId())
            .name(user.getName())
            .role(user.getRole())
            .build();
    }
}

