package com.boardbuddies.boardbuddiesserver.repository;

import com.boardbuddies.boardbuddiesserver.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 게스트 Repository
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    /**
     * 이름과 전화번호로 게스트 조회
     */
    Optional<Guest> findByNameAndPhoneNumber(String name, String phoneNumber);
}

