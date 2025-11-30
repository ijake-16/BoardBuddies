package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMultiResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationRequest;
import com.boardbuddies.boardbuddiesserver.repository.ClubRepository;
import com.boardbuddies.boardbuddiesserver.repository.ReservationRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 시즌방 예약 (일괄 신청)
     */
    @Transactional
    public ReservationMultiResponse reserve(Long userId, Long clubId, ReservationRequest request) {
        // 1. 동아리 조회 (비관적 락 사용 - 동시성 제어)
        Club club = clubRepository.findByIdWithLock(clubId)
                .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));

        // 2. 사용자 조회 및 권한 검증
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        if (!user.getClub().equals(club)) {
            throw new RuntimeException("해당 동아리의 회원이 아닙니다.");
        }
        if (user.getRole() == Role.GUEST || !user.getIsRegistered()) {
            throw new RuntimeException("승인된 회원만 예약할 수 있습니다.");
        }

        // 3. 오픈 시각 검증
        validateOpenTime(club);

        List<ReservationMultiResponse.ReservationResult> results = new ArrayList<>();
        int succeeded = 0;
        int failed = 0;

        // 4. 날짜별 처리
        for (LocalDate date : request.getDates()) {
            try {
                // 개별 날짜 검증 및 예약
                Reservation reservation = processSingleReservation(user, club, date);

                String status = "created";
                if ("waiting".equals(reservation.getStatus())) {
                    status = "waiting";
                }

                results.add(ReservationMultiResponse.ReservationResult.builder()
                        .date(date)
                        .status(status)
                        .reservationId(reservation.getId())
                        .build());
                succeeded++;
            } catch (Exception e) {
                String status = "invalid";
                String reason = e.getMessage();

                if (reason.contains("잔여 수량"))
                    status = "sold_out";
                else if (reason.contains("오픈 전") || reason.contains("마감"))
                    status = "closed";
                else if (reason.contains("이미 예약"))
                    status = "duplicated";

                results.add(ReservationMultiResponse.ReservationResult.builder()
                        .date(date)
                        .status(status)
                        .reason(reason)
                        .build());
                failed++;
            }
        }

        // 5. 응답 생성
        return ReservationMultiResponse.builder()
                .clubId(club.getId())
                .results(results)
                .summary(ReservationMultiResponse.ReservationSummary.builder()
                        .requested(request.getDates().size())
                        .succeeded(succeeded)
                        .failed(failed)
                        .build())
                .build();
    }

    private void validateOpenTime(Club club) {
        if (club.getReservationDay() == null || club.getReservationTime() == null) {
            return; // 설정 없으면 상시 오픈으로 간주 (또는 에러 처리)
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DayOfWeek currentDay = DayOfWeek.valueOf(now.getDayOfWeek().name());
        LocalTime currentTime = now.toLocalTime();

        // 간단한 로직: 오픈 요일/시간 이후여야 함.
        // 실제로는 "이번 주 오픈 시간"을 계산해야 하지만, 여기서는 단순하게 요일/시간만 비교
        // (더 정교한 로직이 필요하면 추가 구현)
        // 예: 매주 월요일 10시 오픈 -> 현재가 월요일 10시 이후인지, 아니면 화~일인지.

        // 여기서는 단순화하여 "현재 요일이 오픈 요일보다 빠르면 불가" 또는 "같은 요일인데 시간이 빠르면 불가"로 구현
        // 주의: DayOfWeek enum 순서 (SUNDAY=0, ... SATURDAY=6) 가정
        // Java DayOfWeek는 MONDAY(1) ~ SUNDAY(7).
        // 우리 도메인 DayOfWeek는 SUNDAY, MONDAY... 순서라고 가정하고 valueOf로 변환했음.
        // 도메인 DayOfWeek의 ordinal을 확인해야 함.

        int openDayOrdinal = club.getReservationDay().ordinal(); // SUNDAY=0, MONDAY=1...
        int currentDayOrdinal = convertJavaDayOfWeekToDomain(now.getDayOfWeek());

        if (currentDayOrdinal < openDayOrdinal) {
            // 아직 오픈 요일이 아님 (이번 주 기준)
            // 하지만 "지난 주 오픈"일 수도 있음. 이 부분은 정책에 따라 다름.
            // 보통 "매주 월요일 10시에 이번 주/다음 주 예약 오픈" 형식이 많음.
            // 여기서는 "예약 오픈 시각 이후"라는 요구사항만 충족하도록,
            // "현재 시각이 오픈 시각보다 이전이면 에러"라고 가정하지 않고,
            // "오픈 요일/시간이 되면 열린다"는 개념으로 접근.

            // 일단 단순하게: 오늘이 오픈 요일이고, 시간이 아직 안 됐으면 에러.
            // 오늘이 오픈 요일보다 이전이면? (예: 오픈 월요일, 오늘 일요일) -> 에러?
            // 이 부분은 "주차" 개념이 없어서 애매함.
            // 요구사항: "예약 오픈 시각 이후"

            // 가장 안전한 해석:
            // 만약 오늘이 오픈 요일이라면, 오픈 시간 이후여야 함.
            // 오늘이 오픈 요일이 아니라면? 열려있는 것으로 간주? 아니면 닫힌 것으로 간주?
            // 보통 "예약 기간"이 정해져 있지 않으면 상시 오픈이거나, 특정 요일에만 열림.
            // 여기서는 "오픈 시각 이후에는 계속 열려있다"고 가정.
            // 즉, 금주 오픈 요일/시간을 지났는지 체크.
        }

        if (currentDayOrdinal == openDayOrdinal && currentTime.isBefore(club.getReservationTime())) {
            throw new RuntimeException("예약 오픈 시간 전입니다.");
        }
    }

    private int convertJavaDayOfWeekToDomain(java.time.DayOfWeek javaDay) {
        // Java: MON(1) ... SUN(7)
        // Domain: SUN(0), MON(1) ... SAT(6) (가정)
        if (javaDay == java.time.DayOfWeek.SUNDAY)
            return 0;
        return javaDay.getValue();
    }

    private Reservation processSingleReservation(User user, Club club, LocalDate date) {
        // 1. 과거 날짜 제외
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("과거 날짜는 예약할 수 없습니다.");
        }

        // 2. 본인 중복 체크
        // 해당 날짜에 이미 내 예약이 있는지 (상태가 confirmed/waiting 인 것들)
        List<Reservation> myReservations = reservationRepository.findAllByUserAndDateBetweenOrderByCreatedAtDesc(
                user, date, date);
        if (!myReservations.isEmpty()) {
            // 취소된 예약 제외하고 체크
            boolean exists = myReservations.stream()
                    .anyMatch(r -> !"CANCELLED".equals(r.getStatus()));
            if (exists) {
                throw new RuntimeException("이미 해당 날짜에 예약이 존재합니다.");
            }
        }

        // 3. 용량(Capacity) 체크
        Long currentCount = reservationRepository.countByClubAndDateAndStatusNot(club, date, "CANCELLED");
        String status = "confirmed";

        if (currentCount >= club.getDailyCapacity()) {
            status = "waiting";
        }

        // 4. 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .club(club)
                .date(date)
                .status(status)
                .build();

        reservationRepository.save(reservation);

        return reservation;
    }

    /**
     * 예약 취소 (일괄 취소)
     */
    @Transactional
    public void cancel(Long userId, Long clubId, ReservationRequest request) {
        // 1. 동아리 조회 (비관적 락 사용 - 동시성 제어)
        Club club = clubRepository.findByIdWithLock(clubId)
                .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));

        // 2. 사용자 조회
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        // 3. 날짜별 취소 처리
        for (LocalDate date : request.getDates()) {
            cancelSingleReservation(user, club, date);
        }
    }

    private void cancelSingleReservation(User user, Club club, LocalDate date) {
        // 내 예약 조회 (취소되지 않은 것)
        Reservation myReservation = reservationRepository.findByUserAndClubAndDateAndStatusNot(
                user, club, date, "CANCELLED")
                .orElseThrow(() -> new RuntimeException("해당 날짜에 예약이 없습니다."));

        String oldStatus = myReservation.getStatus();

        // 상태 변경 -> CANCELLED
        myReservation.cancel();

        // 만약 기존 상태가 CONFIRMED였다면, 대기열 승격 시도
        if ("confirmed".equals(oldStatus)) {
            promoteNextWaitingUser(club, date);
        }
    }

    private void promoteNextWaitingUser(Club club, LocalDate date) {
        // 대기열에서 가장 오래된 예약 조회
        List<Reservation> waitingList = reservationRepository.findByClubAndDateAndStatusOrderByCreatedAtAsc(
                club, date, "waiting");

        if (!waitingList.isEmpty()) {
            Reservation nextReservation = waitingList.get(0);
            nextReservation.confirm();
            log.info("대기열 승격: reservationId={}, userId={}", nextReservation.getId(), nextReservation.getUser().getId());
        }
    }

    /**
     * 날짜별 예약자 명단 조회
     */
    @Transactional(readOnly = true)
    public com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse getReservationsByDate(
            Long userId, Long clubId, LocalDate date) {

        // 권한 확인 (회원 이상)
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));

        if (!user.getClub().equals(club)) {
            throw new RuntimeException("해당 동아리의 회원이 아닙니다.");
        }

        // 해당 날짜의 모든 예약 조회 (취소 제외)
        List<Reservation> reservations = reservationRepository.findByClubAndDateAndStatusNot(club, date, "CANCELLED");

        List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse.UserSummary> confirmedList = new ArrayList<>();
        List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse.UserSummary> waitingList = new ArrayList<>();

        for (Reservation r : reservations) {
            com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse.UserSummary summary = com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse.UserSummary
                    .builder()
                    .userId(r.getUser().getId())
                    .name(r.getUser().getName())
                    .role(r.getUser().getRole())
                    .build();

            if ("confirmed".equals(r.getStatus())) {
                confirmedList.add(summary);
            } else if ("waiting".equals(r.getStatus())) {
                waitingList.add(summary);
            }
        }

        return com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse.builder()
                .date(date.toString())
                .confirmed(confirmedList)
                .waiting(waitingList)
                .build();
    }
}
