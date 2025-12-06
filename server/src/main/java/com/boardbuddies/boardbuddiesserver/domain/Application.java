package com.boardbuddies.boardbuddiesserver.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 크루 가입 신청 엔티티
 */
@Entity
@Table(name = "application", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "crew_id", "status" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신청자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 신청한 크루
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    /**
     * 신청 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberStatus status = MemberStatus.PENDING;

    /**
     * 신청 시간
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 상태 변경 시간
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 처리 시간 (승인/거절 시간)
     */
    private LocalDateTime processedAt;

    // === 비즈니스 메서드 ===

    /**
     * 신청 승인
     */
    public void approve() {
        this.status = MemberStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 신청 거절
     */
    public void reject() {
        this.status = MemberStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }
}
