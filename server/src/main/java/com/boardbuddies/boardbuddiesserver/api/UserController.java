package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.config.CurrentUser;
import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 API 컨트롤러
 * JWT 인증이 필요한 API 예시
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    
    /**
     * 내 정보 조회 (JWT 인증 필요)
     * 
     * @param userId 현재 로그인한 사용자 ID (JWT에서 자동 추출)
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getMyInfo(@CurrentUser Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        return ResponseEntity.ok(
            ApiResponse.success(200, "내 정보 조회 성공", user)
        );
    }
}

