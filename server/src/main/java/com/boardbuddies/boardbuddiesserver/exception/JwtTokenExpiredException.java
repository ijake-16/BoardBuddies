package com.boardbuddies.boardbuddiesserver.exception;

/**
 * JWT 토큰 만료 예외
 */
public class JwtTokenExpiredException extends RuntimeException {

    public JwtTokenExpiredException(String message) {
        super(message);
    }

    public JwtTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}


