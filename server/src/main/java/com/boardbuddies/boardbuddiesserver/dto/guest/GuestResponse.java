package com.boardbuddies.boardbuddiesserver.dto.guest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {

    private Long id;
    private String name;
    private String phoneNumber;
    private String school;
    private LocalDateTime createdAt;
    
    /**
     * 등록한 부원 ID
     */
    private Long registeredById;
    
    /**
     * 등록한 부원 이름
     */
    private String registeredByName;
}

