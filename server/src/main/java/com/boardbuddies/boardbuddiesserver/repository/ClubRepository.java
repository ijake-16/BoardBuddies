package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 동아리 Repository
 */
@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
}

