package com.boardbuddies.boardbuddiesserver.dto.reservation;

import com.boardbuddies.boardbuddiesserver.domain.Reservation;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ReservationResponse {

    @JsonProperty("reservation_id")
    private Long reservationId;

    @JsonProperty("club_id")
    private Long clubId;

    private LocalDate date;

    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .clubId(reservation.getClub().getId())
                .date(reservation.getDate())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
