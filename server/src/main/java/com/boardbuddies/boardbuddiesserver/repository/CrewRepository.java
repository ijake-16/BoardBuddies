package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Crew;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 크루 Repository
 */
@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Crew c WHERE c.id = :id")
    Optional<Crew> findByIdWithLock(Long id);
}
