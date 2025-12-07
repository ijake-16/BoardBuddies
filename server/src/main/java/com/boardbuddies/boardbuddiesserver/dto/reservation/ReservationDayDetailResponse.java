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
    private int waitingCount;
    private int capacity;

    @JsonProperty("member_list")
    private List<ReservationMemberResponse> memberList;

    @JsonProperty("waiting_member_list")
    private List<ReservationMemberResponse> waitingMemberList;

    @JsonProperty("my_reservation")
    private MyReservationInfo myReservation;

    @Getter
    @Builder
    public static class MyReservationInfo {
        @JsonProperty("reservation_id")
        private Long reservationId;
    }
}
