package com.boardbuddies.boardbuddiesserver.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class ReservationDayDetailResponse {
    private LocalDate date;
    private String status; // open, closed
    private int booked;
    private int remaining;

    @JsonProperty("member_list")
    private List<String> memberList;

    @JsonProperty("waiting_member_list")
    private List<String> waitingMemberList;

    @JsonProperty("my_reservation")
    private MyReservationInfo myReservation;

    @Getter
    @Builder
    public static class MyReservationInfo {
        private boolean exists;

        @JsonProperty("reservation_id")
        private Long reservationId;

        @JsonProperty("created_at")
        private ZonedDateTime createdAt;
    }
}
