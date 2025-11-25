package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.auth.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

