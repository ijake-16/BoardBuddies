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
public class ReservationMultiResponse {

    @JsonProperty("club_id")
    private Long clubId;

    private List<ReservationResult> results;

    private ReservationSummary summary;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationResult {
        private LocalDate date;
        private String status; // created | duplicated | sold_out | closed | invalid | conflict

        @JsonProperty("reservation_id")
        private Long reservationId;

        private String reason;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationSummary {
        private int requested;
        private int succeeded;
        private int failed;
    }
}
