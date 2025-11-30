package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Club;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 동아리 Repository
 */
@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Club c WHERE c.id = :id")
    Optional<Club> findByIdWithLock(Long id);
}
