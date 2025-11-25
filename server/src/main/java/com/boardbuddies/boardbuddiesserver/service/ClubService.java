package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubCreateRequest;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubCreateResponse;
import com.boardbuddies.boardbuddiesserver.repository.ClubRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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
        
        // 예약 요일 변환 (int → DayOfWeek, Optional)
        DayOfWeek reservationDay = request.getReservationDay() != null 
            ? DayOfWeek.fromValue(request.getReservationDay()) 
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
            Set<User> managers = assignManagers(club, request.getManagerList());
            allManagers.addAll(managers);  // 매니저 추가
        }
        
        return ClubCreateResponse.from(club, allManagers);
    }
    
    /**
     * 운영진 지정
     * 
     * @param club 동아리
     * @param managerStudentIds 운영진 학번 리스트
     * @return 지정된 운영진 목록
     */
    private Set<User> assignManagers(Club club, Set<String> managerStudentIds) {
        Set<User> managers = new HashSet<>();
        
        for (String studentId : managerStudentIds) {
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

