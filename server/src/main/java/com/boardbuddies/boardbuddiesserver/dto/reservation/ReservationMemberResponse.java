package com.boardbuddies.boardbuddiesserver.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationMemberResponse {
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 예약자 이름 (게스트 예약인 경우 게스트 이름, 일반 예약인 경우 부원 이름)
     */
    @JsonProperty("name")
    private String name;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    /**
     * 게스트 예약인 경우 게스트 ID (일반 예약인 경우 null)
     */
    @JsonProperty("guest_id")
    private Long guestId;

    /**
     * 게스트 예약인 경우 예약한 부원 이름 (일반 예약인 경우 null)
     */
    @JsonProperty("registered_by_name")
    private String registeredByName;

    /**
     * 강습 신청 여부
     */
    @JsonProperty("teaching")
    private Boolean teaching;
}
