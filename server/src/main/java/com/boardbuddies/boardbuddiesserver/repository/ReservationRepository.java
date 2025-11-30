package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Reservation;
import com.boardbuddies.boardbuddiesserver.domain.Club;
import com.boardbuddies.boardbuddiesserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserOrderByCreatedAtDesc(User user);

    List<Reservation> findAllByUserAndDateBetweenOrderByCreatedAtDesc(User user, LocalDate startDate,
            LocalDate endDate);

    Long countByClubAndDateAndStatusNot(Club club, LocalDate date, String status);

    List<Reservation> findByClubAndDateAndStatusOrderByCreatedAtAsc(Club club, LocalDate date, String status);

    Optional<Reservation> findByUserAndClubAndDateAndStatusNot(User user, Club club, LocalDate date, String status);

    List<Reservation> findByClubAndDateAndStatusNot(Club club, LocalDate date, String status);
}
