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
public class CrewCalendarResponse {

    @JsonProperty("date")
    private LocalDate date;

    /**
     * LOW: ~5
     * MEDIUM: 5~10
     * HIGH: 10~
     */
    @JsonProperty("occupancy_status")
    private String occupancyStatus;

}
