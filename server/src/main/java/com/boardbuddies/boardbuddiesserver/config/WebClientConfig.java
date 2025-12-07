package com.boardbuddies.boardbuddiesserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 설정
 * 소셜 로그인 API 호출을 위한 WebClient를 설정합니다.
 */
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .build();
    }
}

