package com.boardbuddies.boardbuddiesserver.api;

import com.boardbuddies.boardbuddiesserver.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 컨트롤러
 * 애플리케이션이 정상 작동하는지 확인용
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(
            ApiResponse.success(200, "서버가 정상 작동 중입니다.", "OK")
        );
    }
}

