package com.boardbuddies.boardbuddiesserver.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 요일 (숫자 값 포함)
 */
@Getter
@RequiredArgsConstructor
public enum DayOfWeek {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);
    
    private final int value;
    
    /**
     * 숫자 값으로 요일 찾기
     */
    public static DayOfWeek fromValue(int value) {
        for (DayOfWeek day : values()) {
            if (day.value == value) {
                return day;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 요일 값입니다: " + value);
    }
}

