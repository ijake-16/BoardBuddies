package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.dto.auth.*;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 소셜 로그인 (카카오, 네이버)
     * 
     * @param provider      소셜 제공자 (kakao, naver)
     * @param authorization Authorization 헤더 (Bearer token)
     * @return 소셜 로그인 응답 (기존 회원: 로그인 토큰, 신규 회원: 임시 토큰)
     */
    @PostMapping("/social/{provider}")
    public ResponseEntity<ApiResponse<?>> socialLogin(
            @PathVariable("provider") String provider,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        try {
            // Authorization 헤더 검증
            if (authorization == null || authorization.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "토큰이 없습니다."));
            }

            // Bearer 토큰 추출
            String socialAccessToken;
            if (authorization.startsWith("Bearer ")) {
                socialAccessToken = authorization.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "유효하지 않은 토큰입니다."));
            }

            // 소셜 제공자 검증
            SocialProvider socialProvider;
            try {
                socialProvider = SocialProvider.valueOf(provider.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "지원하지 않는 소셜 제공자입니다."));
            }

            // 소셜 로그인 처리
            Object result = authService.processSocialLogin(socialProvider, socialAccessToken);

            // 기존 회원 로그인
            if (result instanceof SocialLoginResponse) {
                SocialLoginResponse response = (SocialLoginResponse) result;
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(201, "로그인 성공", response));
            }

            // 신규 회원 - 추가 정보 입력 필요
            if (result instanceof TempTokenResponse) {
                TempTokenResponse response = (TempTokenResponse) result;
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(201, "소셜 로그인 성공. 추가 정보를 입력해주세요.", response));
            }

            // 예상치 못한 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "서버 에러"));

        } catch (RuntimeException e) {
            log.error("소셜 로그인 처리 중 에러 발생", e);

            // 에러 메시지에 따라 적절한 상태 코드와 메시지 반환
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("만료된 소셜 토큰")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "만료된 소셜 토큰입니다."));
            } else if (errorMessage != null && errorMessage.contains("소셜로그인 회원 정보 조회 실패")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "소셜로그인 회원 정보 조회 실패"));
            } else if (errorMessage != null && errorMessage.contains("회원 정보 조회 실패")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "회원 정보 조회 실패"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 회원가입 완료 (추가 정보 입력)
     * 
     * @param authorization Authorization 헤더 (Bearer tempAccessToken)
     * @param request       추가 정보 (이름, 생년월일, 학교, 학번, 성별, 전화번호)
     * @return 회원가입 완료 응답 (정식 토큰)
     */
    @PostMapping("/signup/complete")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> completeSignup(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody SignupCompleteRequest request) {

        try {
            // Authorization 헤더 검증
            if (authorization == null || authorization.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "토큰이 없습니다."));
            }

            // Bearer 토큰 추출
            String tempAccessToken;
            if (authorization.startsWith("Bearer ")) {
                tempAccessToken = authorization.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "유효하지 않은 토큰입니다."));
            }

            // 회원가입 완료 처리
            SocialLoginResponse response = authService.completeSignup(tempAccessToken, request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(201, "회원가입 성공", response));

        } catch (RuntimeException e) {
            log.error("회원가입 완료 처리 중 에러 발생", e);

            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("사용자를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "유효하지 않은 토큰입니다."));
            } else if (errorMessage != null && errorMessage.contains("이미 회원가입이 완료된")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "이미 회원가입이 완료된 사용자입니다."));
            } else if (errorMessage != null && errorMessage.contains("이미 사용 중인 학번")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, "이미 사용 중인 학번입니다."));
            } else if (errorMessage != null && errorMessage.contains("이미 사용 중인 전화번호")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, "이미 사용 중인 전화번호입니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }

    /**
     * 토큰 재발급
     * 
     * @param authorization Authorization 헤더 (Bearer refreshToken)
     * @return 새로운 액세스 토큰과 리프레시 토큰
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> refreshToken(
            @RequestHeader("Authorization") String authorization) {

        try {
            // Authorization 헤더 검증
            if (authorization == null || authorization.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "토큰이 없습니다."));
            }

            // Bearer 토큰 추출
            String refreshToken;
            if (authorization.startsWith("Bearer ")) {
                refreshToken = authorization.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "유효하지 않은 토큰입니다."));
            }

            // 토큰 재발급
            SocialLoginResponse response = authService.refreshToken(refreshToken);

            return ResponseEntity.ok(
                    ApiResponse.success(200, "토큰 재발급 성공", response));

        } catch (RuntimeException e) {
            log.error("토큰 재발급 처리 중 에러 발생", e);

            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("만료된 토큰")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "만료된 리프레시 토큰입니다."));
            } else if (errorMessage != null && errorMessage.contains("유효하지 않은")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "유효하지 않은 리프레시 토큰입니다."));
            } else if (errorMessage != null && errorMessage.contains("사용자를 찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "유효하지 않은 리프레시 토큰입니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "서버 에러"));
            }
        }
    }
}
