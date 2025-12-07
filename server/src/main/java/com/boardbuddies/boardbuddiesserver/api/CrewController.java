package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.dto.crew.*;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.service.CrewApplicationService;
import com.boardbuddies.boardbuddiesserver.service.CrewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 크루 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;
    private final CrewApplicationService crewApplicationService;

    /**
     * 모든 크루 목록 조회
     * 
     * GET /api/crews
     * 
     * @return 크루 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CrewListResponse>>> getAllCrews() {
        try {
            List<CrewListResponse> crews = crewService.getAllCrews();
            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 목록 조회 성공", crews));
        } catch (RuntimeException e) {
            log.error("크루 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }

    /**
     * 크루 상세 정보 조회
     * 
     * GET /api/crews/{crewId}
     * 
     * @param crewId 크루 ID
     * @return 크루 상세 정보
     */
    @GetMapping("/{crewId}")
    public ResponseEntity<ApiResponse<CrewDetailResponse>> getCrewDetail(@PathVariable Long crewId) {
        try {
            CrewDetailResponse crew = crewService.getCrewDetail(crewId);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 정보 조회 성공", crew));
        } catch (RuntimeException e) {
            log.error("크루 정보 조회 중 에러 발생", e);

            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 크루 정보 수정
     * 
     * PATCH /api/crews/{crewId}
     * 
     * @param userId  현재 로그인한 사용자 ID
     * @param crewId  크루 ID
     * @param request 수정 요청
     * @return 수정 결과
     */
    @PatchMapping("/{crewId}")
    public ResponseEntity<ApiResponse<Void>> updateCrew(
            @CurrentUser Long userId,
            @PathVariable Long crewId,
            @Valid @RequestBody CrewUpdateRequest request) {

        try {
            crewService.updateCrew(userId, crewId, request);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 정보 수정 성공", null));
        } catch (RuntimeException e) {
            log.error("크루 정보 수정 중 에러 발생", e);

            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("수정 권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "수정 권한이 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("크루를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "해당 크루를 찾을 수 없습니다."));
            } else if (errorMessage != null
                    && (errorMessage.contains("사용자를 찾을 수 없습니다") || errorMessage.contains("학번"))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "존재하지 않는 회원을 운영진으로 추가할 수 없습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 크루 삭제
     * 
     * DELETE /api/crews/{crewId}
     * 
     * @param userId  현재 로그인한 사용자 ID
     * @param crewId  크루 ID
     * @param request 삭제 요청 (PIN 확인용)
     * @return 삭제 결과
     */
    @DeleteMapping("/{crewId}")
    public ResponseEntity<ApiResponse<Void>> deleteCrew(
            @CurrentUser Long userId,
            @PathVariable Long crewId,
            @Valid @RequestBody CrewDeleteRequest request) {

        try {
            crewService.deleteCrew(userId, crewId, request);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 삭제 성공", null));
        } catch (RuntimeException e) {
            log.error("크루 삭제 중 에러 발생", e);

            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("삭제 권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "크루 삭제 권한이 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("PIN이 일치하지 않습니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "PIN이 일치하지 않습니다."));
            } else if (errorMessage != null && errorMessage.contains("찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 크루 생성
     * 
     * POST /api/crews
     * 
     * @param userId  현재 로그인한 사용자 ID (자동으로 PRESIDENT가 됨)
     * @param request 크루 생성 요청
     * @return 생성된 크루 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CrewCreateResponse>> createCrew(
            @CurrentUser Long userId,
            @Valid @RequestBody CrewCreateRequest request) {

        try {
            CrewCreateResponse response = crewService.createCrew(userId, request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(201, "크루 생성 성공", response));

        } catch (RuntimeException e) {
            log.error("크루 생성 처리 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("사용자를 찾을 수 없습니다")) {
                // 생성자(회장)를 찾을 수 없는 경우 or 운영진 추가 시 사용자 없음
                if (errorMessage.contains("학번")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(404, "존재하지 않는 회원을 운영진으로 추가할 수 없습니다."));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "사용자를 찾을 수 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("회원가입이 완료되지 않았습니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, errorMessage));
            } else if (errorMessage != null && errorMessage.contains("소속 대학이") && errorMessage.contains("일치하지 않습니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 크루 가입 신청
     * 
     * POST /api/crews/{crewId}/applications
     * 
     * @param crewId  크루 ID
     * @param userId  현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @param request 신청 요청 (PIN 포함)
     * @return 성공 응답
     */
    @PostMapping("/{crewId}/applications")
    public ResponseEntity<ApiResponse<Void>> applyForCrew(
            @PathVariable Long crewId,
            @CurrentUser Long userId,
            @Valid @RequestBody CrewApplicationRequest request) {

        try {
            crewApplicationService.applyForCrew(crewId, userId, request);

            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 가입 신청 성공"));

        } catch (RuntimeException e) {
            log.error("크루 가입 신청 처리 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("크루를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "크루를 찾을 수 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("PIN이 일치하지 않습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, errorMessage));
            } else if (errorMessage != null && errorMessage.contains("이미 가입 신청이 완료되었습니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 월별 크루 달력 조회
     * 
     * GET /api/crews/{crewId}/calendar?date=2025-12-01
     * 
     * @param userId 현재 로그인한 사용자 ID
     * @param crewId 크루 ID
     * @param date   조회할 날짜 (월 단위 조회를 위해 해당 월의 아무 날짜나 가능)
     * @return 크루 달력 응답
     */
    @GetMapping("/{crewId}/calendar")
    public ResponseEntity<ApiResponse<CrewCalendarWrapperResponse>> getCrewCalendar(
            @CurrentUser Long userId,
            @PathVariable Long crewId,
            @RequestParam LocalDate date,
            @RequestParam(required = false, defaultValue = "false") boolean showMySchedule) {

        try {
            CrewCalendarWrapperResponse response = crewService.getCrewCalendar(userId, crewId, date, showMySchedule);

            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 달력 조회 성공", response));
        } catch (RuntimeException e) {
            log.error("크루 달력 조회 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("크루를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 나의 달력 조회 (내 예약 + 이용 횟수 + 혼잡도)
     * 
     * GET /api/crews/{crewId}/calendar/my?date=2025-12-01
     * 
     * @param userId 현재 로그인한 사용자 ID
     * @param crewId 크루 ID
     * @param date   조회할 날짜
     * @return 나의 달력 응답
     */
    @GetMapping("/{crewId}/calendar/my")
    public ResponseEntity<ApiResponse<MyCalendarResponse>> getMyCalendar(
            @CurrentUser Long userId,
            @PathVariable Long crewId,
            @RequestParam LocalDate date) {

        try {
            MyCalendarResponse response = crewService.getMyCalendar(userId, crewId, date);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "나의 달력 조회 성공", response));
        } catch (RuntimeException e) {
            log.error("나의 달력 조회 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 크루 가입 신청 목록 조회 (운영진용)
     * 
     * GET /api/crews/{crewId}/applications
     * 
     * @param crewId 크루 ID
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 신청 목록
     */
    @GetMapping("/{crewId}/applications")
    public ResponseEntity<ApiResponse<List<CrewApplicationResponse>>> getApplications(
            @PathVariable Long crewId,
            @CurrentUser Long userId) {

        try {
            List<CrewApplicationResponse> applications = crewApplicationService.getApplications(crewId, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(201, "가입 신청 목록 조회 성공", applications));

        } catch (RuntimeException e) {
            log.error("가입 신청 목록 조회 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "가입 신청 목록을 조회할 권한이 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("크루를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 크루 가입 신청 승인/거절 (운영진용)
     * 
     * POST /api/crews/{crewId}/applications/{applicationId}/approve
     * 
     * @param crewId        크루 ID
     * @param applicationId 신청 ID
     * @param userId        현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @param request       승인/거절 결정 (0: 거절, 1: 승인)
     * @return 성공 응답
     */
    @PostMapping("/{crewId}/applications/{applicationId}/approve")
    public ResponseEntity<ApiResponse<Void>> processApplication(
            @PathVariable Long crewId,
            @PathVariable Long applicationId,
            @CurrentUser Long userId,
            @Valid @RequestBody ApplicationDecisionRequest request) {

        try {
            crewApplicationService.processApplication(crewId, applicationId, userId, request);

            String message = request.isApproved() ? "가입 신청 승인 완료" : "가입 신청 거절 완료";

            return ResponseEntity.ok(
                    ApiResponse.success(200, message));

        } catch (RuntimeException e) {
            log.error("가입 신청 처리 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "가입 승인/거절 권한이 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("크루를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else if (errorMessage != null && errorMessage.contains("신청서를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, errorMessage));
            } else if (errorMessage != null && errorMessage.contains("이미 처리된 신청입니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "이미 처리된 신청입니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }
}
