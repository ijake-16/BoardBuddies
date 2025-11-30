package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMultiResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationRequest;
import com.boardbuddies.boardbuddiesserver.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 시즌방 예약 (일괄 신청)
     * 
     * @param clubId         동아리 ID
     * @param userId         현재 로그인한 사용자 ID
     * @param request        예약 요청 (날짜 목록)
     * @param idempotencyKey 중복 요청 방지 키 (Optional)
     * @return 예약 결과 (부분 성공 가능)
     */
    @PostMapping("/{clubId}/reservations")
    public ResponseEntity<ApiResponse<ReservationMultiResponse>> createReservation(
            @PathVariable Long clubId,
            @CurrentUser Long userId,
            @RequestBody ReservationRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        // Idempotency Key 처리 로직은 추후 구현 (Redis 등 필요)

        ReservationMultiResponse response = reservationService.reserve(userId, clubId, request);

        // 모든 요청이 성공했으면 201 Created, 하나라도 실패했으면 207 Multi-Status
        if (response.getSummary().getFailed() == 0) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(201, "시즌방 예약 성공", response));
        } else {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                    .body(ApiResponse.success(207, "요청 처리 완료 (부분 성공 가능)", response));
        }
    }

    @PostMapping("/{clubId}/reservations/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @PathVariable Long clubId,
            @CurrentUser Long userId,
            @RequestBody ReservationRequest request) {

        reservationService.cancel(userId, clubId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "예약 취소 성공", null));
    }

    @GetMapping("/{clubId}/reservations")
    public ResponseEntity<ApiResponse<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse>> getReservations(
            @PathVariable Long clubId,
            @CurrentUser Long userId,
            @RequestParam String date) {

        com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse response = reservationService
                .getReservationsByDate(userId, clubId, java.time.LocalDate.parse(date));

        return ResponseEntity.ok(ApiResponse.success(200, "예약자 명단 조회 성공", response));
    }
}
