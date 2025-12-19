package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.Guest;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.guest.GuestCreateRequest;
import com.boardbuddies.boardbuddiesserver.dto.guest.GuestResponse;
import com.boardbuddies.boardbuddiesserver.repository.GuestRepository;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    /**
     * 게스트 등록 또는 조회
     * 이름과 전화번호로 게스트를 조회하고, 없으면 생성합니다.
     */
    @Transactional
    public GuestResponse createGuest(Long userId, GuestCreateRequest request) {
        // 1. 사용자 조회 및 권한 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getCrew() == null) {
            throw new RuntimeException("크루에 소속된 회원만 게스트를 등록할 수 있습니다.");
        }

        // 2. 이름과 전화번호로 게스트 조회 (이미 존재하면 반환)
        Guest guest = guestRepository.findByNameAndPhoneNumber(request.getName(), request.getPhoneNumber())
                .orElse(null);

        // 3. 게스트가 없으면 생성
        if (guest == null) {
            guest = Guest.builder()
                    .name(request.getName())
                    .phoneNumber(request.getPhoneNumber())
                    .build();

            guest = guestRepository.save(guest);
        }

        // 4. 응답 생성
        return GuestResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .phoneNumber(guest.getPhoneNumber())
                .createdAt(guest.getCreatedAt())
                .build();
    }

    /**
     * 게스트 조회
     */
    @Transactional(readOnly = true)
    public GuestResponse getGuest(Long userId, Long guestId) {
        // 사용자 조회 (권한 검증용)
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("게스트를 찾을 수 없습니다."));

        return GuestResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .phoneNumber(guest.getPhoneNumber())
                .createdAt(guest.getCreatedAt())
                .build();
    }

    /**
     * 게스트 목록 조회 (이름과 전화번호로 조회)
     * 모든 게스트를 조회합니다.
     */
    @Transactional(readOnly = true)
    public java.util.List<GuestResponse> getMyGuests(Long userId) {
        // 사용자 조회 (권한 검증용)
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 모든 게스트 조회
        java.util.List<Guest> guests = guestRepository.findAll();

        return guests.stream()
                .map(guest -> GuestResponse.builder()
                        .id(guest.getId())
                        .name(guest.getName())
                        .phoneNumber(guest.getPhoneNumber())
                        .createdAt(guest.getCreatedAt())
                        .build())
                .toList();
    }
}

