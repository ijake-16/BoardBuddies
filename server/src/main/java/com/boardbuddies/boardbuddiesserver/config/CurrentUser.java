package com.boardbuddies.boardbuddiesserver.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자 ID를 가져오는 애노테이션
 * 
 * 사용 예시:
 * public ResponseEntity<?> myMethod(@CurrentUser Long userId) {
 *     // userId로 현재 로그인한 사용자 작업 수행
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
public @interface CurrentUser {
}

