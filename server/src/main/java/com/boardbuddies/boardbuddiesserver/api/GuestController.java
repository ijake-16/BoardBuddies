package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.dto.guest.GuestCreateRequest;
import com.boardbuddies.boardbuddiesserver.dto.guest.GuestResponse;
import com.boardbuddies.boardbuddiesserver.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    /**
     * 게스트 등록
     * POST /api/guests
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GuestResponse>> createGuest(
            @CurrentUser Long userId,
            @RequestBody GuestCreateRequest request) {

        GuestResponse response = guestService.createGuest(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "게스트 등록 완료", response));
    }

    /**
     * 게스트 조회
     * GET /api/guests/{guestId}
     */
    @GetMapping("/{guestId}")
    public ResponseEntity<ApiResponse<GuestResponse>> getGuest(
            @CurrentUser Long userId,
            @PathVariable Long guestId) {

        GuestResponse response = guestService.getGuest(userId, guestId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "게스트 조회 완료", response));
    }

    /**
     * 본인이 등록한 게스트 목록 조회
     * GET /api/guests
     */
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<GuestResponse>>> getMyGuests(
            @CurrentUser Long userId) {

        java.util.List<GuestResponse> response = guestService.getMyGuests(userId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "게스트 목록 조회 완료", response));
    }
}

