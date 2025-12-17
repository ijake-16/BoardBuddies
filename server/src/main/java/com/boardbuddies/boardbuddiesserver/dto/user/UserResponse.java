package com.boardbuddies.boardbuddiesserver.dto.user;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
import com.boardbuddies.boardbuddiesserver.domain.Gender;
import com.boardbuddies.boardbuddiesserver.domain.Role;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.auth.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
    private Role role;

    private LocalDate birthDate;
    private String school;
    private String studentId;
    private Gender gender;
    private String phoneNumber;
    private String profileImageUrl;

    private String socialId;
    private SocialProvider socialProvider;

    private Boolean isRegistered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 사용자가 속한 동아리 정보 (id + name)
     * 없으면 null
     */
    private CrewSummary crew;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrewSummary {
        private Long crewId;
        private String crewName;

        public static CrewSummary from(Crew crew) {
            if (crew == null) {
                return null;
            }
            return CrewSummary.builder()
                    .crewId(crew.getId())
                    .crewName(crew.getName())
                    .build();
        }
    }

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .birthDate(user.getBirthDate())
                .school(user.getSchool())
                .studentId(user.getStudentId())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .socialId(user.getSocialId())
                .socialProvider(user.getSocialProvider())
                .isRegistered(user.getIsRegistered())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .crew(CrewSummary.from(user.getCrew()))
                .build();
    }
}

