package com.boardbuddies.boardbuddiesserver.dto.crew;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CrewCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("예약 시간 포맷 검증 - HH:MM")
    void validateReservationTime_HHMM() {
        CrewCreateRequest request = new CrewCreateRequest(
                "Test Crew",
                1234,
                "Test Univ",
                "MONDAY",
                "20:00", // HH:MM
                null,
                20);

        Set<ConstraintViolation<CrewCreateRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("예약 시간 포맷 검증 - HH:MM:SS (이제 허용 안됨)")
    void validateReservationTime_HHMMSS() {
        CrewCreateRequest request = new CrewCreateRequest(
                "Test Crew",
                1234,
                "Test Univ",
                "MONDAY",
                "20:00:00", // HH:MM:SS
                null,
                20);

        Set<ConstraintViolation<CrewCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("예약 시간 형식이 올바르지 않습니다");
    }

    @Test
    @DisplayName("예약 시간 포맷 검증 - 실패 케이스")
    void validateReservationTime_Fail() {
        CrewCreateRequest request = new CrewCreateRequest(
                "Test Crew",
                1234,
                "Test Univ",
                "MONDAY",
                "25:00", // Invalid hour
                null,
                20);

        Set<ConstraintViolation<CrewCreateRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("예약 시간 형식이 올바르지 않습니다");
    }
}
