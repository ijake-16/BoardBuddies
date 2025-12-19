package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Application;
import com.boardbuddies.boardbuddiesserver.domain.Crew;
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
     * 크루별 신청 목록 조회 (전체 또는 특정 상태)
     */
    List<Application> findByCrewOrderByCreatedAtDesc(Crew crew);

    /**
     * 크루별 특정 상태의 신청 목록 조회
     */
    List<Application> findByCrewAndStatusOrderByCreatedAtDesc(Crew crew, MemberStatus status);

    /**
     * 사용자의 특정 크루에 대한 대기중인 신청 확인
     */
    Optional<Application> findByUserAndCrewAndStatus(User user, Crew crew, MemberStatus status);

    /**
     * 사용자가 특정 크루에 대기중인 신청이 있는지 확인
     */
    boolean existsByUserAndCrewAndStatus(User user, Crew crew, MemberStatus status);

    /**
     * 사용자의 모든 가입 신청 조회 (최신순)
     */
    List<Application> findByUserOrderByCreatedAtDesc(User user);
}
