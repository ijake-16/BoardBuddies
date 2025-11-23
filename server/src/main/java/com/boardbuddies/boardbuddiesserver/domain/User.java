package com.boardbuddies.boardbuddiesserver.domain;

import com.boardbuddies.boardbuddiesserver.dto.auth.SocialProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // === 소셜 로그인 정보 ===
    
    /**
     * 소셜 제공자 (KAKAO, NAVER)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider socialProvider;
    
    /**
     * 소셜 고유 ID
     */
    @Column(nullable = false, unique = true, length = 100)
    private String socialId;
    
    /**
     * 소셜 이메일
     */
    @Column(length = 100)
    private String email;
    
    /**
     * 소셜 프로필 이미지 URL
     */
    @Column(length = 500)
    private String profileImageUrl;
    
    // === 추가 입력 정보 ===
    
    /**
     * 이름
     */
    @Column(length = 50)
    private String name;
    
    /**
     * 생년월일
     */
    private LocalDate birthDate;
    
    /**
     * 소속학교
     */
    @Column(length = 100)
    private String school;
    
    /**
     * 학번
     */
    @Column(length = 20)
    private String studentId;
    
    /**
     * 성별 (MALE, FEMALE)
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;
    
    /**
     * 전화번호
     */
    @Column(length = 20)
    private String phoneNumber;
    
    // === 계정 상태 ===
    
    /**
     * 회원가입 완료 여부
     * false: 소셜 로그인만 완료 (추가 정보 입력 대기)
     * true: 회원가입 완료
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRegistered = false;
    
    /**
     * 리프레시 토큰 (로그인 유지용)
     */
    @Column(length = 500)
    private String refreshToken;
    
    // === 타임스탬프 ===
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // === 비즈니스 메서드 ===
    
    /**
     * 회원가입 완료 처리
     */
    public void completeRegistration(String name, LocalDate birthDate, String school, 
                                     String studentId, Gender gender, String phoneNumber) {
        this.name = name;
        this.birthDate = birthDate;
        this.school = school;
        this.studentId = studentId;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.isRegistered = true;
    }
    
    /**
     * 리프레시 토큰 업데이트
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    /**
     * 프로필 이미지 업데이트
     */
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

