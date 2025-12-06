package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
import com.boardbuddies.boardbuddiesserver.domain.Role;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.auth.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 소셜 제공자와 소셜 ID로 사용자 조회
     */
    Optional<User> findBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);

    /**
     * 소셜 제공자와 소셜 ID로 사용자 존재 여부 확인
     */
    boolean existsBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);

    /**
     * 학번으로 사용자 조회
     */
    Optional<User> findByStudentId(String studentId);

    /**
     * 학교와 학번으로 사용자 조회 (유니크)
     */
    Optional<User> findBySchoolAndStudentId(String school, String studentId);

    /**
     * 학번으로 사용자 존재 여부 확인 (중복 검증)
     */
    boolean existsByStudentId(String studentId);

    /**
     * 전화번호로 사용자 존재 여부 확인 (중복 검증)
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * 크루와 역할로 사용자 조회 (단건)
     * (예: 크루의 회장 찾기)
     */
    Optional<User> findByCrewAndRole(Crew crew, Role role);

    /**
     * 역할로 모든 사용자 조회
     */
    List<User> findAllByRole(Role role);

    /**
     * 크루로 모든 사용자 조회
     */
    List<User> findAllByCrew(Crew crew);

    /**
     * 크루와 역할로 모든 사용자 조회
     */
    List<User> findAllByCrewAndRole(Crew crew, Role role);
}
