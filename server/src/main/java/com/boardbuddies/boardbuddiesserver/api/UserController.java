package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.dto.user.UserResponse;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.boardbuddies.boardbuddiesserver.domain.Reservation;
import com.boardbuddies.boardbuddiesserver.dto.reservation.ReservationResponse;
import com.boardbuddies.boardbuddiesserver.repository.ReservationRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 사용자 관련 API 컨트롤러
 * JWT 인증이 필요한 API 예시
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 내 정보 조회 (JWT 인증 필요)
     * 
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 사용자 정보
     */
    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@CurrentUser Long userId) {
        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        return ResponseEntity.ok(
                ApiResponse.success(200, "유저 조회 성공", UserResponse.from(user)));
    }

    /**
     * 내 예약 조회 (JWT 인증 필요)
     * 
     * @param userId    현재 로그인한 사용자 ID
     * @param startDate 조회 시작일 (optional)
     * @param endDate   조회 종료일 (optional)
     * @return 예약 목록
     */
    @GetMapping("/me/reservations")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations(
            @CurrentUser Long userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        User user = Objects.requireNonNull(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));

        List<Reservation> reservations;

        if (startDate != null && endDate != null) {
            // 날짜 범위 검증 (최대 90일)
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            if (daysBetween > 90) {
                return ResponseEntity.status(409).body(
                        ApiResponse.error(409, "요청 기간이 서버 정책을 초과했습니다. (최대 90일)"));
            }
            if (daysBetween < 0) {
                return ResponseEntity.status(400).body(
                        ApiResponse.error(400, "종료일은 시작일보다 빠를 수 없습니다."));
            }

            reservations = reservationRepository.findAllByUserAndDateBetweenOrderByCreatedAtDesc(user, startDate,
                    endDate);
        } else {
            // 기본 조회 (전체 또는 최근 N건 - 여기서는 전체로 구현하되 필요시 제한 가능)
            reservations = reservationRepository.findAllByUserOrderByCreatedAtDesc(user);
        }

        List<ReservationResponse> response = reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(200, "내 예약 조회 성공", response));
    }
}
