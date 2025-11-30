package com.boardbuddies.boardbuddiesserver.dto.reservation;

import com.boardbuddies.boardbuddiesserver.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationListResponse {

    private String date;
    private List<UserSummary> confirmed;
    private List<UserSummary> waiting;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long userId;
        private String name;
        private Role role;
    }
}
