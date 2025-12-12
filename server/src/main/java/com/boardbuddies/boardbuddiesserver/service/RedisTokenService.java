package com.boardbuddies.boardbuddiesserver.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedissonClient redissonClient;

    /**
     * Refresh Token 저장
     * key: "refresh:{userId}"
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
        String key = "refresh:" + userId;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(Long userId) {
        String key = "refresh:" + userId;
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * Refresh Token 삭제 (로그아웃 등)
     */
    public void deleteRefreshToken(Long userId) {
        String key = "refresh:" + userId;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    /**
     * Access Token 블랙리스트 등록 (로그아웃)
     * key: "blacklist:{accessToken}"
     * value: "logout"
     */
    public void setBlackList(String accessToken, long expirationMillis) {
        String key = "blacklist:" + accessToken;
        if (expirationMillis > 0) {
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.set("logout", expirationMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Access Token 블랙리스트 여부 확인
     */
    public boolean isBlackListed(String accessToken) {
        String key = "blacklist:" + accessToken;
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.isExists();
    }
}
