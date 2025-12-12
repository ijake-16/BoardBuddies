package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMultiResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationRequest;
import com.boardbuddies.boardbuddiesserver.repository.CrewRepository;
import com.boardbuddies.boardbuddiesserver.repository.ReservationRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

    private final CrewRepository crewRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    /**
     * 시즌방 예약 (일괄 신청)
     */
    public ReservationMultiResponse reserve(Long userId, Long crewId, ReservationRequest request) {
        if (userId == null || crewId == null) {
            throw new IllegalArgumentException("User ID and Crew ID must not be null");
        }
        // 1. 크루 조회 (DB 락 제거 -> 일반 조회)
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("크루를 찾을 수 없습니다."));

        // 2. 사용자 조회 및 권한 검증
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        if (!user.getCrew().equals(crew)) {
            throw new RuntimeException("해당 크루의 회원이 아닙니다.");
        }
        if (user.getRole() == Role.GUEST || !user.getIsRegistered()) {
            throw new RuntimeException("승인된 회원만 예약할 수 있습니다.");
        }

        // 3. 오픈 시각 검증
        validateOpenTime(crew, request.getDates());

        List<ReservationMultiResponse.ReservationResult> results = new ArrayList<>();
        int succeeded = 0;
        int failed = 0;

        // 4. 날짜별 처리
        for (LocalDate date : request.getDates()) {
            try {
                // 개별 날짜 검증 및 예약 (분산 락 적용)
                Reservation reservation = processSingleReservationWithLock(user, crew, date);

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

                // 락 획득 실패 시 (너무 많은 요청 몰림)
                else if (reason.contains("잠시 후"))
                    status = "retry_lazily";

                results.add(ReservationMultiResponse.ReservationResult.builder()
                        .date(date)
                        .status(status)
                        .build());
                failed++;
            }
        }

        // 5. 응답 생성
        return ReservationMultiResponse.builder()
                .crewId(crew.getId())
                .results(results)
                .summary(ReservationMultiResponse.ReservationSummary.builder()
                        .requested(request.getDates().size())
                        .succeeded(succeeded)
                        .failed(failed)
                        .build())
                .build();
    }

    private Reservation processSingleReservationWithLock(User user, Crew crew, LocalDate date) {
        String lockKey = "lock:reservation:" + crew.getId() + ":" + date;
        // 락 획득 시도
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // waitTime: 락 획득 대기 시간 (5초), leaseTime: 락 보유 시간 (3초 - 자동 해제)
            boolean available = lock.tryLock(5, 3, java.util.concurrent.TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("접속량이 많아 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요.");
            }

            // 트랜잭션 템플릿을 사용하여 트랜잭션 보장
            return transactionTemplate.execute(status -> processSingleReservationLogic(user, crew, date));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
            throw new RuntimeException("서버 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    protected Reservation processSingleReservationLogic(User user, Crew crew, LocalDate date) {
        // 1. 과거 날짜 제외
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("과거 날짜는 예약할 수 없습니다.");
        }

        // 2. 본인 중복 체크
        List<Reservation> myReservations = reservationRepository.findAllByUserAndDateBetweenOrderByCreatedAtDesc(
                user, date, date);
        if (!myReservations.isEmpty()) {
            boolean exists = myReservations.stream()
                    .anyMatch(r -> !"CANCELLED".equals(r.getStatus()));
            if (exists) {
                throw new RuntimeException("이미 해당 날짜에 예약이 존재합니다.");
            }
        }

        // 3. 용량(Capacity) 체크
        Long currentCount = reservationRepository.countByCrewAndDateAndStatusNot(crew, date, "CANCELLED");
        String status = "confirmed";

        if (currentCount >= crew.getDailyCapacity()) {
            status = "waiting";
        }

        // 4. 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .crew(crew)
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
    public void cancel(Long userId, Long crewId, ReservationRequest request) {
        if (userId == null || crewId == null) {
            throw new IllegalArgumentException("User ID and Crew ID must not be null");
        }
        // 1. 크루 조회 (비관적 락 사용 - 동시성 제어)
        Crew crew = crewRepository.findByIdWithLock(crewId)
                .orElseThrow(() -> new RuntimeException("크루를 찾을 수 없습니다."));

        // 2. 사용자 조회
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        // 3. 날짜별 취소 처리
        for (LocalDate date : request.getDates()) {
            cancelSingleReservation(user, crew, date);
        }
    }

    private void cancelSingleReservation(User user, Crew crew, LocalDate date) {
        // 내 예약 조회 (취소되지 않은 것)
        Reservation myReservation = reservationRepository.findByUserAndCrewAndDateAndStatusNot(
                user, crew, date, "CANCELLED")
                .orElseThrow(() -> new RuntimeException("해당 날짜에 예약이 없습니다."));

        String oldStatus = myReservation.getStatus();

        // 상태 변경 -> CANCELLED
        myReservation.cancel();

        // 만약 기존 상태가 CONFIRMED였다면, 대기열 승격 시도
        if ("confirmed".equals(oldStatus)) {
            promoteNextWaitingUser(crew, date);
        }
    }

    private void promoteNextWaitingUser(Crew crew, LocalDate date) {
        // 대기열에서 가장 오래된 예약 조회
        List<Reservation> waitingList = reservationRepository.findByCrewAndDateAndStatusOrderByCreatedAtAsc(
                crew, date, "waiting");

        if (!waitingList.isEmpty()) {
            Reservation nextReservation = waitingList.get(0);
            nextReservation.confirm();
            log.info("대기열 승격: reservationId={}, userId={}", nextReservation.getId(), nextReservation.getUser().getId());
        }
    }

    /**
     * 크루 수용 인원 증가 시 대기열 승격 처리
     */
    @Transactional
    public void promoteWaitingUsers(Crew crew) {
        // 대기 중인 모든 예약 조회
        List<Reservation> waitingReservations = reservationRepository.findByCrewAndStatus(crew, "waiting");

        // 날짜별로 그룹화
        List<LocalDate> dates = waitingReservations.stream()
                .map(Reservation::getDate)
                .distinct()
                .toList();

        for (LocalDate date : dates) {
            // 해당 날짜의 현재 확정된 예약 수 조회
            long confirmedCount = reservationRepository.findByCrewAndDateAndStatusNot(crew, date, "CANCELLED").stream()
                    .filter(r -> "confirmed".equals(r.getStatus()))
                    .count();

            // 남은 자리 계산
            long remaining = crew.getDailyCapacity() - confirmedCount;

            if (remaining > 0) {
                // 대기열에서 오래된 순으로 조회
                List<Reservation> waitingList = reservationRepository.findByCrewAndDateAndStatusOrderByCreatedAtAsc(
                        crew, date, "waiting");

                // 남은 자리만큼 승격
                for (int i = 0; i < Math.min(remaining, waitingList.size()); i++) {
                    Reservation reservation = waitingList.get(i);
                    reservation.confirm();
                    log.info("대기열 승격 (용량 증가): reservationId={}, userId={}", reservation.getId(),
                            reservation.getUser().getId());
                }
            }
        }
    }

    /**
     * 날짜별 예약자 명단 조회
     */
    @Transactional(readOnly = true)
    public com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse getReservationsByDate(
            Long userId, Long crewId, LocalDate date) {
        if (userId == null || crewId == null) {
            throw new IllegalArgumentException("User ID and Crew ID must not be null");
        }

        // 권한 확인 (회원 이상)
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("크루를 찾을 수 없습니다."));

        if (!user.getCrew().equals(crew)) {
            throw new RuntimeException("해당 크루의 회원이 아닙니다.");
        }

        // 해당 날짜의 모든 예약 조회 (취소 제외)
        List<Reservation> reservations = reservationRepository.findByCrewAndDateAndStatusNot(crew, date, "CANCELLED");

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
            Long userId, Long crewId, LocalDate date) {
        if (userId == null || crewId == null) {
            throw new IllegalArgumentException("User ID and Crew ID must not be null");
        }

        // 1. 사용자 및 크루 조회
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("크루를 찾을 수 없습니다."));

        if (!user.getCrew().equals(crew)) {
            throw new RuntimeException("해당 크루의 회원이 아닙니다.");
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
        if (crew.getReservationDay() != null && crew.getReservationTime() != null) {
            LocalDateTime openDateTime = getOpenDateTime(date, crew);
            if (now.isBefore(openDateTime)) {
                status = "closed";
            }
        }

        List<Reservation> reservations = reservationRepository.findAllByCrewAndDateAndStatusNotOrderByCreatedAtAsc(crew,
                date, "CANCELLED");

        int booked = 0;
        List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMemberResponse> memberList = new ArrayList<>();
        List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMemberResponse> waitingMemberList = new ArrayList<>();
        com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.MyReservationInfo myReservationInfo = null;

        for (Reservation r : reservations) {
            com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMemberResponse memberResponse = com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMemberResponse
                    .builder()
                    .userId(r.getUser().getId())
                    .name(r.getUser().getName())
                    .profileImageUrl(r.getUser().getProfileImageUrl())
                    .build();

            if ("confirmed".equals(r.getStatus())) {
                booked++;
                memberList.add(memberResponse);
            } else if ("waiting".equals(r.getStatus())) {
                waitingMemberList.add(memberResponse);
            }

            // 내 예약 확인
            if (r.getUser().getId().equals(userId)) {
                myReservationInfo = com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.MyReservationInfo
                        .builder()
                        .reservationId(r.getId())
                        .build();
            }
        }

        return com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse.builder().date(date)
                .status(status).booked(booked).waitingCount(waitingMemberList.size()).capacity(crew.getDailyCapacity())
                .memberList(memberList).waitingMemberList(waitingMemberList).myReservation(myReservationInfo).build();
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
    private LocalDateTime getOpenDateTime(LocalDate targetDate, Crew crew) {
        // 1. 해당 날짜가 속한 주의 월요일 구하기
        LocalDate targetWeekMonday = targetDate
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        // 2. 전 주 월요일 구하기
        LocalDate prevWeekMonday = targetWeekMonday.minusWeeks(1);

        // 3. 전 주의 설정된 요일 구하기
        // Crew의 DayOfWeek는 도메인 Enum이므로 Java DayOfWeek로 변환 필요
        java.time.DayOfWeek javaDayOfWeek = convertDomainDayToJavaDay(crew.getReservationDay());

        // 전 주 월요일부터 해당 요일까지 이동
        // (월요일부터 시작하므로, 해당 요일이 월요일이면 +0, 화요일이면 +1 ...)
        // TemporalAdjusters.nextOrSame을 사용하면 편리함
        LocalDate openDate = prevWeekMonday.with(java.time.temporal.TemporalAdjusters.nextOrSame(javaDayOfWeek));

        return LocalDateTime.of(openDate, crew.getReservationTime());
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

    private void validateOpenTime(Crew crew, List<LocalDate> dates) {
        if (crew.getReservationDay() == null || crew.getReservationTime() == null) {
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
            LocalDateTime openDateTime = getOpenDateTime(date, crew);
            if (now.isBefore(openDateTime)) {
                throw new RuntimeException("아직 예약 오픈 시간이 아닙니다. (" + date + " 예약은 " + openDateTime + "에 오픈)");
            }
        }
    }

    /**
     * 내 예약 내역 (이번주 + 다음주) 조회 - 메인 화면용
     */
    @Transactional(readOnly = true)
    public List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationCalendarResponse> getMyCalendarReservations(
            Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이번 주 월요일 ~ 다음 주 일요일
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate start = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate end = start.plusWeeks(2).minusDays(1);

        List<Reservation> reservations = reservationRepository.findAllByUserAndDateBetweenOrderByCreatedAtDesc(user,
                start, end);

        return reservations.stream()
                .filter(r -> !"CANCELLED".equals(r.getStatus()))
                .map(r -> com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationCalendarResponse.builder()
                        .reservationId(r.getId())
                        .date(r.getDate())
                        .status(r.getStatus())
                        .build())
                .toList();
    }
}
