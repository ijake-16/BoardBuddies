package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.club.*;
import com.boardbuddies.boardbuddiesserver.repository.ClubRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 동아리 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClubService {
    
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    
    /**
     * 동아리 생성
     * 
     * @param userId 생성자 ID (자동으로 PRESIDENT가 됨)
     * @param request 동아리 생성 요청
     * @return 생성된 동아리 정보
     */
    @Transactional
    public ClubCreateResponse createClub(Long userId, ClubCreateRequest request) {
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
        
        // 동아리 생성
        Club club = Club.builder()
            .name(request.getClubName())
            .univ(request.getUniv())
            .reservationDay(reservationDay)
            .reservationTime(reservationTime)
            .status("ACTIVE")
            .clubPIN(request.getClubPIN())
            .build();
        
        club = clubRepository.save(club);
        
        // 생성자를 PRESIDENT로 설정
        president.joinClub(club, Role.PRESIDENT);
        
        log.info("동아리 생성 완료: clubId={}, presidentId={}", club.getId(), userId);
        
        // 운영진 목록 수집 (회장 + 매니저)
        Set<User> allManagers = new HashSet<>();
        allManagers.add(president);  // 회장 추가
        
        // 운영진 설정 (manager_list가 있는 경우)
        if (request.getManagerList() != null && !request.getManagerList().isEmpty()) {
            Set<User> managers = assignManagers(club, president, request.getManagerList());
            allManagers.addAll(managers);  // 매니저 추가
        }
        
        return ClubCreateResponse.from(club, allManagers);
    }
    
    /**
     * 모든 동아리 목록 조회
     * 
     * @return 동아리 목록
     */
    @Transactional(readOnly = true)
    public List<ClubListResponse> getAllClubs() {
        List<Club> clubs = clubRepository.findAll();
        
        return clubs.stream()
            .map(club -> {
                // 각 동아리의 회장(PRESIDENT) 찾기
                Long presidentId = userRepository.findByClubAndRole(club, Role.PRESIDENT)
                    .map(User::getId)
                    .orElse(null);
                
                return ClubListResponse.from(club, presidentId);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 동아리 상세 정보 조회
     * 
     * @param clubId 동아리 ID
     * @return 동아리 상세 정보
     */
    @Transactional(readOnly = true)
    public ClubDetailResponse getClubDetail(Long clubId) {
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new RuntimeException("해당 동아리를 찾을 수 없습니다."));
        
        return ClubDetailResponse.from(club);
    }
    
    /**
     * 동아리 정보 수정
     * 
     * @param userId 현재 로그인한 사용자 ID
     * @param clubId 동아리 ID
     * @param request 수정 요청
     */
    @Transactional
    public void updateClub(Long userId, Long clubId, ClubUpdateRequest request) {
        // 동아리 조회
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new RuntimeException("해당 동아리를 찾을 수 없습니다."));
        
        // 사용자 조회 및 권한 확인 (MANAGER 또는 PRESIDENT)
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (!user.getClub().equals(club) || 
            (user.getRole() != Role.MANAGER && user.getRole() != Role.PRESIDENT)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        
        // 동아리명 수정
        if (request.getClubName() != null && !request.getClubName().isBlank()) {
            club.updateName(request.getClubName());
        }
        
        // PIN 수정
        if (request.getClubPIN() != null) {
            club.updateClubPIN(request.getClubPIN());
        }
        
        // 예약 요일 수정
        if (request.getReservationDay() != null && !request.getReservationDay().isBlank()) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(request.getReservationDay());
            club.updateReservationDay(dayOfWeek);
        }
        
        // 예약 시간 수정
        if (request.getReservationTime() != null && !request.getReservationTime().isBlank()) {
            LocalTime time = LocalTime.parse(request.getReservationTime());
            club.updateReservationTime(time);
        }
        
        // 운영진 목록 수정
        if (request.getManagerList() != null) {
            // 기존 운영진 제거 (PRESIDENT 제외)
            List<User> existingManagers = userRepository.findAll().stream()
                .filter(u -> u.getClub() != null && u.getClub().equals(club) && u.getRole() == Role.MANAGER)
                .collect(Collectors.toList());
            
            for (User manager : existingManagers) {
                manager.leaveClub();
            }
            
            // 회장 찾기
            User president = userRepository.findByClubAndRole(club, Role.PRESIDENT)
                .orElseThrow(() -> new RuntimeException("동아리 회장을 찾을 수 없습니다."));
            
            // 새로운 운영진 지정
            assignManagers(club, president, request.getManagerList());
        }
        
        log.info("동아리 정보 수정 완료: clubId={}, updatedBy={}", clubId, userId);
    }
    
    /**
     * 동아리 삭제
     * 
     * @param userId 현재 로그인한 사용자 ID
     * @param clubId 동아리 ID
     * @param request 삭제 요청 (PIN 확인용)
     */
    @Transactional
    public void deleteClub(Long userId, Long clubId, ClubDeleteRequest request) {
        // 동아리 조회
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new RuntimeException("해당 동아리를 찾을 수 없습니다."));
        
        // 사용자 조회 및 권한 확인 (PRESIDENT만 가능)
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (!user.getClub().equals(club) || user.getRole() != Role.PRESIDENT) {
            throw new RuntimeException("동아리 삭제 권한이 없습니다.");
        }
        
        // PIN 확인
        if (!club.verifyPIN(request.getClubPIN())) {
            throw new RuntimeException("PIN이 일치하지 않습니다.");
        }
        
        // 동아리 소속 모든 회원의 club과 role 제거
        List<User> members = userRepository.findAll().stream()
            .filter(u -> u.getClub() != null && u.getClub().equals(club))
            .collect(Collectors.toList());
        
        for (User member : members) {
            member.leaveClub();
        }
        
        // 동아리 삭제
        clubRepository.delete(club);
        
        log.info("동아리 삭제 완료: clubId={}, deletedBy={}", clubId, userId);
    }
    
    /**
     * 운영진 지정
     * 
     * @param club 동아리
     * @param president 동아리 회장 (PRESIDENT로 이미 설정됨, 중복 처리 방지용)
     * @param managerStudentIds 운영진 학번 리스트
     * @return 지정된 운영진 목록
     */
    private Set<User> assignManagers(Club club, User president, Set<String> managerStudentIds) {
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
            User user = userRepository.findBySchoolAndStudentId(club.getUniv(), studentId)
                .orElseThrow(() -> new RuntimeException(
                    club.getUniv() + " 소속 학번 " + studentId + "에 해당하는 사용자를 찾을 수 없습니다."));
            
            // 회원가입 완료 여부 확인
            if (!user.getIsRegistered()) {
                log.warn("사용자 {}는 회원가입이 완료되지 않았습니다.", studentId);
                throw new RuntimeException("학번 " + studentId + "는 회원가입이 완료되지 않았습니다. 회원가입 후 운영진으로 지정할 수 있습니다.");
            }
            
            // 운영진으로 지정 (Role.MANAGER)
            user.joinClub(club, Role.MANAGER);
            managers.add(user);
            
            log.info("운영진 지정 완료: clubId={}, userId={}, school={}, studentId={}, role=MANAGER", 
                club.getId(), user.getId(), club.getUniv(), studentId);
        }
        
        return managers;
    }
}

