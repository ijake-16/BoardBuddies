package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Reservation;
import com.boardbuddies.boardbuddiesserver.domain.Crew;
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

    Long countByCrewAndDateAndStatusNot(Crew crew, LocalDate date, String status);

    List<Reservation> findByCrewAndDateAndStatusOrderByCreatedAtAsc(Crew crew, LocalDate date, String status);

    Optional<Reservation> findByUserAndCrewAndDateAndStatusNot(User user, Crew crew, LocalDate date, String status);

    List<Reservation> findByCrewAndDateAndStatusNot(Crew crew, LocalDate date, String status);

    List<Reservation> findByCrewAndStatus(Crew crew, String status);
}
