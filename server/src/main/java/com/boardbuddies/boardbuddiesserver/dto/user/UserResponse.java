package com.boardbuddies.boardbuddiesserver.dto.user;

import com.boardbuddies.boardbuddiesserver.domain.Role;
import com.boardbuddies.boardbuddiesserver.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String userName;
    private String email;
    private Role role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
