package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Application;
import com.boardbuddies.boardbuddiesserver.domain.Club;
import com.boardbuddies.boardbuddiesserver.domain.MemberStatus;
import com.boardbuddies.boardbuddiesserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 동아리 가입 신청 Repository
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    /**
     * 동아리별 신청 목록 조회 (전체 또는 특정 상태)
     */
    List<Application> findByClubOrderByCreatedAtDesc(Club club);
    
    /**
     * 동아리별 특정 상태의 신청 목록 조회
     */
    List<Application> findByClubAndStatusOrderByCreatedAtDesc(Club club, MemberStatus status);
    
    /**
     * 사용자의 특정 동아리에 대한 대기중인 신청 확인
     */
    Optional<Application> findByUserAndClubAndStatus(User user, Club club, MemberStatus status);
    
    /**
     * 사용자가 특정 동아리에 대기중인 신청이 있는지 확인
     */
    boolean existsByUserAndClubAndStatus(User user, Club club, MemberStatus status);
}

