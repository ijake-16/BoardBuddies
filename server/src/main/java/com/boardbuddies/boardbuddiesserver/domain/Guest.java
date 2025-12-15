package com.boardbuddies.boardbuddiesserver.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 게스트 엔티티
 * 부원이 등록한 게스트 정보를 저장합니다.
 */
@Entity
@Table(name = "guests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게스트 이름
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 게스트 전화번호
     */
    @Column(nullable = false, length = 20)
    private String phoneNumber;

    /**
     * 게스트 학교/소속
     */
    @Column(length = 100)
    private String school;

    /**
     * 게스트를 등록한 부원 (User)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User registeredBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

