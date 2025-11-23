package com.boardbuddies.boardbuddiesserver.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 공통 응답 포맷
 * @param <T> 응답 데이터 타입
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private int code;
    private String message;
    private T data;
    
    /**
     * 성공 응답 생성 (data 포함)
     */
    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
    
    /**
     * 성공 응답 생성 (data 없음)
     */
    public static <T> ApiResponse<T> success(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
    
    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}

