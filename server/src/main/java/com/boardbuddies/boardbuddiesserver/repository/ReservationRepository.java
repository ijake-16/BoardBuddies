package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Reservation;
import com.boardbuddies.boardbuddiesserver.domain.Crew;
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

        List<Reservation> findAllByUserAndDateBetweenOrderByCreatedAtDesc(User user, LocalDate startDate,
                        LocalDate endDate);

        Long countByCrewAndDateAndStatusNot(Crew crew, LocalDate date, String status);

        List<Reservation> findByCrewAndDateAndStatusOrderByCreatedAtAsc(Crew crew, LocalDate date, String status);

        Optional<Reservation> findByUserAndCrewAndDateAndStatusNot(User user, Crew crew, LocalDate date, String status);

        List<Reservation> findByCrewAndDateAndStatusNot(Crew crew, LocalDate date, String status);

        List<Reservation> findByCrewAndStatus(Crew crew, String status);

        List<Reservation> findAllByCrewAndDateBetweenAndStatusNot(Crew crew, LocalDate startDate, LocalDate endDate,
                        String status);

        List<Reservation> findAllByCrewAndDateAndStatusNotOrderByCreatedAtAsc(Crew crew, LocalDate date, String status);

        @Query("SELECT new com.boardbuddies.boardbuddiesserver.dto.crew.DailyReservationCount(r.date, COUNT(r)) " +
                        "FROM Reservation r " +
                        "WHERE r.crew = :crew " +
                        "AND r.date BETWEEN :startDate AND :endDate " +
                        "AND r.status <> 'CANCELLED' " +
                        "GROUP BY r.date")
        List<DailyReservationCount> findDailyCountsByCrewAndDateBetween(
                        @Param("crew") Crew crew,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        List<Reservation> findAllByCrewAndUserAndDateBetweenAndStatusNot(Crew crew, User user, LocalDate startDate,
                        LocalDate endDate, String status);
}
