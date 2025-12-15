package com.boardbuddies.boardbuddiesserver.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @JsonProperty("crew_id")
    private Long crewId;

    private List<LocalDate> dates;

    /**
     * 게스트 예약인 경우 게스트 ID (일반 예약인 경우 null)
     */
    @JsonProperty("guest_id")
    private Long guestId;
}
