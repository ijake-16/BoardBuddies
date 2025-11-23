package com.boardbuddies.boardbuddiesserver.dto.auth;

import com.boardbuddies.boardbuddiesserver.domain.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원가입 완료 요청 (추가 정보 입력)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupCompleteRequest {
    
    /**
     * 이름
     */
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    /**
     * 생년월일 (yyyy-MM-dd)
     */
    @NotNull(message = "생년월일은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    /**
     * 소속학교
     */
    @NotBlank(message = "소속학교는 필수입니다.")
    private String school;
    
    /**
     * 학번
     */
    @NotBlank(message = "학번은 필수입니다.")
    private String studentId;
    
    /**
     * 성별 (MALE, FEMALE)
     */
    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;
    
    /**
     * 전화번호 (010-1234-5678 형식)
     */
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-xxxx-xxxx)")
    private String phoneNumber;
}

