package com.boardbuddies.boardbuddiesserver.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyReservationCount {
    private LocalDate date;
    private Long count;
}
