package com.boardbuddies.boardbuddiesserver.dto.reservation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReservationCalendarResponse {
    private Long reservationId;
    private LocalDate date;
    private String status;
}
