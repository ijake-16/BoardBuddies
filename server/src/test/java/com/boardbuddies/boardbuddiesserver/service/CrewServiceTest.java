package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.crew.CrewCalendarResponse;
import com.boardbuddies.boardbuddiesserver.dto.crew.DailyReservationCount;
import com.boardbuddies.boardbuddiesserver.repository.CrewRepository;
import com.boardbuddies.boardbuddiesserver.repository.ReservationRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CrewServiceTest {

    @InjectMocks
    private CrewService crewService;

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationService reservationService;

    @Test
    @DisplayName("주간 간략 크루 달력 조회 - 정상 동작")
    void getCrewBriefCalendar() {
        // given
        Long userId = 1L;
        Long crewId = 1L;
        // 2023-12-13 (Wednesday)
        LocalDate today = LocalDate.of(2023, 12, 13);
        // Expected range: 2023-12-11 (Monday) ~ 2023-12-17 (Sunday)
        LocalDate expectedStart = LocalDate.of(2023, 12, 11);
        LocalDate expectedEnd = LocalDate.of(2023, 12, 17);

        Crew crew = Crew.builder()
                .id(crewId)
                .name("Test Crew")
                .build();
        User user = User.builder()
                .id(userId)
                .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // Mocking reservation data
        DailyReservationCount count1 = new DailyReservationCount() {
            @Override
            public LocalDate getDate() {
                return expectedStart; // Monday
            }

            @Override
            public Long getCount() {
                return 3L; // LOW
            }
        };
        DailyReservationCount count2 = new DailyReservationCount() {
            @Override
            public LocalDate getDate() {
                return expectedEnd; // Sunday
            }

            @Override
            public Long getCount() {
                return 12L; // HIGH
            }
        };

        given(reservationRepository.findDailyCountsByCrewAndDateBetween(eq(crew), eq(expectedStart), eq(expectedEnd)))
                .willReturn(List.of(count1, count2));

        // when
        List<CrewCalendarResponse> result = crewService.getCrewBriefCalendar(userId, crewId, today);

        // then
        assertThat(result).hasSize(7); // Mon to Sun = 7 days

        // Verify Monday (Start)
        assertThat(result.get(0).getDate()).isEqualTo(expectedStart);
        assertThat(result.get(0).getOccupancyStatus()).isEqualTo("LOW"); // count 3 < 5

        // Verify Sunday (End)
        assertThat(result.get(6).getDate()).isEqualTo(expectedEnd);
        assertThat(result.get(6).getOccupancyStatus()).isEqualTo("HIGH"); // count 12 >= 10

        // Verify Middle (No data)
        assertThat(result.get(1).getDate()).isEqualTo(expectedStart.plusDays(1));
        assertThat(result.get(1).getOccupancyStatus()).isEqualTo("LOW"); // count 0 < 5
    }
}
