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
        /**
         * 예약자 이름 (게스트 예약인 경우 게스트 이름, 일반 예약인 경우 부원 이름)
         */
        private String name;
        private Role role;
        /**
         * 게스트 예약인 경우 게스트 ID (일반 예약인 경우 null)
         */
        private Long guestId;
        /**
         * 게스트 예약인 경우 예약한 부원 이름 (일반 예약인 경우 null)
         */
        private String registeredByName;
        /**
         * 강습 신청 여부
         */
        private Boolean teaching;
    }
}
