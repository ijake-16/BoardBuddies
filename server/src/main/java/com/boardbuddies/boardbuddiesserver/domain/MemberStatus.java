package com.boardbuddies.boardbuddiesserver.domain;

/**
 * 동아리 가입 신청 상태
 */
public enum MemberStatus {
    PENDING,    // 승인 대기
    APPROVED,   // 승인됨
    REJECTED    // 거절됨
}

