package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.*;
import com.boardbuddies.boardbuddiesserver.dto.club.ApplicationDecisionRequest;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubApplicationRequest;
import com.boardbuddies.boardbuddiesserver.dto.club.ClubApplicationResponse;
import com.boardbuddies.boardbuddiesserver.repository.ApplicationRepository;
import com.boardbuddies.boardbuddiesserver.repository.ClubRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 동아리 가입 신청 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClubApplicationService {
    
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    
    /**
     * 동아리 가입 신청
     * 
     * @param clubId 동아리 ID
     * @param userId 신청자 ID
     * @param request 신청 요청 (PIN 포함)
     */
    @Transactional
    public void applyForClub(Long clubId, Long userId, ClubApplicationRequest request) {
        // 동아리 조회
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));
        
        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // PIN 검증
        if (!club.verifyPIN(request.getClubPIN())) {
            throw new RuntimeException("PIN이 일치하지 않습니다.");
        }
        
        // 이미 신청한 경우 확인 (대기중인 신청만)
        if (applicationRepository.existsByUserAndClubAndStatus(user, club, MemberStatus.PENDING)) {
            throw new RuntimeException("이미 가입 신청이 완료되었습니다.");
        }
        
        // 신청서 생성
        Application application = Application.builder()
            .user(user)
            .club(club)
            .status(MemberStatus.PENDING)
            .build();
        
        applicationRepository.save(application);
        
        log.info("동아리 가입 신청 완료: userId={}, clubId={}", userId, clubId);
    }
    
    /**
     * 동아리 가입 신청 목록 조회 (운영진용)
     * 
     * @param clubId 동아리 ID
     * @param managerId 조회하는 운영진 ID
     * @return 신청 목록
     */
    @Transactional(readOnly = true)
    public List<ClubApplicationResponse> getApplications(Long clubId, Long managerId) {
        // 동아리 조회
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));
        
        // 권한 확인 (운영진 이상만 조회 가능)
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (manager.getRole() != Role.MANAGER && manager.getRole() != Role.PRESIDENT) {
            throw new RuntimeException("가입 신청 목록을 조회할 권한이 없습니다.");
        }
        
        // 신청 목록 조회
        List<Application> applications = applicationRepository.findByClubOrderByCreatedAtDesc(club);
        
        return applications.stream()
            .map(ClubApplicationResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 동아리 가입 신청 승인/거절 (운영진용)
     * 
     * @param clubId 동아리 ID
     * @param applicationId 신청 ID
     * @param managerId 처리하는 운영진 ID
     * @param request 승인/거절 결정
     */
    @Transactional
    public void processApplication(Long clubId, Long applicationId, Long managerId, 
                                   ApplicationDecisionRequest request) {
        // 동아리 조회
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new RuntimeException("동아리를 찾을 수 없습니다."));
        
        // 권한 확인 (운영진 이상만 처리 가능)
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (manager.getRole() != Role.MANAGER && manager.getRole() != Role.PRESIDENT) {
            throw new RuntimeException("가입 승인/거절 권한이 없습니다.");
        }
        
        // 신청서 조회
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));
        
        // 신청서가 해당 동아리의 것인지 확인
        if (!application.getClub().getId().equals(clubId)) {
            throw new RuntimeException("해당 동아리의 신청서가 아닙니다.");
        }
        
        // 이미 처리된 신청인지 확인
        if (application.getStatus() != MemberStatus.PENDING) {
            throw new RuntimeException("이미 처리된 신청입니다.");
        }
        
        User applicant = application.getUser();
        
        if (request.isApproved()) {
            // 승인 처리
            application.approve();
            applicant.joinClub(club, Role.MEMBER); // 기본 역할: MEMBER
            
            log.info("동아리 가입 승인: userId={}, clubId={}, applicationId={}", 
                applicant.getId(), clubId, applicationId);
        } else {
            // 거절 처리
            application.reject();
            
            log.info("동아리 가입 거절: userId={}, clubId={}, applicationId={}", 
                applicant.getId(), clubId, applicationId);
        }
    }
}

