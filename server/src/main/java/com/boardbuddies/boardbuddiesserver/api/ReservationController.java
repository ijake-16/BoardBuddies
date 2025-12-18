package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationDayDetailResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationListResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationMultiResponse;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationRequest;
import com.boardbuddies.boardbuddiesserver.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class ReservationController {

        private final ReservationService reservationService;

        /**
         * 시즌방 예약 (일괄 신청)
         * 
         * POST /api/crews/{crewId}/reservations
         * 
         * @param userId  현재 로그인한 사용자 ID
         * @param crewId  크루 ID
         * @param request 예약 요청 (날짜 목록)
         * @return 예약 결과
         */
        @PostMapping("/{crewId}/reservations")
        public ResponseEntity<ApiResponse<ReservationMultiResponse>> reserve(
                        @CurrentUser Long userId,
                        @PathVariable Long crewId,
                        @RequestBody ReservationRequest request) {

                ReservationMultiResponse response = reservationService.reserve(userId, crewId, request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(201, "예약 신청 완료", response));
        }

        /**
         * 예약 취소 (일괄 취소)
         * 
         * DELETE /api/crews/{crewId}/reservations
         */
        @DeleteMapping("/{crewId}/reservations")
        public ResponseEntity<ApiResponse<Void>> cancel(
                        @CurrentUser Long userId,
                        @PathVariable Long crewId,
                        @RequestBody ReservationRequest request) {

                reservationService.cancel(userId, crewId, request);

                return ResponseEntity.ok(
                                ApiResponse.success(200, "예약 취소 완료"));
        }

        /**
         * 날짜별 예약자 명단 조회
         * 
         * GET /api/crews/{crewId}/reservations?date=2023-11-01
         */
        @GetMapping("/{crewId}/reservations")
        public ResponseEntity<ApiResponse<ReservationListResponse>> getReservationsByDate(
                        @CurrentUser Long userId,
                        @PathVariable Long crewId,
                        @RequestParam java.time.LocalDate date) {

                ReservationListResponse response = reservationService
                                .getReservationsByDate(userId, crewId, date);

                return ResponseEntity.ok(
                                ApiResponse.success(200, "예약자 명단 조회 완료", response));
        }

        /**
         * 날짜별 예약 상세 조회 (단건 - 캘린더 클릭 시)
         * 
         * GET /api/crews/{crewId}/reservations/detail?date=2023-11-01
         */
        @GetMapping("/{crewId}/reservations/detail")
        public ResponseEntity<ApiResponse<ReservationDayDetailResponse>> getDayReservationDetail(
                        @CurrentUser Long userId,
                        @PathVariable Long crewId,
                        @RequestParam java.time.LocalDate date) {

                ReservationDayDetailResponse response = reservationService
                                .getDayReservationDetail(userId, crewId, date);

                return ResponseEntity.ok(
                                ApiResponse.success(200, "예약 상세 조회 완료", response));
        }

        /**
         * 내 예약 내역 (이번주 + 다음주) 조회 - 메인 화면용
         * 
         * GET /api/crews/my-calendar
         */
        @GetMapping("/my-calendar")
        public ResponseEntity<ApiResponse<java.util.List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationCalendarResponse>>> getMyCalendar(
                        @CurrentUser Long userId) {

                java.util.List<com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationCalendarResponse> response = reservationService
                                .getMyCalendarReservations(userId);

                return ResponseEntity.ok(
                                ApiResponse.success(200, "내 예약 조회 완료", response));
        }
}
