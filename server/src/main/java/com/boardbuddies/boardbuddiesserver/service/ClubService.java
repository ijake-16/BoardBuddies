package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubCreateRequest;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubCreateResponse;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubListResponse;
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

