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
        validateOpenTime(club, request.getDates());

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

    /**
     * 날짜별 예약 상세 조회 (단건)
     */
    @Transactional(readOnly = true)
    public com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse getDayReservationDetail(
            Long userId, Long clubId, LocalDate date) {

        // 1. 사용자 및 동아리 조회
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));

        if (!user.getClub().equals(club)) {
            throw new RuntimeException("해당 동아리의 회원이 아닙니다.");
        }

        // 2. 상태 결정 (open/closed)
        String status = "open";
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 2-1. 마감 시간 체크 (예약일 다음날 새벽 2시까지)
        LocalDateTime deadline = getDeadline(date);
        if (now.isAfter(deadline)) {
            status = "closed";
        }

        // 2-2. 오픈 시간 체크
        if (club.getReservationDay() != null && club.getReservationTime() != null) {
            LocalDateTime openDateTime = getOpenDateTime(date, club);
            if (now.isBefore(openDateTime)) {
                status = "closed";
            }
        }

        // 3. 예약 목록 조회
        List<Reservation> reservations = reservationRepository.findByClubAndDateAndStatusNot(club, date, "CANCELLED");

        int booked = 0;
        List<String> memberList = new ArrayList<>();
        List<String> waitingMemberList = new ArrayList<>();
        com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.MyReservationInfo myReservationInfo = com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.MyReservationInfo
                .builder()
                .exists(false)
                .build();

        for (Reservation r : reservations) {
            String memberName = r.getUser().getSchool() + " " + r.getUser().getName();

            if ("confirmed".equals(r.getStatus())) {
                booked++;
                memberList.add(memberName);
            } else if ("waiting".equals(r.getStatus())) {
                waitingMemberList.add(memberName);
            }

            // 내 예약 확인
            if (r.getUser().getId().equals(userId)) {
                myReservationInfo = com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.MyReservationInfo
                        .builder()
                        .exists(true)
                        .reservationId(r.getId())
                        .createdAt(r.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")))
                        .build();
            }
        }

        int remaining = Math.max(0, club.getDailyCapacity() - booked);

        return com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.builder()
                .date(date)
                .status(status)
                .booked(booked)
                .remaining(remaining)
                .memberList(memberList)
                .waitingMemberList(waitingMemberList)
                .myReservation(myReservationInfo)
                .build();
    }

    /**
     * 예약 마감 시간 계산
     * 해당 날짜의 다음날 새벽 2시
     */
    private LocalDateTime getDeadline(LocalDate date) {
        return date.plusDays(1).atTime(2, 0);
    }

    /**
     * 예약 오픈 시간 계산
     * 해당 날짜가 속한 주의 전 주(Previous Week)의 설정된 요일/시간
     * (주의 시작은 월요일 기준)
     */
    private LocalDateTime getOpenDateTime(LocalDate targetDate, Club club) {
        // 1. 해당 날짜가 속한 주의 월요일 구하기
        LocalDate targetWeekMonday = targetDate
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        // 2. 전 주 월요일 구하기
        LocalDate prevWeekMonday = targetWeekMonday.minusWeeks(1);

        // 3. 전 주의 설정된 요일 구하기
        // Club의 DayOfWeek는 도메인 Enum이므로 Java DayOfWeek로 변환 필요
        java.time.DayOfWeek javaDayOfWeek = convertDomainDayToJavaDay(club.getReservationDay());

        // 전 주 월요일부터 해당 요일까지 이동
        // (월요일부터 시작하므로, 해당 요일이 월요일이면 +0, 화요일이면 +1 ...)
        // TemporalAdjusters.nextOrSame을 사용하면 편리함
        LocalDate openDate = prevWeekMonday.with(java.time.temporal.TemporalAdjusters.nextOrSame(javaDayOfWeek));

        return LocalDateTime.of(openDate, club.getReservationTime());
    }

    private java.time.DayOfWeek convertDomainDayToJavaDay(DayOfWeek domainDay) {
        switch (domainDay) {
            case MONDAY:
                return java.time.DayOfWeek.MONDAY;
            case TUESDAY:
                return java.time.DayOfWeek.TUESDAY;
            case WEDNESDAY:
                return java.time.DayOfWeek.WEDNESDAY;
            case THURSDAY:
                return java.time.DayOfWeek.THURSDAY;
            case FRIDAY:
                return java.time.DayOfWeek.FRIDAY;
            case SATURDAY:
                return java.time.DayOfWeek.SATURDAY;
            case SUNDAY:
                return java.time.DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid DayOfWeek: " + domainDay);
        }
    }

    private void validateOpenTime(Club club, List<LocalDate> dates) {
        if (club.getReservationDay() == null || club.getReservationTime() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        for (LocalDate date : dates) {
            // 1. 마감 시간 체크
            LocalDateTime deadline = getDeadline(date);
            if (now.isAfter(deadline)) {
                throw new RuntimeException("예약 마감 시간이 지났습니다. (" + date + ")");
            }

            // 2. 오픈 시간 체크
            LocalDateTime openDateTime = getOpenDateTime(date, club);
            if (now.isBefore(openDateTime)) {
                throw new RuntimeException("아직 예약 오픈 시간이 아닙니다. (" + date + " 예약은 " + openDateTime + "에 오픈)");
            }
        }
    }
}
