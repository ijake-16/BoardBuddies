package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.crew.CrewUpdateRequest;
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

    @Mock
    private FileStorageService fileStorageService;

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

    @Test
    @DisplayName("크루 프로필 이미지 수정 - 업로드 성공")
    void updateCrewProfileImage_Upload() {
        // given
        Long userId = 1L;
        Long crewId = 1L;
        String fileName = "test-image.jpg";
        String uploadPath = "/uploads/" + fileName;

        Crew crew = Crew.builder().id(crewId).build();
        User user = User.builder().id(userId).build();
        user.joinCrew(crew, com.boardbuddies.boardbuddiesserver.domain.Role.PRESIDENT);

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file",
                fileName, "image/jpeg", "test data".getBytes());

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(fileStorageService.storeFile(any())).willReturn(fileName);

        // when
        crewService.updateCrewProfileImage(userId, crewId, file);

        // then
        assertThat(crew.getProfileImageUrl()).isEqualTo(uploadPath);
    }

    @Test
    @DisplayName("크루 프로필 이미지 수정 - 초기화 성공")
    void updateCrewProfileImage_Reset() {
        // given
        Long userId = 1L;
        Long crewId = 1L;

        Crew crew = Crew.builder().id(crewId).profileImageUrl("/uploads/old-image.jpg").build();
        User user = User.builder().id(userId).build();
        user.joinCrew(crew, com.boardbuddies.boardbuddiesserver.domain.Role.PRESIDENT);

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        crewService.updateCrewProfileImage(userId, crewId, null);

        // then
        assertThat(crew.getProfileImageUrl()).isNull();
    }

    @Test
    @DisplayName("크루 정보 수정 (이름) - 회장 성공")
    void updateCrew_Name_President_Success() {
        // given
        Long userId = 1L;
        Long crewId = 1L;
        String newName = "New Name";

        CrewUpdateRequest request = new CrewUpdateRequest(
                newName, null, null, null, null, null, null);

        Crew crew = Crew.builder().id(crewId).name("Old Name").build();
        User user = User.builder().id(userId).build();
        user.joinCrew(crew, com.boardbuddies.boardbuddiesserver.domain.Role.PRESIDENT);

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        crewService.updateCrew(userId, crewId, request);

        // then
        assertThat(crew.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("크루 프로필 이미지 수정 - 운영진 성공")
    void updateCrewProfileImage_Manager_Success() {
        // given
        Long userId = 2L;
        Long crewId = 1L;
        String fileName = "manager-upload.jpg";
        String uploadPath = "/uploads/" + fileName;

        Crew crew = Crew.builder().id(crewId).build();
        User user = User.builder().id(userId).build();
        user.joinCrew(crew, com.boardbuddies.boardbuddiesserver.domain.Role.MANAGER);

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file",
                fileName, "image/jpeg", "test data".getBytes());

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(fileStorageService.storeFile(any())).willReturn(fileName);

        // when
        crewService.updateCrewProfileImage(userId, crewId, file);

        // then
        assertThat(crew.getProfileImageUrl()).isEqualTo(uploadPath);
    }

    @Test
    @DisplayName("크루 정보 수정 (이름) - 운영진 실패 (권한 없음)")
    void updateCrew_Name_Manager_Fail() {
        // given
        Long userId = 2L;
        Long crewId = 1L;
        String newName = "New Name";

        com.boardbuddies.boardbuddiesserver.dto.crew.CrewUpdateRequest request = new com.boardbuddies.boardbuddiesserver.dto.crew.CrewUpdateRequest(
                newName, null, null, null, null, null, null);

        Crew crew = Crew.builder().id(crewId).build();
        User user = User.builder().id(userId).build();
        user.joinCrew(crew, com.boardbuddies.boardbuddiesserver.domain.Role.MANAGER);

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when, then
        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> {
                    crewService.updateCrew(userId, crewId, request);
                });
    }

    @Test
    @DisplayName("크루 정보 수정 (정책) - 운영진 성공")
    void updateCrew_Policy_Manager_Success() {
        // given
        Long userId = 2L;
        Long crewId = 1L;

        com.boardbuddies.boardbuddiesserver.dto.crew.CrewUpdateRequest request = new com.boardbuddies.boardbuddiesserver.dto.crew.CrewUpdateRequest(
                null, null, 1234, "MONDAY", "10:00", 20, null);

        Crew crew = Crew.builder().id(crewId).build();
        User user = User.builder().id(userId).build();
        user.joinCrew(crew, com.boardbuddies.boardbuddiesserver.domain.Role.MANAGER);

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crew));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        crewService.updateCrew(userId, crewId, request);

        // then
        assertThat(crew.getCrewPIN()).isEqualTo(1234);
        assertThat(crew.getReservationDay()).isEqualTo(com.boardbuddies.boardbuddiesserver.domain.DayOfWeek.MONDAY);
        assertThat(crew.getReservationTime()).isEqualTo(java.time.LocalTime.of(10, 0));
        assertThat(crew.getDailyCapacity()).isEqualTo(20);
    }
}
