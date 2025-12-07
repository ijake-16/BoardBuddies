package com.boardbuddies.boardbuddiesserver.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewCalendarWrapperResponse {

    @JsonProperty("calendar")
    private List<CrewCalendarResponse> calendar;

    @JsonProperty("my_reservations")
    private List<CrewMyMonthlyReservationResponse> myReservations;
}
