package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Reservation;
import com.boardbuddies.boardbuddiesserver.domain.Crew;
import com.boardbuddies.boardbuddiesserver.domain.Guest;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.crew.DailyReservationCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
        List<Reservation> findAllByUserOrderByCreatedAtDesc(User user);

        /**
         * 특정 사용자의 모든 예약 삭제
         */
        void deleteAllByUser(User user);

        List<Reservation> findAllByUserAndDateBetweenOrderByCreatedAtDesc(User user, LocalDate startDate,
                        LocalDate endDate);

        /**
         * 일반 예약만 조회 (게스트 예약 제외) - 전체
         * guest IS NULL을 먼저 체크하여 게스트 예약을 빠르게 제외
         */
        @Query("SELECT r FROM Reservation r " +
                "WHERE r.guest IS NULL AND r.user = :user " +
                "ORDER BY r.createdAt DESC")
        List<Reservation> findAllByUserAndGuestIsNullOrderByCreatedAtDesc(@Param("user") User user);

        /**
         * 일반 예약만 조회 (게스트 예약 제외) - 날짜 범위
         * guest IS NULL을 먼저 체크하여 게스트 예약을 빠르게 제외
         */
        @Query("SELECT r FROM Reservation r " +
                "WHERE r.guest IS NULL AND r.user = :user " +
                "AND r.date BETWEEN :startDate AND :endDate " +
                "ORDER BY r.createdAt DESC")
        List<Reservation> findAllByUserAndGuestIsNullAndDateBetweenOrderByCreatedAtDesc(
                @Param("user") User user,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate);

        /**
         * 일반 예약만 조회 (게스트 예약 제외) - 특정 날짜
         * guest IS NULL을 먼저 체크하여 게스트 예약을 빠르게 제외
         */
        @Query("SELECT r FROM Reservation r " +
                "WHERE r.guest IS NULL AND r.user = :user AND r.crew = :crew " +
                "AND r.date = :date")
        Optional<Reservation> findByUserAndCrewAndDateAndGuestIsNull(
                @Param("user") User user,
                @Param("crew") Crew crew,
                @Param("date") LocalDate date);

        Long countByCrewAndDate(Crew crew, LocalDate date);

        List<Reservation> findByCrewAndDateAndStatusOrderByCreatedAtAsc(Crew crew, LocalDate date, String status);

        Optional<Reservation> findByUserAndCrewAndDate(User user, Crew crew, LocalDate date);

        /**
         * 게스트 예약 조회
         */
        Optional<Reservation> findByGuestAndCrewAndDate(Guest guest, Crew crew, LocalDate date);

        List<Reservation> findByCrewAndDate(Crew crew, LocalDate date);

        /**
         * 크루와 날짜로 예약 조회 (User, Guest Fetch Join으로 N+1 문제 방지)
         */
        @Query("SELECT r FROM Reservation r " +
                "LEFT JOIN FETCH r.user " +
                "LEFT JOIN FETCH r.guest " +
                "WHERE r.crew = :crew AND r.date = :date " +
                "ORDER BY r.createdAt ASC")
        List<Reservation> findByCrewAndDateWithFetch(@Param("crew") Crew crew, @Param("date") LocalDate date);

        List<Reservation> findByCrewAndStatus(Crew crew, String status);

        List<Reservation> findAllByCrewAndDateBetween(Crew crew, LocalDate startDate, LocalDate endDate);

        List<Reservation> findAllByCrewAndDateOrderByCreatedAtAsc(Crew crew, LocalDate date);

        /**
         * 크루와 날짜로 예약 조회 (User, Guest Fetch Join으로 N+1 문제 방지) - 정렬 포함
         */
        @Query("SELECT r FROM Reservation r " +
                "LEFT JOIN FETCH r.user " +
                "LEFT JOIN FETCH r.guest " +
                "WHERE r.crew = :crew AND r.date = :date " +
                "ORDER BY r.createdAt ASC")
        List<Reservation> findAllByCrewAndDateOrderByCreatedAtAscWithFetch(@Param("crew") Crew crew, @Param("date") LocalDate date);

        @Query("SELECT new com.boardbuddies.boardbuddiesserver.dto.crew.DailyReservationCount(r.date, COUNT(r)) " +
                        "FROM Reservation r " +
                        "WHERE r.crew = :crew " +
                        "AND r.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY r.date")
        List<DailyReservationCount> findDailyCountsByCrewAndDateBetween(
                        @Param("crew") Crew crew,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        List<Reservation> findAllByCrewAndUserAndDateBetween(Crew crew, User user, LocalDate startDate,
                        LocalDate endDate);

        /**
         * 일반 예약만 조회 (게스트 예약 제외) - 크루, 사용자, 날짜 범위
         * guest IS NULL을 먼저 체크하여 게스트 예약을 빠르게 제외
         */
        @Query("SELECT r FROM Reservation r " +
                "WHERE r.guest IS NULL AND r.crew = :crew AND r.user = :user " +
                "AND r.date BETWEEN :startDate AND :endDate")
        List<Reservation> findAllByCrewAndUserAndDateBetweenAndGuestIsNull(
                @Param("crew") Crew crew,
                @Param("user") User user,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate);

        Long countByUserAndCrewAndStatus(User user, Crew crew, String status);

        /**
         * 일반 예약만 카운트 (게스트 예약 제외)
         * guest IS NULL을 먼저 체크하여 게스트 예약을 빠르게 제외
         */
        @Query("SELECT COUNT(r) FROM Reservation r " +
                "WHERE r.guest IS NULL AND r.user = :user AND r.crew = :crew AND r.status = :status")
        Long countByUserAndCrewAndStatusAndGuestIsNull(
                @Param("user") User user,
                @Param("crew") Crew crew,
                @Param("status") String status);

        Long countByCrewAndDateAndStatusAndCreatedAtBefore(Crew crew, LocalDate date, String status,
                        java.time.LocalDateTime createdAt);

        /**
         * 크루별 회원 사용 통계 조회 (confirmed 상태만)
         */
        @Query("SELECT new com.boardbuddies.boardbuddiesserver.dto.crew.MemberUsageResponse(" +
                        "r.user.id, r.user.name, COUNT(r)) " +
                        "FROM Reservation r " +
                        "WHERE r.crew = :crew AND r.status = 'confirmed' " +
                        "GROUP BY r.user.id, r.user.name " +
                        "ORDER BY r.user.name")
        List<com.boardbuddies.boardbuddiesserver.dto.crew.MemberUsageResponse> findUsageCountsByCrew(
                        @Param("crew") Crew crew);

        /**
         * 특정 게스트 목록과 연관된 모든 예약 삭제
         */
        void deleteAllByGuestIn(List<Guest> guests);
}
