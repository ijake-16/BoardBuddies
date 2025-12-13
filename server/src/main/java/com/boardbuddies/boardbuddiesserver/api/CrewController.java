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
            @PathVariable Long crewId,
            @CurrentUser Long userId,
            @RequestBody CrewUpdateRequest request) {
        try {
            crewService.updateCrew(userId, crewId, request);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "크루 정보 수정 성공", null));

        } catch (Exception e) {
            log.error("크루 정보 수정 중 에러 발생", e);
            return handleUpdateError(e);
        }
    }

    private ResponseEntity<ApiResponse<Void>> handleUpdateError(Exception e) {
        if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } else if (e instanceof jakarta.persistence.EntityNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } else if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }

        // Fallback for generic RuntimeExceptions that might still use message matching
        // (legacy)
        String errorMessage = e.getMessage();
        if (errorMessage != null && errorMessage.contains("권한이 없습니다")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, errorMessage));
        } else if (errorMessage != null && errorMessage.contains("크루를 찾을 수 없습니다")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, errorMessage));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "서버 에러"));
    }

    /**
     * 크루 프로필 이미지 수정
     * 
     * POST /api/crews/{crewId}/profile-image
     * 
     * @param crewId 크루 ID
     * @param userId 현재 로그인한 사용자 ID
     * @param file   이미지 파일 (optional, 없으면 초기화)
     * @return 성공 응답
     */
    @PostMapping("/{crewId}/profile-image")
    public ResponseEntity<ApiResponse<Void>> updateProfileImage(
            @PathVariable Long crewId,
            @CurrentUser Long userId,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file) {
        try {
            crewService.updateCrewProfileImage(userId, crewId, file);

            String message = (file != null && !file.isEmpty()) ? "프로필 이미지 수정 성공" : "프로필 이미지 초기화 성공";
            return ResponseEntity.ok(ApiResponse.success(200, message));

        } catch (RuntimeException e) {
            log.error("크루 프로필 이미지 수정 중 에러 발생", e);
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, errorMessage));
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
     * 부원별 시즌방 사용 통계 조회
     * 
     * GET
     * /api/crews/{crewId}/usage-statistics?search=이름&sortBy=usageCount&sortOrder=desc
     * 
     * @param userId    현재 로그인한 사용자 ID
     * @param crewId    크루 ID
     * @param search    이름 검색어 (선택)
     * @param sortBy    정렬 기준: name(기본), usageCount
     * @param sortOrder 정렬 순서: asc(기본), desc
     * @return 부원별 사용 횟수 리스트
     */
    @GetMapping("/{crewId}/usage-statistics")
    public ResponseEntity<ApiResponse<java.util.List<MemberUsageResponse>>> getMemberUsageStatistics(
            @CurrentUser Long userId,
            @PathVariable Long crewId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder) {

        try {
            java.util.List<MemberUsageResponse> response = crewService
                    .getMemberUsageStatistics(userId, crewId, search, sortBy, sortOrder);

            return ResponseEntity.ok(
                    ApiResponse.success(200, "부원 사용 통계 조회 성공", response));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            log.error("부원 사용 통계 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }

    /**
     * 주간 간략 크루 달력 조회
     * 
     * GET /api/crews/{crewId}/calendar/week?date=2025-12-13
     * 
     * @param userId 현재 로그인한 사용자 ID
     * @param crewId 크루 ID
     * @param date   조회할 날짜 (기본값: 오늘)
     * @return 주간 달력 응답
     */
    @GetMapping("/{crewId}/calendar/week")
    public ResponseEntity<ApiResponse<List<CrewCalendarResponse>>> getCrewBriefCalendar(
            @CurrentUser Long userId,
            @PathVariable Long crewId,
            @RequestParam(required = false) LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }

        try {
            List<CrewCalendarResponse> response = crewService.getCrewBriefCalendar(userId, crewId, date);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "주간 달력 조회 성공", response));
        } catch (RuntimeException e) {
            log.error("주간 달력 조회 중 에러 발생", e);
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

    // ==================== 운영진 관리 API ====================

    /**
     * 운영진 목록 조회 (MANAGER 이상)
     * 
     * GET /api/crews/{crewId}/managers
     * 
     * @param crewId 크루 ID
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 운영진 목록 (회장 + 매니저)
     */
    @GetMapping("/{crewId}/managers")
    public ResponseEntity<ApiResponse<List<ManagerResponse>>> getManagers(
            @PathVariable Long crewId,
            @CurrentUser Long userId) {

        try {
            List<ManagerResponse> managers = crewService.getManagers(userId, crewId);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "운영진 목록 조회 성공", managers));

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            log.error("운영진 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }

    /**
     * 일반 부원 목록 조회 (MANAGER 이상)
     * 
     * GET /api/crews/{crewId}/members
     * 
     * @param crewId 크루 ID
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 일반 부원 목록 (MEMBER만)
     */
    @GetMapping("/{crewId}/members")
    public ResponseEntity<ApiResponse<List<ManagerResponse>>> getMembers(
            @PathVariable Long crewId,
            @CurrentUser Long userId) {

        try {
            List<ManagerResponse> members = crewService.getMembers(userId, crewId);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "일반 부원 목록 조회 성공", members));

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            log.error("일반 부원 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }

    /**
     * 운영진 추가 (PRESIDENT만)
     * 
     * POST /api/crews/{crewId}/managers/{userId}
     * 
     * @param crewId       크루 ID
     * @param targetUserId 추가할 부원의 user ID
     * @param userId       현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 성공 응답
     */
    @PostMapping("/{crewId}/managers/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> addManager(
            @PathVariable Long crewId,
            @PathVariable Long targetUserId,
            @CurrentUser Long userId) {

        try {
            crewService.addManager(userId, crewId, targetUserId);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "운영진 추가 성공"));

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("운영진 추가 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }

    /**
     * 운영진 삭제 (PRESIDENT만)
     * 
     * DELETE /api/crews/{crewId}/managers/{userId}
     * 
     * @param crewId       크루 ID
     * @param targetUserId 삭제할 운영진의 user ID
     * @param userId       현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 성공 응답
     */
    @DeleteMapping("/{crewId}/managers/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> removeManager(
            @PathVariable Long crewId,
            @PathVariable Long targetUserId,
            @CurrentUser Long userId) {

        try {
            crewService.removeManager(userId, crewId, targetUserId);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "운영진 삭제 성공"));

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("운영진 삭제 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }

    /**
     * 부원 삭제 (MANAGER 이상)
     * 
     * DELETE /api/crews/{crewId}/members/{targetUserId}
     * 
     * @param crewId       크루 ID
     * @param targetUserId 삭제할 부원의 user ID
     * @param userId       현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 성공 응답
     */
    @DeleteMapping("/{crewId}/members/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long crewId,
            @PathVariable Long targetUserId,
            @CurrentUser Long userId) {

        try {
            crewService.removeMember(userId, crewId, targetUserId);
            return ResponseEntity.ok(
                    ApiResponse.success(200, "부원 삭제 성공"));

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("부원 삭제 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
        }
    }
}
