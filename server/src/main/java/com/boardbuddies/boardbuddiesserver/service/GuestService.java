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
     * 게스트 등록
     * 부원이 게스트 정보를 등록합니다.
     */
    @Transactional
    public GuestResponse createGuest(Long userId, GuestCreateRequest request) {
        // 1. 사용자 조회 및 권한 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getCrew() == null) {
            throw new RuntimeException("크루에 소속된 회원만 게스트를 등록할 수 있습니다.");
        }

        // 2. 게스트 생성
        Guest guest = Guest.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .school(request.getSchool())
                .registeredBy(user)
                .build();

        guest = guestRepository.save(guest);

        // 3. 응답 생성
        return GuestResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .phoneNumber(guest.getPhoneNumber())
                .school(guest.getSchool())
                .createdAt(guest.getCreatedAt())
                .registeredById(user.getId())
                .registeredByName(user.getName())
                .build();
    }

    /**
     * 게스트 조회 (본인이 등록한 게스트)
     */
    @Transactional(readOnly = true)
    public GuestResponse getGuest(Long userId, Long guestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Guest guest = guestRepository.findByIdAndRegisteredBy(guestId, user)
                .orElseThrow(() -> new RuntimeException("게스트를 찾을 수 없습니다."));

        return GuestResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .phoneNumber(guest.getPhoneNumber())
                .school(guest.getSchool())
                .createdAt(guest.getCreatedAt())
                .registeredById(guest.getRegisteredBy().getId())
                .registeredByName(guest.getRegisteredBy().getName())
                .build();
    }

    /**
     * 본인이 등록한 게스트 목록 조회
     */
    @Transactional(readOnly = true)
    public java.util.List<GuestResponse> getMyGuests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Fetch Join으로 N+1 문제 방지
        java.util.List<Guest> guests = guestRepository.findAllByRegisteredByOrderByCreatedAtDescWithFetch(user);

        return guests.stream()
                .map(guest -> GuestResponse.builder()
                        .id(guest.getId())
                        .name(guest.getName())
                        .phoneNumber(guest.getPhoneNumber())
                        .school(guest.getSchool())
                        .createdAt(guest.getCreatedAt())
                        .registeredById(guest.getRegisteredBy().getId())
                        .registeredByName(guest.getRegisteredBy().getName())
                        .build())
                .toList();
    }
}

