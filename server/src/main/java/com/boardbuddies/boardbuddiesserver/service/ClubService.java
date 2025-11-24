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
        
        // 운영진 설정 (manager_list가 있는 경우)
        if (request.getManagerList() != null && !request.getManagerList().isEmpty()) {
            assignManagers(club, request.getManagerList());
        }
        
        return ClubCreateResponse.from(club);
    }
    
    /**
     * 운영진 지정
     * 
     * @param club 동아리
     * @param managerStudentIds 운영진 학번 리스트
     */
    private void assignManagers(Club club, Set<String> managerStudentIds) {
        for (String studentId : managerStudentIds) {
            User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("학번 " + studentId + "에 해당하는 사용자를 찾을 수 없습니다."));
            
            // 소속 대학이 일치하는지 확인
            if (!user.getSchool().equals(club.getUniv())) {
                log.warn("사용자 {}의 소속 대학({})이 동아리 대학({})과 일치하지 않습니다.", 
                    studentId, user.getSchool(), club.getUniv());
                throw new RuntimeException("학번 " + studentId + "의 소속 대학이 동아리 대학과 일치하지 않습니다.");
            }
            
            // 운영진으로 지정 (Role.MANAGER)
            user.joinClub(club, Role.MANAGER);
            
            log.info("운영진 지정 완료: clubId={}, userId={}, studentId={}, role=MANAGER", 
                club.getId(), user.getId(), studentId);
        }
    }
}

