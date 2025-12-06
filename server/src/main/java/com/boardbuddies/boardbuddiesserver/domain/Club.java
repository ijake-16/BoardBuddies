package com.boardbuddies.boardbuddiesserver.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 동아리 엔티티
 */
@Entity
@Table(name = "club")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 동아리명
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 대학교명
     */
    @Column(nullable = false, length = 50)
    private String univ;

    /**
     * 예약 요일 (SUNDAY(0) ~ SATURDAY(6))
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10)
    private DayOfWeek reservationDay;

    /**
     * 예약 시간 (HH:MM:SS)
     */
    @Column(nullable = true)
    private LocalTime reservationTime;

    /**
     * 동아리 상태 (ACTIVE, INACTIVE 등)
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * 동아리 PIN (가입 신청 시 필요)
     */
    @Column(nullable = false)
    private Integer clubPIN;

    /**
     * 일별 수용 인원 (기본값 20)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer dailyCapacity = 20;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * PIN 일치 여부 확인
     */
    public boolean verifyPIN(Integer pin) {
        return this.clubPIN.equals(pin);
    }

    /**
     * 동아리 이름 수정
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * 동아리 PIN 수정
     */
    public void updateClubPIN(Integer clubPIN) {
        this.clubPIN = clubPIN;
    }

    /**
     * 예약 요일 수정
     */
    public void updateReservationDay(DayOfWeek reservationDay) {
        this.reservationDay = reservationDay;
    }

    /**
     * 예약 시간 수정
     */
    public void updateReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    /**
     * 일별 수용 인원 수정
     */
    public void updateDailyCapacity(Integer dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }
}
