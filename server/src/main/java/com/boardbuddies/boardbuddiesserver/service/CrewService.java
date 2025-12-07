package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.crew.*;
import com.boardbuddies.boardbuddiesserver.repository.CrewRepository;
import com.boardbuddies.boardbuddiesserver.repository.ReservationRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 크루 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    /**
     * 크루 생성
     * 
     * @param userId  생성자 ID (자동으로 PRESIDENT가 됨)
     * @param request 크루 생성 요청
     * @return 생성된 크루 정보
     */
    @Transactional
    public CrewCreateResponse createCrew(Long userId, CrewCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        // 생성자 조회
        User president = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 예약 요일 변환 (String → DayOfWeek, Optional)
        DayOfWeek reservationDay = request.getReservationDay() != null
                ? DayOfWeek.valueOf(request.getReservationDay())
                : null;

        // 예약 시간 변환 (String → LocalTime, Optional)
        LocalTime reservationTime = request.getReservationTime() != null
                ? LocalTime.parse(request.getReservationTime())
                : null;

        // 크루 생성
        Crew crew = Crew.builder()
                .name(request.getCrewName())
                .univ(request.getUniv())
                .reservationDay(reservationDay)
                .reservationTime(reservationTime)
                .status("ACTIVE")
                .crewPIN(request.getCrewPIN())
                .dailyCapacity(request.getDailyCapacity() != null ? request.getDailyCapacity() : 20)
                .build();

        crew = crewRepository.save(crew);

        // 생성자를 PRESIDENT로 설정
        president.joinCrew(crew, Role.PRESIDENT);

        log.info("크루 생성 완료: crewId={}, presidentId={}", crew.getId(), userId);

        // 운영진 목록 수집 (회장 + 매니저)
        Set<User> allManagers = new HashSet<>();
        allManagers.add(president); // 회장 추가

        // 운영진 설정 (manager_list가 있는 경우)
        if (request.getManagerList() != null && !request.getManagerList().isEmpty()) {
            Set<User> managers = assignManagers(crew, president, request.getManagerList());
            allManagers.addAll(managers); // 매니저 추가
        }

        return CrewCreateResponse.from(crew, allManagers);
    }

    /**
     * 모든 크루 목록 조회
     * 
     * @return 크루 목록
     */
    @Transactional(readOnly = true)
    public List<CrewListResponse> getAllCrews() {
        List<Crew> crews = crewRepository.findAll();

        // 모든 회장(PRESIDENT) 조회
        List<User> presidents = userRepository.findAllByRole(Role.PRESIDENT);

        // crewId -> presidentId 맵 생성
        java.util.Map<Long, Long> crewPresidentMap = presidents.stream()
                .filter(user -> user.getCrew() != null)
                .collect(Collectors.toMap(
                        user -> user.getCrew().getId(),
                        User::getId,
                        (existing, replacement) -> existing // 중복 시 기존 값 유지
                ));

        return crews.stream()
                .map(crew -> {
                    Long presidentId = crewPresidentMap.get(crew.getId());
                    return CrewListResponse.from(crew, presidentId);
                })
                .collect(Collectors.toList());
    }

    /**
     * 크루 상세 정보 조회
     * 
     * @param crewId 크루 ID
     * @return 크루 상세 정보
     */
    @Transactional(readOnly = true)
    public CrewDetailResponse getCrewDetail(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID must not be null");
        }
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("해당 크루를 찾을 수 없습니다."));

        return CrewDetailResponse.from(crew);
    }

    /**
     * 크루 정보 수정
     * 
     * @param userId  현재 로그인한 사용자 ID
     * @param crewId  크루 ID
     * @param request 수정 요청
     */
    @Transactional
    public void updateCrew(Long userId, Long crewId, CrewUpdateRequest request) {
        if (userId == null || crewId == null) {
            throw new IllegalArgumentException("User ID and Crew ID must not be null");
        }
        // 크루 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("해당 크루를 찾을 수 없습니다."));

        // 사용자 조회 및 권한 확인 (MANAGER 또는 PRESIDENT)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!user.getCrew().equals(crew) ||
                (user.getRole() != Role.MANAGER && user.getRole() != Role.PRESIDENT)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 크루명 수정
        if (request.getCrewName() != null && !request.getCrewName().isBlank()) {
            crew.updateName(request.getCrewName());
        }

        // PIN 수정
        if (request.getCrewPIN() != null) {
            crew.updateCrewPIN(request.getCrewPIN());
        }

        // 예약 요일 수정
        if (request.getReservationDay() != null && !request.getReservationDay().isBlank()) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(request.getReservationDay());
            crew.updateReservationDay(dayOfWeek);
        }

        // 예약 시간 수정
        if (request.getReservationTime() != null && !request.getReservationTime().isBlank()) {
            LocalTime time = LocalTime.parse(request.getReservationTime());
            crew.updateReservationTime(time);
        }

        // 일별 수용 인원 수정
        if (request.getDailyCapacity() != null) {
            crew.updateDailyCapacity(request.getDailyCapacity());
            // 수용 인원 증가 시 대기열 승격 시도
            reservationService.promoteWaitingUsers(crew);
        }

        // 운영진 목록 수정
        if (request.getManagerList() != null) {
            // 기존 운영진 제거 (PRESIDENT 제외)
            List<User> existingManagers = userRepository.findAllByCrewAndRole(crew, Role.MANAGER);

            for (User manager : existingManagers) {
                manager.leaveCrew();
            }

            // 회장 찾기
            User president = userRepository.findByCrewAndRole(crew, Role.PRESIDENT)
                    .orElseThrow(() -> new RuntimeException("크루 회장을 찾을 수 없습니다."));

            // 새로운 운영진 지정
            assignManagers(crew, president, request.getManagerList());
        }

        log.info("크루 정보 수정 완료: crewId={}, updatedBy={}", crewId, userId);
    }

    /**
     * 크루 삭제
     * 
     * @param userId  현재 로그인한 사용자 ID
     * @param crewId  크루 ID
     * @param request 삭제 요청 (PIN 확인용)
     */
    @Transactional
    public void deleteCrew(Long userId, Long crewId, CrewDeleteRequest request) {
        if (userId == null || crewId == null) {
            throw new IllegalArgumentException("User ID and Crew ID must not be null");
        }
        // 크루 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("해당 크루를 찾을 수 없습니다."));

        // 사용자 조회 및 권한 확인 (PRESIDENT만 가능)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!user.getCrew().equals(crew) || user.getRole() != Role.PRESIDENT) {
            throw new RuntimeException("크루 삭제 권한이 없습니다.");
        }

        // PIN 확인
        if (!crew.verifyPIN(request.getCrewPIN())) {
            throw new RuntimeException("PIN이 일치하지 않습니다.");
        }

        // 크루 소속 모든 회원 조회 (최적화)
        List<User> members = userRepository.findAllByCrew(crew);

        for (User member : members) {
            member.leaveCrew();
        }

        // 크루 삭제
        crewRepository.delete(crew);

        log.info("크루 삭제 완료: crewId={}, deletedBy={}", crewId, userId);
    }

    /**
     * 운영진 지정
     * 
     * @param crew              크루
     * @param president         크루 회장 (PRESIDENT로 이미 설정됨, 중복 처리 방지용)
     * @param managerStudentIds 운영진 학번 리스트
     * @return 지정된 운영진 목록
     */
    private Set<User> assignManagers(Crew crew, User president, Set<String> managerStudentIds) {
        Set<User> managers = new HashSet<>();

        // 회장 본인이 manager_list에 있는지 확인
        Set<String> studentIdsToProcess = managerStudentIds;
        if (managerStudentIds.contains(president.getStudentId())) {
            log.info("회장 본인(학번: {})은 manager_list에서 제외됩니다. (이미 PRESIDENT로 설정됨)",
                    president.getStudentId());
            // 회장이 포함된 경우에만 새로운 Set 생성
            studentIdsToProcess = new HashSet<>(managerStudentIds);
            studentIdsToProcess.remove(president.getStudentId());
        }

        for (String studentId : studentIdsToProcess) {
            // 학교 + 학번으로 사용자 조회 (유니크)
            User user = userRepository.findBySchoolAndStudentId(crew.getUniv(), studentId)
                    .orElseThrow(() -> new RuntimeException(
                            crew.getUniv() + " 소속 학번 " + studentId + "에 해당하는 사용자를 찾을 수 없습니다."));

            // 회원가입 완료 여부 확인
            if (!user.getIsRegistered()) {
                log.warn("사용자 {}는 회원가입이 완료되지 않았습니다.", studentId);
                throw new RuntimeException("학번 " + studentId + "는 회원가입이 완료되지 않았습니다. 회원가입 후 운영진으로 지정할 수 있습니다.");
            }

            // 운영진으로 지정 (Role.MANAGER)
            user.joinCrew(crew, Role.MANAGER);
            managers.add(user);

            log.info("운영진 지정 완료: crewId={}, userId={}, school={}, studentId={}, role=MANAGER",
                    crew.getId(), user.getId(), crew.getUniv(), studentId);
        }

        return managers;
    }

    /**
     * 월별 크루 달력 조회
     * 
     * @param userId 현재 로그인한 사용자 ID
     * @param crewId 크루 ID
     * @param year   년도
     * @param month  월
     * @return 크루 달력 응답 리스트
     */
    @Transactional(readOnly = true)
    public CrewCalendarWrapperResponse getCrewCalendar(Long userId, Long crewId, int year, int month,
            boolean showMySchedule) {
        // 크루 및 사용자 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("해당 크루를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 조회 기간 설정 (해당 월의 1일 ~ 마지막 날)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 1. 일별 예약 수 집계 (DB에서 Count만 조회)
        List<DailyReservationCount> dailyCounts = reservationRepository.findDailyCountsByCrewAndDateBetween(
                crew, startDate, endDate);

        // 날짜별 Count 매핑
        java.util.Map<LocalDate, Long> countMap = dailyCounts.stream()
                .collect(Collectors.toMap(DailyReservationCount::getDate, DailyReservationCount::getCount));

        List<CrewCalendarResponse> calendarResponses = new java.util.ArrayList<>();

        // 2. 1일부터 말일까지 응답 생성
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long countLong = countMap.getOrDefault(date, 0L);
            int count = countLong.intValue();

            // 혼잡도 상태 결정
            String occupancyStatus;
            if (count < 5) {
                occupancyStatus = "LOW";
            } else if (count < 10) {
                occupancyStatus = "MEDIUM";
            } else {
                occupancyStatus = "HIGH";
            }

            calendarResponses.add(CrewCalendarResponse.builder()
                    .date(date)
                    .occupancyStatus(occupancyStatus)
                    .build());
        }

        // 3. 내 예약 정보 조회 (옵션)
        List<CrewMyMonthlyReservationResponse> myReservations = null;
        if (showMySchedule) {
            myReservations = getMyMonthlyReservations(userId, crewId, year, month);
        }

        return CrewCalendarWrapperResponse.builder()
                .calendar(calendarResponses)
                .myReservations(myReservations)
                .build();
    }

    /**
     * 내 월별 예약 내역 조회
     * 
     * @param userId 사용자 ID
     * @param crewId 크루 ID
     * @param year   년도
     * @param month  월
     * @return 내 예약 목록 (날짜, 상태)
     */
    @Transactional(readOnly = true)
    public List<CrewMyMonthlyReservationResponse> getMyMonthlyReservations(Long userId, Long crewId, int year,
            int month) {
        // 크루 및 사용자 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new RuntimeException("해당 크루를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 조회 기간 설정 (해당 월의 1일 ~ 마지막 날)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 내 예약 정보 조회
        List<Reservation> myReservations = reservationRepository.findAllByCrewAndUserAndDateBetweenAndStatusNot(
                crew, user, startDate, endDate, "CANCELLED");

        return myReservations.stream()
                .map(r -> CrewMyMonthlyReservationResponse.builder()
                        .date(r.getDate())
                        .status("confirmed".equalsIgnoreCase(r.getStatus()) ? "CONFIRMED" : "WAITING")
                        .build())
                .collect(Collectors.toList());
    }
}
