package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Guest;
import com.boardbuddies.boardbuddiesserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게스트 Repository
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    /**
     * 등록한 부원으로 게스트 목록 조회
     */
    List<Guest> findAllByRegisteredByOrderByCreatedAtDesc(User registeredBy);

    /**
     * 등록한 부원으로 게스트 목록 조회 (Fetch Join으로 N+1 문제 방지)
     */
    @Query("SELECT g FROM Guest g " +
            "LEFT JOIN FETCH g.registeredBy " +
            "WHERE g.registeredBy = :registeredBy " +
            "ORDER BY g.createdAt DESC")
    List<Guest> findAllByRegisteredByOrderByCreatedAtDescWithFetch(@Param("registeredBy") User registeredBy);

    /**
     * 등록한 부원과 ID로 게스트 조회
     */
    Optional<Guest> findByIdAndRegisteredBy(Long id, User registeredBy);
}

