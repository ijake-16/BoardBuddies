package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.dto.club.*;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.service.ClubApplicationService;
import com.boardbuddies.boardbuddiesserver.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 동아리 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    
    private final ClubService clubService;
    private final ClubApplicationService clubApplicationService;
    
    /**
     * 동아리 생성
     * 
     * POST /api/clubs
     * 
     * @param userId 현재 로그인한 사용자 ID (자동으로 PRESIDENT가 됨)
     * @param request 동아리 생성 요청
     * @return 생성된 동아리 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClubCreateResponse>> createClub(
        @CurrentUser Long userId,
        @Valid @RequestBody ClubCreateRequest request) {
        
        try {
            ClubCreateResponse response = clubService.createClub(userId, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "동아리 생성 성공", response));
            
        } catch (RuntimeException e) {
            log.error("동아리 생성 처리 중 에러 발생", e);
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("사용자를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "사용자를 찾을 수 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("학번") && errorMessage.contains("찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, errorMessage));
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
     * 동아리 가입 신청
     * 
     * POST /api/clubs/{clubId}/applications
     * 
     * @param clubId 동아리 ID
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @param request 신청 요청 (PIN 포함)
     * @return 성공 응답
     */
    @PostMapping("/{clubId}/applications")
    public ResponseEntity<ApiResponse<Void>> applyForClub(
        @PathVariable Long clubId,
        @CurrentUser Long userId,
        @Valid @RequestBody ClubApplicationRequest request) {
        
        try {
            clubApplicationService.applyForClub(clubId, userId, request);
            
            return ResponseEntity.ok(
                ApiResponse.success(200, "동아리 가입 신청 성공")
            );
            
        } catch (RuntimeException e) {
            log.error("동아리 가입 신청 처리 중 에러 발생", e);
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("동아리를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "동아리를 찾을 수 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("PIN이 일치하지 않습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "PIN이 일치하지 않습니다."));
            } else if (errorMessage != null && errorMessage.contains("이미 가입 신청이 완료되었습니다")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "이미 가입 신청이 완료되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }
    
    /**
     * 동아리 가입 신청 목록 조회 (운영진용)
     * 
     * GET /api/clubs/{clubId}/applications
     * 
     * @param clubId 동아리 ID
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 신청 목록
     */
    @GetMapping("/{clubId}/applications")
    public ResponseEntity<ApiResponse<List<ClubApplicationResponse>>> getApplications(
        @PathVariable Long clubId,
        @CurrentUser Long userId) {
        
        try {
            List<ClubApplicationResponse> applications = 
                clubApplicationService.getApplications(clubId, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "가입 신청 목록 조회 성공", applications));
            
        } catch (RuntimeException e) {
            log.error("가입 신청 목록 조회 중 에러 발생", e);
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "가입 신청 목록을 조회할 권한이 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("동아리를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "동아리를 찾을 수 없습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }
    
    /**
     * 동아리 가입 신청 승인/거절 (운영진용)
     * 
     * POST /api/clubs/{clubId}/applications/{applicationId}/approve
     * 
     * @param clubId 동아리 ID
     * @param applicationId 신청 ID
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @param request 승인/거절 결정 (0: 거절, 1: 승인)
     * @return 성공 응답
     */
    @PostMapping("/{clubId}/applications/{applicationId}/approve")
    public ResponseEntity<ApiResponse<Void>> processApplication(
        @PathVariable Long clubId,
        @PathVariable Long applicationId,
        @CurrentUser Long userId,
        @Valid @RequestBody ApplicationDecisionRequest request) {
        
        try {
            clubApplicationService.processApplication(clubId, applicationId, userId, request);
            
            String message = request.isApproved() ? "가입 신청 승인 완료" : "가입 신청 거절 완료";
            
            return ResponseEntity.ok(
                ApiResponse.success(200, message)
            );
            
        } catch (RuntimeException e) {
            log.error("가입 신청 처리 중 에러 발생", e);
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "가입 승인/거절 권한이 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("동아리를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "동아리를 찾을 수 없습니다."));
            } else if (errorMessage != null && errorMessage.contains("신청서를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "신청서를 찾을 수 없습니다."));
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

