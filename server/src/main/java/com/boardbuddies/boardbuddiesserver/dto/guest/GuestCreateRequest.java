package com.boardbuddies.boardbuddiesserver.dto.guest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestCreateRequest {

    /**
     * 게스트 이름
     */
    private String name;

    /**
     * 게스트 전화번호
     */
    private String phoneNumber;
}

