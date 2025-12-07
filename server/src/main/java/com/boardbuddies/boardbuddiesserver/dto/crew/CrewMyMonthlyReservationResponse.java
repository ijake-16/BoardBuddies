package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewMyMonthlyReservationResponse {

    @JsonProperty("date")
    private LocalDate date;

    /**
     * CONFIRMED: 확정됨
     * WAITING: 대기 중
     */
    @JsonProperty("status")
    private String status;

    @JsonProperty("waiting_order")
    private Integer waitingOrder;
}
