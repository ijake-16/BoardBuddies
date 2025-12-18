package com.boardbuddies.boardbuddiesserver.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    /**
     * 게스트 예약인 경우 게스트 정보 (일반 예약인 경우 null)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status;

    /**
     * 강습 신청 여부 (예약 확정된 날에만 신청 가능)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean teaching = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void confirm() {
        this.status = "confirmed";
    }

    public void applyTeaching() {
        if (!"confirmed".equals(this.status)) {
            throw new IllegalStateException("예약이 확정된 경우에만 강습을 신청할 수 있습니다.");
        }
        this.teaching = true;
    }

    public void cancelTeaching() {
        this.teaching = false;
    }
}
